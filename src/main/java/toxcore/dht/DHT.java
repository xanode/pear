package toxcore.dht;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.sun.jna.Platform;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class DHT implements Runnable {
    private final String libraryPath;
    final private byte[] publicKey;
    final private byte[] privateKey;
    final private int port;
    private DatagramSocket socket;
    private ConcurrentHashMap<byte[], InetAddress> nodes;
    private byte[] buffer;
    private ArrayList<Thread> servicingThread;
    private boolean running = true;
    public DHT(int port) throws SodiumLibraryException, SocketException {
        // Load libsodium library first in order to generate public and private key
        if (Platform.isWindows()) {
            this.libraryPath = "C:/libsodium/libsodium.dll";
        } else if (Platform.isMac()) {
            this.libraryPath = "/usr/local/lib/libsodium.dylib";
        } else {
            this.libraryPath = "/usr/local/lib/libsodium.so";
        }
        SodiumLibrary.setLibraryPath(this.libraryPath);

        // Then generate keys
        SodiumKeyPair keyPair = SodiumLibrary.cryptoBoxKeyPair();
        this.publicKey = keyPair.getPublicKey();
        this.privateKey = keyPair.getPrivateKey(); // Should be loaded from file in real world, see Tox Doc

        // Initialize network parameters
        this.port = port;
        this.socket = new DatagramSocket(this.port);
        this.buffer = new byte[65536]; // Max length of UDP datagrams

        // Initialize local hashed table
        this.nodes = new ConcurrentHashMap<>();

        // Initialize list that references started threads
        this.servicingThread = new ArrayList<>();
    }

    public void run() {
        // TODO: write function
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public byte[] getPrivateKey() {
        return this.privateKey;
    }

    public int getPort() {
        return this.port;
    }
}
