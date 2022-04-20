package toxcore.dht;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Ping Test")
class PingTest {
    Random rd = new Random();

    @Test
    @DisplayName("Instanciate a Ping with IPv4 addresses")
    void testPingIPv4() throws SodiumLibraryException, UnknownHostException {
        Node node = generateIPv4Node();
        byte[] pingId = generateRandomId();

        Ping ping = new Ping(node, pingId);

        assertEquals(ping.getNode(), node);
        assertEquals(ping.getPingId(), pingId);
    }

    // ============================================================
    // HELPERS
    // ============================================================
    Node generateIPv4Node() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[Network.SIZE_IP4];
        Node node = null;

        rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address not to be a multicast address

        node = new Node(
                new DHT(), // Necessary to initialize the Sodium library
                SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                InetAddress.getByAddress(randomInetAddress),
                rd.nextInt(65536));

        return node;
    }

    byte[] generateRandomId() {
        byte[] randomId = new byte[Network.PING_ID_LENGTH];
        rd.nextBytes(randomId);

        return randomId;
    }

}
