package toxcore.dht;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.sun.jna.Platform;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
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
        while(this.running) {
            System.out.println("DHT >> ");
            command = scanner.nextLine();
            if (command.toLowerCase().equals("close")) {
                this.closeDHT();
            } else {
                System.out.println("Invalid command.");
            }
        }
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
     * Indicates which of the keys is the closest to baseKey.
     * @param baseKey Base key for comparison
     * @param initialKey Initial key
     * @param comparisonKey Key for comparison
     * @return True if the key to compare is closer to the base key than the initial key, false otherwise.
     */
    public static boolean getClosest(byte[] baseKey, byte[] initialKey, byte[] comparisonKey) {
        // TODO: Constant used below should be replaced!
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
