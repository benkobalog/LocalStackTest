package s3

import java.io.File

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model._

import scala.util.Try

class S3Uploader(s3Client: AmazonS3) {

  def upload(bucketName: String,
             stringObjKeyName: String,
             fileObjKeyName: String,
             fileName: String) = Try {
    val request =
      new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName))
    val metadata = new ObjectMetadata()
    metadata.setContentType("plain/text")
    request.setMetadata(metadata)
    s3Client.putObject(request)
  }

  def createBucket(bucketName: String): Unit =
    if (!s3Client.doesBucketExistV2(bucketName)) {
      s3Client.createBucket(new CreateBucketRequest(bucketName))

      val bucketLocation =
        s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName))
      println("Created bucket Location: " + bucketLocation)
    }
}
