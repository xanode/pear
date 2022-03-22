package toxcore.dht;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class NodeNetworkData {

    private final byte[] nodeKey;
    private final InetAddress nodeAddress;
    private final int port;

    protected NodeNetworkData(final byte[] nodeKey, final InetAddress nodeAdress, final int port) {
        this.nodeKey = nodeKey;
        this.nodeAddress = nodeAdress;
        this.port = port;
    }

    /**
     * Get node key.
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
}
