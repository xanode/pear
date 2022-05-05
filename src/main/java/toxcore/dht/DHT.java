package toxcore.dht;

import java.net.InetAddress;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.sun.jna.Platform;

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
    private Network network;

    public DHT() throws SodiumLibraryException {
        // Load libsodium library
        if (Platform.isWindows()) {
            this.libraryPath = "C:/libsodium/libsodium.dll";
        } else if (Platform.isMac()) {
            this.libraryPath = "/usr/local/lib/libsodium.dylib";
        } else {
            this.libraryPath = "/usr/lib64/libsodium.so.23"; // TODO: the right place depend on the distro
        }
        SodiumLibrary.setLibraryPath(this.libraryPath);

        // Generate keys
        SodiumKeyPair keyPair = SodiumLibrary.cryptoBoxKeyPair();
        this.publicKey = keyPair.getPublicKey();
        this.privateKey = keyPair.getPrivateKey();

        // Initialize buckets
        // Create a node with our public key
        Node node = new Node(
                this,
                this.publicKey,
                InetAddress.getLoopbackAddress(),
                Network.PING_PORT
        );
        this.buckets = new Buckets(node, 8 * CRYPTO_PUBLIC_KEY_SIZE); // Size in Buckets is the number of bits in the public key

        // Initialize network
        this.network = new Network(this);
    }

    /**
     * Get the public key of the DHT
     * @return the public key of the DHT
     */
    public byte[] getPublicKey() {
        return this.publicKey;
    }

    /**
     * Get the network instance of the DHT
     * @return the network instance of the DHT
     */
    public Network getNetwork() {
        return this.network;
    }

    /**
     * Generate a random nonce.
     * @return a random nonce
     */
    public byte[] generateNonce() { // Should be static?
        return SodiumLibrary.randomBytes(CRYPTO_NONCE_SIZE);
    }

    /**
     * Encrypt data with the public key of the receiver and sign it with the private key of the DHT
     * @param receiverPublicKey the public key of the receiver
     * @param nonce the nonce used to encrypt the data
     * @param data the data to encrypt
     * @return the encrypted data
     * @throws SodiumLibraryException in case of error
     */
    public byte[] encrypt(byte[] receiverPublicKey, byte[] nonce, byte[] data) throws SodiumLibraryException {
        // Encrypt data and return it
        return SodiumLibrary.cryptoBoxEasy(data, nonce, receiverPublicKey, this.privateKey);
    }

    /**
     * Verify data with the receiver public key and decrypt it with the private key of the DHT
     * @param senderPublicKey the public key of the sender of the data
     * @param nonce the nonce used to encrypt the data
     * @param data the data to decrypt
     * @return the decrypted data
     * @throws SodiumLibraryException in case of error
     */
    public byte[] decrypt(byte[] senderPublicKey, byte[] nonce, byte[] data) throws SodiumLibraryException {
        // Decrypt data and return it
        return SodiumLibrary.cryptoBoxOpenEasy(data, nonce, senderPublicKey, this.privateKey);
    }

}
