package s3

import java.io.{BufferedReader, InputStreamReader}

import com.amazonaws.services.s3.AmazonS3

import scala.util.Try

class S3Downloader(s3: AmazonS3) {
  def download(bucketName: String, key: String) = Try {
    val objectInputStream =
      s3.getObject(bucketName: String, key: String).getObjectContent
    val reader = new BufferedReader(new InputStreamReader(objectInputStream))
    val result =
      Stream.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
    reader.close()
    objectInputStream.close()
    result
  }
}
