package clients

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.sqs.AmazonSQS

trait ClientProvider {
  val s3: AmazonS3
  val sqs: AmazonSQS
}
