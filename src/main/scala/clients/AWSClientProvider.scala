package clients
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}

object AWSClientProvider extends ClientProvider {
  private val clientRegion = "us-east-2"

  override val s3: AmazonS3 = AmazonS3ClientBuilder
    .standard()
    .withRegion(clientRegion)
    .withCredentials(new ProfileCredentialsProvider())
    .build()

  override val sqs: AmazonSQS = AmazonSQSClientBuilder.defaultClient()
}
