package toxcore.dht.network;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import toxcore.dht.DHT;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

public class Node {

    private final DHT dht;
    private final byte[] nodeKey;
    private final InetAddress nodeAddress;
    private final int port;

    public Node(final DHT dht, final byte[] nodeKey, final InetAddress nodeAddress, final int port) {
        if (nodeAddress.isMulticastAddress()) {
            // Do not accept multicast address it cannot represent a node
            throw new IllegalArgumentException("Multicast address not allowed");
        }
        this.dht = dht;
        this.nodeKey = nodeKey;
        this.nodeAddress = nodeAddress;
        this.port = port;
    }

    /**
     * Get node public key.
     * @return the node public key.
     */
    public byte[] getNodeKey() {
        return this.nodeKey;
    }

    /**
     * Get the node IP address.
     * @return the node IP address.
     */
    public InetAddress getNodeAddress() {
        return this.nodeAddress;
    }

    /**
     * Get the port of the node.
     * @return the port of the node.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Get the DHT instance of the node.
     * @return the DHT instance of the node.
     */
    public DHT getDHT() {
        return this.dht;
    }

    /**
     * Tell if a node equals another object.
     * @param o The object to compare.
     * @return True if the object is a node and if the node is the same, false either.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        if (!nodeAddress.equals(node.nodeAddress)) return false;
        return Arrays.equals(nodeKey, node.nodeKey);
    }

    /**
     * Compute the hashcode of the node.
     * @return the hashcode of the node.
     */
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(nodeKey);
        result = 31 * result + nodeAddress.hashCode();
        result = 31 * result + port;
        return result;
    }

    /**
     * Prepare data for packaging.
     * @return Raw data into packed node format.
     */
    public byte[] prepareRawData() {
        int lengthAddress = (this.nodeAddress instanceof Inet4Address) ? Network.SIZE_IP4 : Network.SIZE_IP6;
        int addressFamily = (this.nodeAddress instanceof Inet4Address) ? Network.PEAR_AF_INET : Network.PEAR_AF_INET6;
        return ByteBuffer.allocate(1+7+lengthAddress+2+32)
                .put((byte)0) // Only UDP for now
                .put((byte)addressFamily)
                .put(this.nodeAddress.getAddress())
                .put((byte)this.port)
                .put(this.nodeKey)
                .array();
    }

    /**
     * Tell if a node is alive.
     * @return True if the node is alive, false either.
     */
    public boolean isAlive() {
        if (!this.nodeAddress.isAnyLocalAddress()) {
            return false;
        } else {
            byte[] pingId = new byte[Network.ID_LENGTH];
            new SecureRandom().nextBytes(pingId);
            Ping ping = new Ping(this, pingId);
            try {
                ping.send(PacketType.REQUEST);
                while (!ping.isReceived() && ((new Date()).getTime() - ping.getSentDate().getTime()) < Network.PING_TIMEOUT);
                if (!ping.isReceived()) {
                    return false;
                }
            } catch (SodiumLibraryException e) {
                e.printStackTrace(); // Should never happen
            }
            return true;
        }
    }
}
