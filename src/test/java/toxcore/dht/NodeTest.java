package toxcore.dht;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Node class")
public class NodeTest {

    Random rd = new Random();

    @Test
    @DisplayName("Instanciate a Node with IPv4 non-multicast address")
    public void testConstructorWithIPv4Address() throws SodiumLibraryException, UnknownHostException {
        Node node = generateIPv4Node("non-multicast");
        assertNotNull(node);
    }

    @Test
    @DisplayName("Instanciate a Node with IPv4 multicast address")
    public void testConstructorWithMulticastIPv4Address() throws SodiumLibraryException, UnknownHostException {
        try {
            generateIPv4Node("multicast");
        } catch (IllegalArgumentException e) {
            // This is expected
            assertEquals(e.getMessage(), "Multicast address not allowed");
        }
    }

    @Test
    @DisplayName("Instanciate a Node with IPv6 address (non-multicast)")
    public void testConstructorWithIPv6Address() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[16];
        rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (rd.nextInt(240) + 1); // Force the address to not be multicast (RFC 5771)

        Node node = new Node(
                new DHT(),
                SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                InetAddress.getByAddress(randomInetAddress),
                rd.nextInt(65536));

        assertNotNull(node);
    }

    @Test
    @DisplayName("Instanciate a Node with IPv6 multicast address")
    public void testConstructorWithMulticastIPv6Address() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[16];
        this.rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (this.rd.nextInt(240) + 15); // Force the address to be multicast (RFC 5771)

        try {
            new Node(
                    new DHT(),
                    SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                    InetAddress.getByAddress(randomInetAddress),
                    this.rd.nextInt(65536));

        } catch (IllegalArgumentException e) {
            // This is expected
            assertEquals(e.getMessage(), "Multicast address not allowed");
        }
    }

    @Test
    @DisplayName("Equals method: same node object")
    public void testEqualsSameNode() throws SodiumLibraryException, UnknownHostException {
        Node node = generateIPv4Node("non-multicast");

        assertEquals(node, node);
    }

    @Test
    @DisplayName("Equals method: different objects with same parameters")
    public void testEqualsDifferentNodesSameParameters() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[4];

        // Generate random values
        byte[] publicKey = SodiumLibrary.cryptoBoxKeyPair().getPublicKey();
        this.rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (this.rd.nextInt(224) + 1); // Force the address to not be multicast (RFC 5771)
        int port = this.rd.nextInt(65536);
        DHT dht = new DHT(); // Necessary to initialize the Sodium library

        Node node1 = new Node(
                dht,
                publicKey,
                InetAddress.getByAddress(randomInetAddress),
                port);

        Node node2 = new Node(
                dht,
                publicKey,
                InetAddress.getByAddress(randomInetAddress),
                port);

        assertEquals(node1, node2);
    }

    @Test
    @DisplayName("Equals method: different objects with different parameters")
    public void testEqualsDifferentNodes() throws SodiumLibraryException, UnknownHostException {
        Node node1 = generateIPv4Node("non-multicast");
        Node node2 = generateIPv4Node("non-multicast");

        assertNotEquals(node1, node2);
    }

    @Test
    @DisplayName("hashCode method: same node")
    public void testHashCodeSameNode() throws SodiumLibraryException, UnknownHostException {
        Node node = generateIPv4Node("non-multicast");

        assertEquals(node.hashCode(), node.hashCode());
    }

    @Test
    @DisplayName("hashCode method: different nodes with same parameters")
    public void testHashCodeDifferentNodesSameParameters() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[4];

        // Generate random values
        DHT dht = new DHT(); // Necessary to initialize the Sodium library
        byte[] publicKey = SodiumLibrary.cryptoBoxKeyPair().getPublicKey();
        this.rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (this.rd.nextInt(224) + 1); // Force the address to not be multicast (RFC 5771)
        int port = this.rd.nextInt(65536);

        Node node1 = new Node(
                dht,
                publicKey,
                InetAddress.getByAddress(randomInetAddress),
                port);

        Node node2 = new Node(
                dht,
                publicKey,
                InetAddress.getByAddress(randomInetAddress),
                port);

        assertEquals(node1.hashCode(), node2.hashCode());
    }

    @Test
    @DisplayName("hashCode method: different nodes with different parameters")
    public void testHashCodeDifferentNodes() throws SodiumLibraryException, UnknownHostException {
        Node node1 = generateIPv4Node("non-multicast");
        Node node2 = generateIPv4Node("non-multicast");

        assertNotEquals(node1.hashCode(), node2.hashCode());
    }

    /**
     * Generates a random IPv4 node.
     * @param addressType The address type.
     * @return a random IPv4 node
     * @throws SodiumLibraryException if the Sodium library is not initialized
     * @throws UnknownHostException if the random address is not valid
     */
    public static Node generateIPv4Node(String addressType) throws SodiumLibraryException, UnknownHostException {
        Random rd = new Random();
        byte[] randomInetAddress = new byte[4];
        rd.nextBytes(randomInetAddress);

        if (Objects.equals(addressType, "multicast")) {
            randomInetAddress[0] = (byte) (rd.nextInt(224) + 1);
        } else if (Objects.equals(addressType, "non-multicast")) {
            randomInetAddress[0] = (byte) (rd.nextInt(240) + 1);
        }

        return new Node(
                new DHT(),
                SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                InetAddress.getByAddress(randomInetAddress),
                rd.nextInt(65536));
    }
}
