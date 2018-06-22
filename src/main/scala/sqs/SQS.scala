package sqs

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model._

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.util.Try

class SQS(sqs: AmazonSQS) {

  def getOrCreateQueueUrl(queueName: String,
                          pollWaitTime: Duration): Try[String] =
    getQueueUrl(queueName).recoverWith {
      case _ =>
        createQueue(queueName, pollWaitTime)
          .flatMap(_ => getQueueUrl(queueName))
    }

  private def createQueue(queueName: String, pollWaitTime: Duration) =
    Try {
      val queueRequest = new CreateQueueRequest()
        .withQueueName(queueName)
        .addAttributesEntry("ReceiveMessageWaitTimeSeconds",
                            pollWaitTime.toSeconds.toString)
      println("Creating queue: " + queueName)
      sqs.createQueue(queueRequest)
    }

  def getQueueUrl(queueName: String) =
    Try {
      sqs.getQueueUrl(queueName).getQueueUrl
    }

  def sendOne(queueUrl: String, message: String): Try[SendMessageResult] =
    Try {
      sqs.sendMessage(queueUrl, message)
    }

  def receive(queueUrl: String,
              pollWaitTime: Option[Duration]): Try[List[Message]] =
    Try {
      val receiveRequest = pollWaitTime match {
        case Some(wt) =>
          new ReceiveMessageRequest(queueUrl)
            .withWaitTimeSeconds(wt.toSeconds.toInt)
        case None => new ReceiveMessageRequest(queueUrl)
      }

      val messages =
        sqs.receiveMessage(receiveRequest).getMessages.asScala.toList

      messages.foreach(m => sqs.deleteMessage(queueUrl, m.getReceiptHandle))
      messages
    }

  def shutdown(): Unit = sqs.shutdown()
}
