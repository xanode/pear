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

    @Test
    @DisplayName("Instanciate a ClientList")
    void testClientList() {
        new ClientList(32, getRandomNode());
    }

    @Test
    @DisplayName("Add a node in the client list")
    void testAdd() {
        ClientList clientList = new ClientList(32, getRandomNode());

        Node node = getRandomNode();

        assertEquals(clientList.add(node), true);
    }

    @Test
    @DisplayName("Remove a public key in the client list")
    void testRemove() {
        ClientList clientList = new ClientList(32, getRandomNode());
        Node node = getRandomNode();
        clientList.add(node);

        // Remove the key
        assertEquals(clientList.remove(node), true);

        // Try to remove it again
        assertEquals(clientList.remove(node), false);
    }

    @Test
    @DisplayName("Add a client in a full list")
    void testAddFullList() {
        ClientList clientList = new ClientList(32, getRandomNode());

        // Fill the list
        for (int i = 0; i < 32; i++) {
            if (clientList.add(getRandomNode()) == true) {
            }
        }

        // Try to add a new key
        assertEquals(clientList.add(getRandomNode()), false);
    }

    /**
     * Get a random key.
     *
     * @return A random key.
     */
    static Node getRandomNode() {
        byte[] randomInetAddress = new byte[4];
        Random rd = new Random();
        rd.nextBytes(randomInetAddress);
        randomInetAddress[0] = (byte) (rd.nextInt(224) + 1); // Force the address not to be a multicast address (RFC 5771)
        try {
            Node node = new Node(
                    SodiumLibrary.cryptoBoxKeyPair().getPublicKey(),
                    InetAddress.getByAddress(randomInetAddress),
                    rd.nextInt(65536)
            );
            return node;
        } catch (SodiumLibraryException | UnknownHostException ignored) {
            // Can't happen
        }
        return null;
    }
}
