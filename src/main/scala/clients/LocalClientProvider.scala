package clients
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}

object LocalClientProvider extends ClientProvider {
  private val clientRegion = "us-east-2"
  private val sqsUrl = "http://localhost:4576"
  private val s3Url = "http://localhost:4572"

  override val s3: AmazonS3 = AmazonS3ClientBuilder
    .standard()
    .withCredentials(
      new AWSStaticCredentialsProvider(new BasicAWSCredentials("", "")))
    .withPathStyleAccessEnabled(true)
    .withEndpointConfiguration(new EndpointConfiguration(s3Url, clientRegion))
    .build()

  override val sqs: AmazonSQS = AmazonSQSClientBuilder
    .standard()
    .withEndpointConfiguration(new EndpointConfiguration(sqsUrl, clientRegion))
    .build()
}
