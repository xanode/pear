package toxcore.dht;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("Test ClientList class")
public class ClientListTest {
    Random rd = new Random();

    @Test
    @DisplayName("Instanciate a ClientList with IPv4 node")
    void testClientListIPv4() throws SodiumLibraryException, UnknownHostException {
        new ClientList(32, generateIPv4Node());
    }

    @Test
    @DisplayName("Instanciate a ClientList with IPv6 node")
    void testClientListIPv6() throws SodiumLibraryException, UnknownHostException {
        new ClientList(32, generateIPv6Node());
    }

    @Test
    @DisplayName("Add a node in the client list")
    void testAdd() throws SodiumLibraryException, UnknownHostException {
        ClientList clientList = new ClientList(32, generateIPv4Node());

        Node node = generateIPv4Node();

        assertEquals(clientList.add(node), true);
    }

    @Test
    @DisplayName("Remove a node from a client list")
    void testRemove() throws SodiumLibraryException, UnknownHostException {
        ClientList clientList = new ClientList(32, generateIPv4Node());
        Node node = generateIPv4Node();
        clientList.add(node);

        // Remove the key
        assertEquals(clientList.remove(node), true);

        // Try to remove it again
        assertEquals(clientList.remove(node), false);
    }

    @Test
    @DisplayName("Add a node in a full list")
    void testAddFullList() throws SodiumLibraryException, UnknownHostException {
        ClientList clientList = new ClientList(32, generateIPv4Node());

        // Fill the list
        for (int i = 0; i < 32; i++) {
            clientList.add(generateIPv4Node());
        }

        // Try to add a new key
        assertEquals(clientList.add(generateIPv4Node()), false);
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

    Node generateIPv6Node() throws SodiumLibraryException, UnknownHostException {
        byte[] randomInetAddress = new byte[Network.SIZE_IP6];
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
}
