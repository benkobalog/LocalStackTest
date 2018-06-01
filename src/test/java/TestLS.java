import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.TestUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(value = LocalstackTestRunner.class)
public class TestLS {

    @Test
    public void testLocalS3API() {
        AmazonS3 s3 = TestUtils.getClientS3();
        List<Bucket> buckets = s3.listBuckets();

        System.out.println(buckets);
        assert(1 == 2);
    }

}