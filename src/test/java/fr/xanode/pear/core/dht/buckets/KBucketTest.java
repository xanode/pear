package fr.xanode.pear.core.dht.buckets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.UnknownHostException;
import java.util.Random;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import fr.xanode.pear.core.dht.network.NodeTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test KBucket class")
public class KBucketTest {

    @Test
    @DisplayName("Test KBucket constructor")
    public void testConstructor() {
        Random rand = new Random();
        int length = rand.nextInt(50) + 1;
        KBucket kBucket = new KBucket(length);
        assertEquals(0, kBucket.getSize());
        assertEquals(length, kBucket.getK());
    }

    @Test
    @Disabled("This test needs networking ability")
    @DisplayName("Test KBucket update")
    public void testUpdate() throws UnknownHostException, SodiumLibraryException {
        Random rand = new Random();
        KBucket kBucket = new KBucket(rand.nextInt(50) + 1);
        int i = 0;
        kBucket.update(NodeTest.generateIPv4Node(false));
    }
}