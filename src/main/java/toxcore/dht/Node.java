package toxcore.dht;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

public class Node {

    private final byte[] nodeKey;
    private final InetAddress nodeAddress;
    private final int port;
    private Timestamp timestamp;

    Node(final byte[] nodeKey, final InetAddress nodeAddress, final int port) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        if (!nodeAddress.equals(node.nodeAddress)) return false;
        if (!nodeKey.equals(node.nodeKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeKey.hashCode();
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
