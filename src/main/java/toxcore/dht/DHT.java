package toxcore.dht;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.sun.jna.Platform;

import java.net.InetAddress;

public class DHT {

    // Constants
    // Crypto-related constants
    protected static final byte CRYPTO_PUBLIC_KEY_SIZE = 32;
    protected static final byte CRYPTO_PRIVATE_KEY_SIZE = 32;
    protected static final byte CRYPTO_NONCE_SIZE = 24;

    // Attributes
    private final String libraryPath;
    private final byte[] publicKey;
    private final byte[] privateKey;
    private Buckets buckets;

    public DHT() throws SodiumLibraryException {
        // Load libsodium library
        if (Platform.isWindows()) {
            this.libraryPath = "C:/libsodium/libsodium.dll";
        } else if (Platform.isMac()) {
            this.libraryPath = "/usr/local/lib/libsodium.dylib";
        } else {
            this.libraryPath = "/usr/lib/libsodium.so"; // TODO: the right place depend on the distro
        }
        SodiumLibrary.setLibraryPath(this.libraryPath);

        // Generate keys
        SodiumKeyPair keyPair = SodiumLibrary.cryptoBoxKeyPair();
        this.publicKey = keyPair.getPublicKey();
        this.privateKey = keyPair.getPrivateKey();

        // Initialize buckets
        // Create a node with our public key
        Node node = new Node(
                this.publicKey,
                InetAddress.getLoopbackAddress(),
                Network.PING_PORT
        );
        this.buckets = new Buckets(node, (int) Math.pow(2, CRYPTO_PUBLIC_KEY_SIZE)); // Size in Buckets is the number of bits in the public key
    }

    /**
     * Get the public key of the DHT
     * @return the public key of the DHT
     */
    public byte[] getPublicKey() {
        return this.publicKey;
    }

    /**
     * Generate a random nonce.
     * @return a random nonce
     */
    public byte[] generateNonce() { // Should be static?
        return SodiumLibrary.randomBytes(CRYPTO_NONCE_SIZE);
    }

    /**
     * Encrypt data with the public key of the DHT
     * @param data the data to encrypt
     * @return the encrypted data
     * @throws SodiumLibraryException in case of error
     */
    public byte[] encrypt(byte[] nonce, byte[] data) throws SodiumLibraryException {
        // Encrypt data and return it
        return SodiumLibrary.cryptoSecretBoxEasy(data, nonce, this.publicKey);
    }

    /**
     * Verify and decrypt data with the private key of the DHT
     * @param nonce the nonce used to encrypt the data
     * @param data the data to decrypt
     * @return the decrypted data
     * @throws SodiumLibraryException in case of error
     */
    public byte[] decrypt(byte[] nonce, byte[] data) throws SodiumLibraryException {
        // Decrypt data and return it
        return SodiumLibrary.cryptoSecretBoxOpenEasy(data, nonce, this.privateKey);
    }

}
