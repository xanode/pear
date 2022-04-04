package toxcore.dht;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class Network {

    // Constants
    // Packet types
    protected static final byte PACKET_TYPE_LENGTH = 1;
    protected static final byte PACKET_PING_REQUEST_TYPE = 0;
    protected static final byte PACKET_PING_RESPONSE_TYPE = 1;
    protected static final byte PACKET_FIND_NODE_REQUEST_TYPE = 2; // Node request
    protected static final byte PACKET_FIND_NODE_RESPONSE_TYPE = 4; // Node response
    // Network related constants
    protected static final byte PEAR_AF_INET = 2;
    protected static final byte PEAR_AF_INET6 = 10;
    protected static final byte SIZE_IP4 = 4;
    protected static final byte SIZE_IP6 = 16;
    private static final int MAX_UDP_PACKET_SIZE = 65536;
    // Ping related constants
    protected static final int PING_PORT = 33445;
    protected static final int PING_TIMEOUT = 10000; // ms

    // Attributes
    protected final DHT dht;
    protected final DatagramSocket pingSocket;
    private boolean running;

    protected Network(DHT dht) throws SocketException {
        this.dht = dht;
        this.pingSocket = new DatagramSocket(PING_PORT);
        this.running = true;
    }

    /**
     * Handle received packets.
     */
    protected void handle() {
        while (running) {
            DatagramPacket receivedPingPacket = new DatagramPacket(new byte[MAX_UDP_PACKET_SIZE], MAX_UDP_PACKET_SIZE);
            try {
                this.pingSocket.receive(receivedPingPacket);
                // TODO: Handle received ping packets
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stop the network core of the DHT.
     * Close the ping socket.
     */
    protected void close() {
        this.running = false;
        this.pingSocket.close();
    }

    /**
     * Generate the header of a packet.
     * @param type The type of the packet.
     * @return The header of the packet.
     */
    protected byte[] generatePacketHeader(byte type, byte[] nonce) {
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
     * @param type The type of the packet.
     * @param payload The payload of the packet to send.
     * @param node The node to send the packet to.
     */
    protected void sendPacket(byte type, byte[] payload, Node node) throws SodiumLibraryException {
        // Encrypt payload
        byte[] nonce = dht.generateNonce();
        byte[] encryptedPayload = dht.encrypt(node.getNodeKey(), nonce, payload);
        // Build packet
        byte[] packet = ByteBuffer.allocate(
                        PACKET_TYPE_LENGTH
                        + DHT.CRYPTO_PUBLIC_KEY_SIZE
                        + DHT.CRYPTO_NONCE_SIZE
                        + encryptedPayload.length
                )
                .put(this.generatePacketHeader(type, nonce))
                .put(encryptedPayload)
                .array();
        DatagramPacket pingPacket = new DatagramPacket(packet, packet.length, node.getNodeAddress(), node.getPort());
        // Send packet
        try {
            this.pingSocket.send(pingPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
