package toxcore.dht;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.SocketException;
import java.util.Random;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DHT Test")
class DHTTest {

    @Test
    @DisplayName("Instanciating a DHT and closing it")
    void testDHT() throws SocketException, SodiumLibraryException {
        Random r = new Random();
        int port = r.nextInt(65535 - 1024) + 1024;
        DHT dht = new DHT(port);
        dht.close();
    }

    @Test
    @DisplayName("Return the correct port")
    void testGetPort() throws SocketException, SodiumLibraryException {
        Random r = new Random();
        int port = r.nextInt(65535 - 1024) + 1024;
        DHT dht = new DHT(port);
        assertEquals(port, dht.getPort());
    }

    @Test
    @DisplayName("Get keys")
    void testGetKeys() throws SocketException, SodiumLibraryException {
        Random r = new Random();
        int port = r.nextInt(65535 - 1024) + 1024;
        DHT dht = new DHT(port);
        assertEquals(32, dht.getPublicKey().length);
        assertEquals(32, dht.getPrivateKey().length);
    }

}
