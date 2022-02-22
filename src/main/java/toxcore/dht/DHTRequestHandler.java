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
import java.util.Collections;
import java.util.Set;
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
        /*
         * Get the closest nodes in our own private DHT
         */
        ArrayList<byte[]> closestNodes = new ArrayList<>();
        //Get the keys of the DHT
        ConcurrentHashMap<byte[], InetAddress> nodes = this.dht.getDHT();
        Set<byte[]> keys = nodes.keySet();
        int numberOfNeighbor = 4;
        //Create the list of all the distances
        ArrayList<BigInteger> distances = new ArrayList<>();
        for (byte[] publicKey : keys){
            BigInteger distance = getDistance(publicKey);
            distances.add(distance);
        }
        //do this the numberOfNeighbor time
        for (int i=0; i<numberOfNeighbor; i++) {
            //find the min
            BigInteger min = Collections.min(distances);
            //get the matching publicKey
            for (byte[] publicKey : keys) {
                BigInteger distance = getDistance(publicKey);
                if (distance.equals(min)) {
                    closestNodes.add(publicKey);
                    distances.remove(min);
                    break;
                }
            }
        }
        // TODO: I create getDHT methode in DHT.java to have access to publicKeys. Maybe optimize this methode using xor 2 time to get the matching publicKey
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
