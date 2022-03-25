package toxcore.dht;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test ClientList class")
public class ClientListTest {

    @Test
    @DisplayName("Instanciate a ClientList")
    void testClientList() {
        new ClientList(32, getRandomKey());
    }

    @Test
    @DisplayName("Add a public key in the client list")
    void testAdd() {
        ClientList clientList = new ClientList(32, getRandomKey());

        byte[] key = getRandomKey();

        assertEquals(clientList.add(key), true);
    }

    @Test
    @DisplayName("Remove a public key in the client list")
    void testRemove() {
        ClientList clientList = new ClientList(32, getRandomKey());
        byte[] key = getRandomKey();
        clientList.add(key);

        // Remove the key
        assertEquals(clientList.remove(key), true);

        // Try to remove it again
        assertEquals(clientList.remove(key), false);
    }

    @Test
    @DisplayName("Add a client in a full list")
    void testAddFullList() {
        ClientList clientList = new ClientList(32, getRandomKey());
        byte[] key = new byte[32];
        Random rd = new Random();

        // Fill the list
        for (int i = 0; i < 32; i++) {
            rd.nextBytes(key);
            clientList.add(key);
        }

        // Try to add a new key
        rd.nextBytes(key);
        assertEquals(clientList.add(key), false);
    }

    /**
     * Get a random key.
     *
     * @return A random key.
     */
    static byte[] getRandomKey() {
        byte[] key = new byte[32];
        Random rd = new Random();
        rd.nextBytes(key);
        return key;
    }
}
