package toxcore.dht;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Arrays;

public class Node {

    private final byte[] nodeKey;
    private final InetAddress nodeAddress;
    private final int port;
    private Timestamp timestamp;

    Node(final byte[] nodeKey, final InetAddress nodeAddress, final int port) {
        if (nodeAddress.isMulticastAddress()) {
            // Do not accept multicast address it cannot represent a node
            throw new IllegalArgumentException("Multicast address not allowed");
        }
        this.nodeKey = nodeKey;
        this.nodeAddress = nodeAddress;
        this.port = port;
    }

    /**
     * Get node public key.
     * @return the node public key.
     */
    protected byte[] getNodeKey() {
        return this.nodeKey;
    }

    /**
     * Get the node IP address.
     * @return the node IP address.
     */
    protected InetAddress getNodeAddress() {
        return this.nodeAddress;
    }

    /**
     * Get the port of the node.
     * @return the port of the node.
     */
    protected int getPort() {
        return this.port;
    }

    /**
     * Get the timestamp of the node.
     * @return the timestamp of the node.
     */
    protected Timestamp getTimestamp() {
        return this.timestamp;
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
     * Get the hashcode of the node.
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
    protected byte[] prepareRawData() {
        int lengthAddress = (this.nodeAddress instanceof Inet4Address) ? 4 : 16;
        int addressFamily = (this.nodeAddress instanceof Inet4Address) ? 2 : 10;
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
    protected boolean isAlive() {
        if (!this.nodeAddress.isAnyLocalAddress()) {
            // TODO: check if the node is still alive
            return false;
        } else {
            // Update timestamp
            this.timestamp = new Timestamp(System.currentTimeMillis());
            return true;
        }
    }


}
