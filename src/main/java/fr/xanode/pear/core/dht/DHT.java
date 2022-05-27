package fr.xanode.pear.core.dht;

import java.net.InetAddress;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.sun.jna.Platform;
import fr.xanode.pear.core.dht.buckets.Buckets;
import fr.xanode.pear.core.dht.network.Network;
import fr.xanode.pear.core.dht.network.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DHT {

    // Constants
    // Crypto-related constants
    public static final byte CRYPTO_KEY_SIZE = 32;
    public static final byte CRYPTO_NONCE_SIZE = 24;

    @Getter private final byte[] publicKey;
    private final byte[] privateKey;
    private final Buckets buckets;
    @Getter private final Network network;

    public DHT() throws SodiumLibraryException {
        // Load libsodium library
        log.info("Loading libsodium library...");
        // Attributes
        String libraryPath;
        if (Platform.isWindows()) {
            libraryPath = "C:/libsodium/libsodium.dll";
        } else if (Platform.isMac()) {
            libraryPath = "/usr/local/lib/libsodium.dylib";
        } else {
            libraryPath = "/usr/lib64/libsodium.so.23"; // TODO: the right place depend on the distro
        }
        SodiumLibrary.setLibraryPath(libraryPath);
        log.info("libsodium library loaded.");

        // Generate keys
        log.info("Generating keys...");
        SodiumKeyPair keyPair = SodiumLibrary.cryptoBoxKeyPair();
        this.publicKey = keyPair.getPublicKey();
        this.privateKey = keyPair.getPrivateKey();
        log.info("Key pair generated.");

        // Initialize buckets
        log.info("Initialize buckets...");
        // Create a node with our public key
        log.info("Creating a homomorphic node...");
        Node node = new Node(
                this,
                this.publicKey,
                InetAddress.getLoopbackAddress(),
                Network.DHT_PORT
        );
        log.info("Homomorphic node created.");
        this.buckets = new Buckets(node, 8 * CRYPTO_KEY_SIZE); // Size in Buckets is the number of bits in the public key
        log.info("Buckets initialized.");

        // Initialize network
        log.info("Initialize network...");
        this.network = new Network(this);
        log.info("Network initialized.");
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

    /**
     * Returns if a node is in our buckets.
     * @param node the node to check
     * @return true if the node is in our buckets, false otherwise
     */
    public Node isNodeKnown(Node node) {
        return this.buckets.contains(node);
    }

    /**
     * Returns if a node will be inserted in our buckets.
     * @param node the node to check
     * @return true if the node will be inserted in our buckets, false otherwise
     */
    public boolean isInsertable(Node node) {
        return this.buckets.isInsertable(node);
    }

    /**
     * Add a node in the buckets.
     * @param node The node to add.
     */
    public void addNode(Node node) {
        this.buckets.update(node);
    }
}
