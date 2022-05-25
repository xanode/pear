package toxcore.dht.network;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import toxcore.dht.async.AsynchronousService;
import toxcore.dht.DHT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class Network {

    // Constants
    // Packet types
    public static final byte PACKET_TYPE_LENGTH = 1;
    public static final byte PACKET_PING_REQUEST_TYPE = 0;
    public static final byte PACKET_PING_RESPONSE_TYPE = 1;
    public static final byte PACKET_FIND_NODE_REQUEST_TYPE = 2; // Node request
    public static final byte PACKET_FIND_NODE_RESPONSE_TYPE = 4; // Node response
    // Network related constants
    public static final byte PEAR_AF_INET = 2;
    public static final byte PEAR_AF_INET6 = 10;
    public static final byte SIZE_IP4 = 4;
    public static final byte SIZE_IP6 = 16;
    public static final int MAX_UDP_PACKET_SIZE = 65536;
    // Ping related constants
    public static final int ID_LENGTH = 8;
    public static final int PING_PORT = 33445;
    public static final int PING_TIMEOUT = 10000; // ms

    // Attributes
    public final DHT dht;
    protected DatagramSocket pingSocket;
    private boolean running;
    private ConcurrentHashMap <byte[], Callable> trackingSentPacket; // byte[] is the packet identifier

    public Network(DHT dht) {
        this.dht = dht;
    }

    /**
     * Handle received packets.
     */
    public void handle() throws SocketException {
        this.pingSocket = new DatagramSocket(PING_PORT);
        var service = new AsynchronousService(new LinkedBlockingDeque<>());
        this.running = true;
        while (running) {
            DatagramPacket receivedPacket = new DatagramPacket(new byte[MAX_UDP_PACKET_SIZE], MAX_UDP_PACKET_SIZE);
            try {
                this.pingSocket.receive(receivedPacket);
                service.execute(new PacketManagementTask(this.dht, receivedPacket.getData(), this.trackingSentPacket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        service.close();
    }

    /**
     * Stop the network core of the DHT.
     * Close the ping socket.
     */
    public void close() {
        this.running = false;
        this.pingSocket.close();
    }

    /**
     * Generate the header of a packet.
     * @param type The type of the packet.
     * @return The header of the packet.
     */
    public byte[] generatePacketHeader(byte type, byte[] nonce) {
        ByteBuffer header = ByteBuffer.allocate(
                PACKET_TYPE_LENGTH
                + DHT.CRYPTO_PUBLIC_KEY_SIZE
                + DHT.CRYPTO_NONCE_SIZE
        );
        switch (type) {
            case PACKET_PING_REQUEST_TYPE -> header.put(PACKET_PING_REQUEST_TYPE);
            case PACKET_PING_RESPONSE_TYPE -> header.put(PACKET_PING_RESPONSE_TYPE);
            case PACKET_FIND_NODE_REQUEST_TYPE -> header.put(PACKET_FIND_NODE_REQUEST_TYPE);
            case PACKET_FIND_NODE_RESPONSE_TYPE -> header.put(PACKET_FIND_NODE_RESPONSE_TYPE);
            default -> throw new IllegalArgumentException("Invalid packet type: " + type);
        }
        header.put(dht.getPublicKey()); // Public of key of the sender !
        header.put(nonce);
        return header.array();
    }

    /**
     * Send a packet to a given node.
     * @param packet The packet to send
     * @param receiver The node to send to packet to
     * @param callback The method to call when receiving the response
     */
    public void sendPacket(Packet packet, Node receiver, Callable callback) throws SodiumLibraryException {
        // Check packet
        if (packet.getSenderPublicKey() != receiver.getNodeKey()) {
            throw new IllegalArgumentException("Receiver node public key and packet public key mismatch");
        }
        // Send packet
        try {
            byte[] data = packet.toByteArray(this.dht);
            this.pingSocket.send(new DatagramPacket(data, data.length, receiver.getNodeAddress(), receiver.getPort()));
            // Register it to handle response
            this.trackingSentPacket.put(packet.getIdentifier(), callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
