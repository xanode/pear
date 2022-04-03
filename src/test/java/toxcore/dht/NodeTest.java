package toxcore.dht;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Node class")
public class NodeTest {

    @Test
    @DisplayName("Test Node constructor")
    public void testConstructor() {
        byte[] randomInetAddress = new byte[4];
        Random rd = new Random();
        Node node = null;
        try {
            rd.nextBytes(randomInetAddress);
            randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address not to be a multicast address (RFC 5771)
            node = new Node(
                    SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                    InetAddress.getByAddress(randomInetAddress),
                    rd.nextInt(65536)
            );
        } catch (SodiumLibraryException | UnknownHostException ignored) {
        }
        assertNotEquals(node, null);
    }

    @Test
    @DisplayName("Test Node equals")
    public void testEquals() {
        byte[] randomInetAddress = new byte[4];
        Random rd = new Random();
        // Generate two identical nodes
        Node node1 = null;
        Node node2 = null;
        try {
            // Generate random values
            byte[] publicKey = SodiumLibrary.cryptoBoxKeyPair().getPublicKey();
            rd.nextBytes(randomInetAddress);
            randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address not to be a multicast address (RFC 5771)
            int port = rd.nextInt(65536);

            node1 = new Node(
                    publicKey,
                    InetAddress.getByAddress(randomInetAddress),
                    port
            );
            node2 = new Node(
                    publicKey,
                    InetAddress.getByAddress(randomInetAddress),
                    port
            );
        } catch (SodiumLibraryException | UnknownHostException ignored) {
        }
        // A node is equal to itself
        assertEquals(node1, node1);
        // Two identical nodes are equal
        assertEquals(node1, node2);
    }

    @Test
    @DisplayName("Test Node hashCode")
    public void testHashCode() {
        byte[] randomInetAddress = new byte[4];
        Random rd = new Random();
        Node node1 = null;
        Node node2 = null;
        try {
            rd.nextBytes(randomInetAddress);
            randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address not to be a multicast address (RFC 5771)
            node1 = new Node(
                    SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                    InetAddress.getByAddress(randomInetAddress),
                    rd.nextInt(65536)
            );
            rd.nextBytes(randomInetAddress);
            randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address not to be a multicast address (RFC 5771)
            node2 = new Node(
                    SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                    InetAddress.getByAddress(randomInetAddress),
                    rd.nextInt(65536)
            );
        } catch (SodiumLibraryException | UnknownHostException ignored) {
        }
        // The hashCode of a node stay the same
        assertEquals(node1.hashCode(), node1.hashCode());
        // Two different nodes have different hashCodes
        assertNotEquals(node1.hashCode(), node2.hashCode());
    }
}