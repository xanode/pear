package toxcore.dht;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class DHTRequestHandler implements Runnable {
    final private byte[] publicKey;
    final private byte[] privateKey;
    private DatagramPacket packet;
    private DatagramSocket socket;
    private ConcurrentHashMap<byte[], InetAddress> nodes;
    final private int port;
    public DHTRequestHandler(DatagramSocket socket, DatagramPacket packet, int port, byte[] publicKey, byte[] privateKey, ConcurrentHashMap<byte[], InetAddress> nodes) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.socket = socket;
        this.packet = packet;
        this.nodes = nodes;
        this.port = port;
    }

    public void run() {
        // TODO: everything
    }

    private DatagramPacket DHTPacketBuilder(byte[] type, byte[] receiverPublicKey, byte[] payload, InetAddress receiverInetAddress) throws SodiumLibraryException {
        // Generate nonce
        byte[] nonce = SodiumLibrary.randomBytes(SodiumLibrary.cryptoBoxNonceBytes().intValue());
        // Encrypt payload
        byte[] encryptedPayload = SodiumLibrary.cryptoBoxEasy(payload, nonce, receiverPublicKey, this.privateKey);

        // Collect data
        byte[] data = new byte[type.length+this.publicKey.length+nonce.length+encryptedPayload.length];
        // TODO: build packet

        // Effective construction of datagram packet
        return new DatagramPacket(data, data.length, receiverInetAddress, this.port);
    }

    private BigInteger getDistance(byte[] nodePublicKey) {
        /*
         * Compute distance between this node and a foreign one.
         * The distance function is defined as the XOR between the 2 DHT public keys,
         * both are treated as unsigned 32 byte numbers in big endian format.
         */
        return (new BigInteger(1, this.publicKey)).xor(new BigInteger(1, nodePublicKey));
    }

    private ArrayList<byte[]> getClosestNodes(byte[] nodePublicKey) {
        // Get closest nodes in our own private list
        ArrayList<byte[]> closestNodes = new ArrayList<>();
        // TODO: compute distances, sort and return them
        return closestNodes;
    }

    private InetAddress lookupNode(byte[] nodePublicKey) {
        // Recursive algorithm that lookup a node in the full dht
        // TODO: write the algorithm
        return InetAddress.getLoopbackAddress();
    }

    // TODO: Add missing functions!
}
