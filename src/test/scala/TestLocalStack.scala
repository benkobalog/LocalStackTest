import cloud.localstack.LocalstackTestRunner
import cloud.localstack.TestUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite

@RunWith(classOf[LocalstackTestRunner])
class TestLocalStack extends JUnitSuite {

  @Test
  def testLocalS3API(): Unit = {
    val s3 = TestUtils.getClientS3
    val buckets = s3.listBuckets()

    println(buckets)
    s3.createBucket("bucket12334234")
    assert(1 == 1)
  }
}
