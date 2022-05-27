package fr.xanode.pear.core.dht.network;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.xanode.pear.core.dht.async.AsynchronousService;
import fr.xanode.pear.core.dht.DHT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@RequiredArgsConstructor
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
    public static final int DHT_PORT = 33445;
    public static final int PING_TIMEOUT = 10000; // ms

    // Attributes
    @NonNull public final DHT dht;
    protected DatagramSocket pingSocket;
    private boolean running;
    private final ConcurrentHashMap <byte[], Callable<?>> trackingSentPacket = new ConcurrentHashMap<>(); // byte[] is the packet identifier


    /**
     * Handle received packets.
     */
    public void handle() throws SocketException {
        log.info("Starting network service...");
        this.pingSocket = new DatagramSocket(DHT_PORT);
        log.info("Creating asynchronous service...");
        var service = new AsynchronousService(new LinkedBlockingDeque<>());
        log.info("Asynchronous service created.");
        this.running = true;
        log.info("Network service started.");
        while (running) {
            DatagramPacket receivedPacket = new DatagramPacket(new byte[MAX_UDP_PACKET_SIZE], MAX_UDP_PACKET_SIZE);
            try {
                log.info("Waiting for packets...");
                this.pingSocket.receive(receivedPacket);
                log.info("Packet received!");
                service.execute(new PacketManagementTask(this.dht, receivedPacket.getData(), this.trackingSentPacket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("Closing asynchronous service...");
        service.close();
        log.info("Asynchronous service closed.");
        log.info("Network service closed.");
    }

    /**
     * Stop the network core of the DHT.
     * Close the ping socket.
     */
    public void close() {
        log.info("Closing network service...");
        this.running = false;
        this.pingSocket.close();
        log.info("Socket closed.");
    }

    /**
     * Send a packet to a given node.
     * @param packet The packet to send
     * @param receiver The node to send to packet to
     * @param callback The method to call when receiving the response
     */
    public void sendPacket(Packet packet, Node receiver, Callable<?> callback) throws SodiumLibraryException {
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
