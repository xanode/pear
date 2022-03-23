package toxcore.dht;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.SocketException;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DHT Test")
class DHTTest {

    @Test
    @DisplayName("Instanciating a DHT and closing it")
    void testDHT() throws SocketException, SodiumLibraryException {
        DHT dht = new DHT(34567);
        dht.close();
    }

    @Test
    @DisplayName("Return the correct port")
    void testGetPort() throws SocketException, SodiumLibraryException {
        DHT dht = new DHT(34567);
        assertEquals(34567, dht.getPort());
    }

}
