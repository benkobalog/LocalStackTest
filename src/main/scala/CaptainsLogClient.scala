import clients.{ClientProvider, LocalClientProvider}
import com.typesafe.config.ConfigFactory
import s3.S3
import sqs.SQS

import scala.util.{Failure, Success}

object CaptainsLogClient {
  private val config = ConfigFactory.load()
  private val queueName = config.getString("queue.name")
  private val bucketName = config.getString("bucket.name")

  def main(args: Array[String]): Unit = {
    val awsResources: ClientProvider = LocalClientProvider

    args match {
      case Array("get", id) =>
        val s3 = new S3(awsResources.s3)
        s3.download(bucketName, id) match {
          case Success(log) => println(s"Here's log number $id: $log")
          case Failure(_)   => println("There's no log with id: " + id)
        }

      case Array("add", text) =>
        val sqsWrapper = new SQS(awsResources.sqs)
        for {
          queueName <- sqsWrapper.getQueueUrl(queueName)
          _ <- sqsWrapper.sendOne(queueName, text)
        } yield ()
        sqsWrapper.shutdown()

      case _ =>
        println("Invalid parameters")
    }
  }
}
