import clients.{AWSClientProvider, ClientProvider, LocalClientProvider}
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectResult
import com.amazonaws.services.sqs.model.Message
import s3.S3
import sqs.SQS

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import com.typesafe.config.ConfigFactory

object CaptainsLogServer {
  @volatile private var shutdown = false
  private val config = ConfigFactory.load()
  private val queueName = config.getString("queue.name")
  private val bucketName = config.getString("bucket.name")
  private val isLocalStack = config.getBoolean("localstack")
  private val awsResources: ClientProvider =
    if (isLocalStack) LocalClientProvider else AWSClientProvider

  println("Starting with config " + awsResources)

  def main(args: Array[String]): Unit = {
    val sqsWrapper = new SQS(awsResources.sqs)
    val s3 = awsResources.s3

    prepareBucket(s3)

    sys.addShutdownHook {
      shutdown = true
      sqsWrapper.shutdown()
      s3.shutdown()
      println("Shutting down")
    }

    checkMessages(s3, sqsWrapper, queueName)
  }

  @tailrec
  def checkMessages(s3: AmazonS3, sqs: SQS, queueName: String): Try[Int] = {
    val pollWaitTime = 2.second

    val newId = for {
      queueUrl <- sqs.getOrCreateQueueUrl(queueName, pollWaitTime)
      messages <- sqs.receive(queueUrl, None)
      lastId <- getLastId(s3)
      newId <- messagesToS3(s3, lastId, messages)
      _ <- setLastId(s3, newId)
    } yield newId

    if (shutdown) newId
    else
      newId match {
        case Success(nId) =>
          println("New ID: " + nId)
          checkMessages(s3, sqs, queueName)

        case Failure(throwable) =>
          println("Error: " + throwable.getMessage)
          checkMessages(s3, sqs, queueName)
      }
  }

  def messagesToS3(s3: AmazonS3, id: Int, messages: List[Message]): Try[Int] =
    Try {
      messages.foreach { msg =>
        s3.putObject(bucketName, id.toString, msg.getBody)
        println(s"""Uploaded message: "${msg.getBody}" with id: $id""")
      }
      id + messages.length
    }

  def getLastId(s3: AmazonS3): Try[Int] =
    Try(s3.getObjectAsString(bucketName, "lastId"))
      .map(_.toInt)
      .recover {
        case err =>
          println("Error: " + err.getMessage + "\nCreating lastId file.")
          s3.putObject(bucketName, "lastId", "0")
          0
      }

  def setLastId(s3: AmazonS3, currentId: Int): Try[PutObjectResult] = {
    Try {
      s3.putObject(bucketName, "lastId", currentId.toString)
    }
  }

  def logTime[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) / 1e9 + " s")
    result
  }

  private def prepareBucket(s3: AmazonS3): Unit = {
    new S3(s3).createBucket(bucketName)
  }

}
