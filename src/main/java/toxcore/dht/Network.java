package toxcore.dht;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Network {

    // Constants
    // Packet types
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
    protected final DatagramSocket pingSocket;
    private boolean running;

    protected Network() throws SocketException {
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

}
