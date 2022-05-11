package toxcore.dht;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test KBucket class")
public class KBucketTest {

    @Test
    @DisplayName("Test KBucket constructor")
    public void testConstructor() {
        Random rand = new Random();
        int length = rand.nextInt(50) + 1;
        KBucket kbucket = new KBucket(length);
        assertEquals(0, kbucket.getSize());
        assertEquals(length, kbucket.getK());
    }


}