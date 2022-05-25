package toxcore.dht.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toxcore.dht.DHT;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("Instanciate a Ping with IPv6 addresses")
    void testPingIPv6() throws SodiumLibraryException, UnknownHostException {
        Node node = generateIPv6Node();
        byte[] pingId = generateRandomId();
        Ping ping = new Ping(node, pingId);

        assertEquals(ping.getNode(), node);
        assertEquals(ping.getPingId(), pingId);
    }

    @Test
    @DisplayName("Equals method: same object")
    void testEqualsSameObject() throws SodiumLibraryException, UnknownHostException {
        Node node = generateIPv4Node();
        byte[] pingId = generateRandomId();
        Ping ping = new Ping(node, pingId);

        assertEquals(ping, ping);
    }

    @Test
    @DisplayName("Equals method: same node and ping id")
    void testEqualsSameNodeAndId() throws SodiumLibraryException, UnknownHostException {
        Node node = generateIPv4Node();
        byte[] pingId = generateRandomId();

        Ping ping1 = new Ping(node, pingId);
        Ping ping2 = new Ping(node, pingId);

        assertEquals(ping1, ping2);
    }

    @Test
    @DisplayName("Equals method: same node, different ping id")
    void testEqualsSameNodeDifferentPingId() throws SodiumLibraryException, UnknownHostException {
        Node node = generateIPv4Node();

        Ping ping1 = new Ping(node, generateRandomId());
        Ping ping2 = new Ping(node, generateRandomId());

        assertNotEquals(ping1, ping2);
    }

    @Test
    @DisplayName("Equals method: different node, same ping id")
    void testEqualsDifferentNodeSamePingId() throws SodiumLibraryException, UnknownHostException {
        byte[] pingId = generateRandomId();

        Ping ping1 = new Ping(generateIPv4Node(), pingId);
        Ping ping2 = new Ping(generateIPv4Node(), pingId);

        assertNotEquals(ping1, ping2);
    }

    @Test
    @DisplayName("Equals method: different node, different ping id")
    void testEqualsDifferentNodeDifferentPingId() throws SodiumLibraryException, UnknownHostException {
        Ping ping1 = new Ping(generateIPv4Node(), generateRandomId());
        Ping ping2 = new Ping(generateIPv4Node(), generateRandomId());

        assertNotEquals(ping1, ping2);
    }

    // ============================================================
    // HELPERS
    // ============================================================
    Node generateIPv4Node() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[Network.SIZE_IP4];
        Node node;

        rd.nextBytes(randomInetAddress);
        while (InetAddress.getByAddress(randomInetAddress).isMulticastAddress()) { // Force the address not to be a multicast address
            rd.nextBytes(randomInetAddress);
        }

        node = new Node(
                new DHT(), // Necessary to initialize the Sodium library
                SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                InetAddress.getByAddress(randomInetAddress),
                rd.nextInt(65536));

        return node;
    }

    Node generateIPv6Node() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[Network.SIZE_IP6];
        Node node;

        rd.nextBytes(randomInetAddress);
        while (InetAddress.getByAddress(randomInetAddress).isMulticastAddress()) { // Force the address not to be a multicast address
            rd.nextBytes(randomInetAddress);
        }

        node = new Node(
                new DHT(), // Necessary to initialize the Sodium library
                SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                InetAddress.getByAddress(randomInetAddress),
                rd.nextInt(65536));

        return node;
    }

    byte[] generateRandomId() {
        byte[] randomId = new byte[Network.ID_LENGTH];
        rd.nextBytes(randomId);

        return randomId;
    }

}
