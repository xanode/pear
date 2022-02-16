package toxcore.dht;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class DHTRequestHandler implements Runnable {
    private DHT dht;
    private DatagramPacket packet;
    public DHTRequestHandler(DHT dht, DatagramPacket packet) {
        this.dht = dht;
        this.packet = packet;
    }

    public void run() {
        // TODO: everything
    }

    private DatagramPacket DHTPacketBuilder(byte[] type, byte[] receiverPublicKey, byte[] payload, InetAddress receiverInetAddress) throws SodiumLibraryException {
        // Generate nonce
        byte[] nonce = SodiumLibrary.randomBytes(SodiumLibrary.cryptoBoxNonceBytes().intValue());
        // Encrypt payload
        byte[] encryptedPayload = SodiumLibrary.cryptoBoxEasy(payload, nonce, receiverPublicKey, this.dht.getPrivateKey());

        // Collect data
        byte[] data = new byte[type.length+this.dht.getPublicKey().length+nonce.length+encryptedPayload.length];
        // TODO: build packet

        // Effective construction of datagram packet
        return new DatagramPacket(data, data.length, receiverInetAddress, this.dht.getPort());
    }

    private BigInteger getDistance(byte[] nodePublicKey) {
        /*
         * Compute distance between this node and a foreign one.
         * The distance function is defined as the XOR between the 2 DHT public keys,
         * both are treated as unsigned 32 byte numbers in big endian format.
         */
        return (new BigInteger(1, this.dht.getPublicKey())).xor(new BigInteger(1, nodePublicKey));
    }

    private ArrayList<byte[]> getClosestNodes(byte[] nodePublicKey) {
        // Get closest nodes in our own private list
        ArrayList<byte[]> closestNodes = new ArrayList<>();
        // TODO: compute distances, sort and return them
        return closestNodes;
    }

    private void sendNodes (byte[] nodePublicKey, byte[] receiverPublicKey) throws IOException, SodiumLibraryException {
        /*
         * Send the 4 closest nodes to the target/"node" from your own private list
         * Send it to the receiver
         */
        //Get the closest nodes
        ArrayList<byte[]> closestNodes = getClosestNodes(nodePublicKey);
        //Create the payload packet
        ByteBuffer buffer = ByteBuffer.allocate(1+32*closestNodes.size());
        //Number of nodes
        buffer.put((byte) closestNodes.size());
        //Adding publicKey of the closest nodes
        for (byte[] nodeClose : closestNodes) {
            for (byte i : nodeClose) {
                buffer.put(i);
            }
        }
        byte[] payload = buffer.array();
        //build the packet with the payload
        //type of the packet
        byte[] typePacket = new byte[] {4};
        DatagramPacket builtPacket = DHTPacketBuilder(typePacket, receiverPublicKey, payload, null);
        //Send the builtPacket
        DatagramSocket socket = new DatagramSocket();
        socket.send(builtPacket);
    }

    private InetAddress lookupNode(byte[] nodePublicKey) {
        // Recursive algorithm that lookup a node in the full dht
        // TODO: write the algorithm
        return InetAddress.getLoopbackAddress();
    }

    // TODO: Add missing functions!
}
