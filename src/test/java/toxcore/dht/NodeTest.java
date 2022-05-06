package toxcore.dht;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Node class")
public class NodeTest {
    Random rd = new Random();

    @Test
    @DisplayName("Instanciate a Node with IPv4 address (non-multicast)")
    public void testConstructorWithIPv4Address() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[4];
        rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address to not be multicast (RFC 5771)

        Node node = new Node(
                new DHT(), // Necessary to initialize the Sodium library
                SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                InetAddress.getByAddress(randomInetAddress),
                rd.nextInt(65536));

        assertNotNull(node);
    }

    @Test
    @DisplayName("Instanciate a Node with IPv4 multicast address")
    public void testConstructorWithMulticastIPv4Address() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[4];
        rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) 224; // Force the address to be multicast (RFC 5771)

        try {
            new Node(
                    new DHT(), // Necessary to initialize the Sodium library
                    SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                    InetAddress.getByAddress(randomInetAddress),
                    rd.nextInt(65536));
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
        rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (rd.nextInt(240) + 15); // Force the address to be multicast (RFC 5771)

        try {
            new Node(
                    new DHT(),
                    SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                    InetAddress.getByAddress(randomInetAddress),
                    rd.nextInt(65536));

        } catch (IllegalArgumentException e) {
            // This is expected
            assertEquals(e.getMessage(), "Multicast address not allowed");
        }
    }

    @Test
    @DisplayName("Equals method: same node object")
    public void testEqualsSameNode() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[4]; // TODO: test with IPv6 addresses too if necessary
        rd.nextBytes(randomInetAddress);

        Node node = new Node(
                new DHT(), // Necessary to initialize the Sodium library
                SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                InetAddress.getByAddress(randomInetAddress),
                rd.nextInt(65536));

        assertTrue(node.equals(node));
    }

    @Test
    @DisplayName("Equals method: different objects with same parameters")
    public void testEqualsDifferentNodesSameParameters() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[4];

        // Generate random values
        byte[] publicKey = SodiumLibrary.cryptoBoxKeyPair().getPublicKey();
        rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address to not be multicast (RFC 5771)
        int port = rd.nextInt(65536);
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

        assertTrue(node1.equals(node2));
    }

    @Test
    @DisplayName("Test Node hashCode")
    public void testHashCode() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[4];

        rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address to not be multicast (RFC 5771)
        Node node1 = new Node(
                new DHT(), // Necessary to initialize the Sodium library
                SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                InetAddress.getByAddress(randomInetAddress),
                rd.nextInt(65536));

        rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address to not be multicast (RFC 5771)
        Node node2 = new Node(
                new DHT(), // Necessary to initialize the Sodium library
                SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                InetAddress.getByAddress(randomInetAddress),
                rd.nextInt(65536));

        // The hashCode of a node stay the same
        assertEquals(node1.hashCode(), node1.hashCode());
        // Two different nodes have different hashCodes
        assertNotEquals(node1.hashCode(), node2.hashCode());
    }
}
