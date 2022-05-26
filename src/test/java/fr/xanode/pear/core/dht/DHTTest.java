package fr.xanode.pear.core.dht;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import fr.xanode.pear.core.dht.DHT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DHT Test")
class DHTTest {

    @Test
    @DisplayName("Instanciating a DHT")
    void testDHT() throws SodiumLibraryException {
        DHT dht = new DHT();
        assertEquals(dht.getNetwork().dht, dht);
    }

    @Test
    @DisplayName("Test that the public key is the right size.") // Stupid test, but it's a start.
    void testPublicKeySize() throws SodiumLibraryException {
        DHT dht = new DHT();
        assertEquals(dht.getPublicKey().length, DHT.CRYPTO_PUBLIC_KEY_SIZE);
    }

    @Test
    @DisplayName("Test that a generated nonce is the right size.") // Stupid test, but it's a start.
    void testNonceSize() throws SodiumLibraryException {
        DHT dht = new DHT();
        assertEquals((byte) SodiumLibrary.cryptoBoxNonceBytes().intValue(), DHT.CRYPTO_NONCE_SIZE); // This is necessary for encryption
        assertEquals(dht.generateNonce().length, DHT.CRYPTO_NONCE_SIZE);
    }

    @Test
    @DisplayName("Test that the encryption is working")
    void testEncryption() throws SodiumLibraryException {
        DHT sender = new DHT();
        DHT receiver = new DHT();
        byte[] nonce = sender.generateNonce();
        byte[] plaintext = "Hello World".getBytes();
        byte[] ciphertext = sender.encrypt(receiver.getPublicKey(), nonce, plaintext);
        byte[] decrypted = receiver.decrypt(sender.getPublicKey(), nonce, ciphertext);
        assertEquals(new String(plaintext), new String(decrypted));
    }


}
