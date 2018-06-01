package clients
import cloud.localstack.TestUtils
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.sqs.AmazonSQS

object TestClientProvider extends ClientProvider {
  override val s3: AmazonS3 = TestUtils.getClientS3
  override val sqs: AmazonSQS = TestUtils.getClientSQS
}
