package toxcore.dht;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.sun.jna.Platform;

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
            this.libraryPath = "/usr/lib64/libsodium.so.23"; // /usr/local/lib/libsodium.so
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

    @Override
    public void run() {
        while (running) {
            DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
            try {
                this.socket.receive(packet);
                Thread thread = new Thread(new DHTRequestHandler(this, packet));
                this.servicingThread.add(thread);
                thread.start();
            } catch (SocketException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        this.running = false;
        try {
            for (Thread thread: this.servicingThread) {
                if (thread.isAlive()) {
                    thread.join();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commandLine() {
        Scanner scanner = new Scanner(System.in);
        String command;
        while (this.running) {
            System.out.println("DHT >> ");
            command = scanner.nextLine();
            if (command.toLowerCase().equals("close")) {
                this.close();
            } else {
                System.out.println("Invalid command.");
            }
        }
        scanner.close();
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

    /**
     * Indicates which of the keys is the closest to onde of baseNode.
     * @param baseNode Base node for comparison
     * @param initialNode Initial node
     * @param comparisonNode Node for comparison
     * @return True if the node to compare is closer to the base node than the initial node, false otherwise.
     */
    public static boolean getClosest(Node baseNode, Node initialNode, Node comparisonNode) {
        // TODO: Constant used below should be replaced!
        byte[] baseKey = baseNode.getNodeKey();
        byte[] initialKey = initialNode.getNodeKey();
        byte[] comparisonKey = comparisonNode.getNodeKey();
        for (int i=0; i<32; i++) { // Big-endian format! (32 because 32 byte keys!)
            int distanceToComparison = (baseKey[i] & 0xff) ^ (comparisonKey[i] & 0xff); // Convert to unsigned byte before xor
            int distanceToInitial = (baseKey[i] & 0xff) ^ (initialKey[i] & 0xff);
            if (distanceToComparison < distanceToInitial) {
                return true;
            } else if (distanceToComparison > distanceToInitial) {
                return false;
            }
        }
        return false;
    }

    public static BigInteger getDistance(byte[] baseKey, byte[] nodeKey) {
        return (new BigInteger(1, baseKey)).xor(new BigInteger(1, nodeKey));
    }
}
