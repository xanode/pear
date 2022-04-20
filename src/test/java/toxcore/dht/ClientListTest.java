package toxcore.dht;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        new ClientList(ClientList.CLIENT_LIST_SIZE, generateIPv4Node());
    }

    @Test
    @DisplayName("Instanciate a ClientList with IPv6 node")
    void testClientListIPv6() throws SodiumLibraryException, UnknownHostException {
        new ClientList(ClientList.CLIENT_LIST_SIZE, generateIPv6Node());
    }

    @Test
    @DisplayName("Add nodes in the client list")
    void testAdd() throws SodiumLibraryException, UnknownHostException {
        ClientList clientList = new ClientList(ClientList.CLIENT_LIST_SIZE, generateIPv4Node());

        Node node4 = generateIPv4Node();
        Node node6 = generateIPv6Node();

        assertTrue(clientList.add(node4));
        assertTrue(clientList.add(node6));
    }

    @Test
    @DisplayName("Remove nodes from a client list")
    void testRemove() throws SodiumLibraryException, UnknownHostException {
        ClientList clientList = new ClientList(ClientList.CLIENT_LIST_SIZE, generateIPv4Node());

        // Generate nodes and add them
        Node node4 = generateIPv4Node();
        Node node6 = generateIPv4Node();
        clientList.add(node4);
        clientList.add(node6);

        // Remove nodes
        assertTrue(clientList.remove(node4));
        assertTrue(clientList.remove(node6));

        // Try again
        assertFalse(clientList.remove(node4));
        assertFalse(clientList.remove(node6));
    }

    @Test
    @DisplayName("Add nodes to a full list")
    void testAddFullList() throws SodiumLibraryException, UnknownHostException {
        ClientList clientList = new ClientList(ClientList.CLIENT_LIST_SIZE, generateIPv4Node());

        // Fill the list
        for (int i = 0; i < ClientList.CLIENT_LIST_SIZE; i++) {
            clientList.add(generateIPv4Node());
        }

        // Try to add new nodes
        assertFalse(clientList.add(generateIPv4Node()));
        assertFalse(clientList.add(generateIPv6Node()));
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
