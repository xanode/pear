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
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class DHTRequestHandler implements Runnable {
    private DHT dht;
    private DatagramPacket packet;
    public DHTRequestHandler(DHT dht, DatagramPacket packet) {
        this.dht = dht;
        this.packet = packet;
    }

    @Override
    public void run() {
        // Parse data
        byte[] packetData = this.packet.getData();
        byte[] packetType = Arrays.copyOfRange(packetData, 0, 1);
        byte[] senderPublicKey = Arrays.copyOfRange(packetData, 1, 33);
        byte[] nonce = Arrays.copyOfRange(packetData, 33, 57);
        byte[] encryptedPayload = Arrays.copyOfRange(packetData, 58, this.packet.getLength());
        // Select action
        switch (packetType[0]) {
            case (byte) 0:
                // Ping request
                break;
            case (byte) 1:
                // Ping response
                break;
            default:
                // Invalid Type
        }
    }

    private DatagramPacket DHTPacketBuilder(byte[] type, byte[] receiverPublicKey, byte[] payload, InetAddress receiverInetAddress) throws SodiumLibraryException {
        // Generate nonce
        byte[] nonce = SodiumLibrary.randomBytes(SodiumLibrary.cryptoBoxNonceBytes().intValue());
        // Encrypt payload
        byte[] encryptedPayload = SodiumLibrary.cryptoBoxEasy(payload, nonce, receiverPublicKey, this.dht.getPrivateKey());

        // Collect data
        byte[] data = ByteBuffer.allocate(type.length+this.dht.getPublicKey().length+nonce.length+encryptedPayload.length)
                .put(type)
                .put(receiverPublicKey)
                .put(nonce)
                .put(encryptedPayload)
                .array();

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
        //type of the packet : 4
        byte[] typePacket = new byte[] {4};
        DatagramPacket builtPacket = DHTPacketBuilder(typePacket, receiverPublicKey, payload, null);
        //Send the builtPacket
        DatagramSocket socket = new DatagramSocket();
        socket.send(builtPacket);
        // TODO : need to replace null element (InetAddress) in DHT builder
    }

    private InetAddress lookupNode(byte[] nodePublicKey) {
        // Recursive algorithm that lookup a node in the full dht
        // TODO: write the algorithm
        return InetAddress.getLoopbackAddress();
    }

    // TODO: Add missing functions!
}
