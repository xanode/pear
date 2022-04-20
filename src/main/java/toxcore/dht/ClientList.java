package toxcore.dht;

import java.util.ArrayList;

public class ClientList {

    private final int maximumSize;
    private final Node baseNode;
    private ArrayList<Node> clientList;

    protected static final int CLIENT_LIST_SIZE = 32;

    ClientList(final int maximumSize, final Node baseNode) {
        this.maximumSize = maximumSize; // Should be set to 32
        this.baseNode = baseNode;
        this.clientList = new ArrayList<>();
    }

    /**
     * Add a given node in the client list, sorted by proximity to the base node.
     * @param node Node to add in the list.
     * @return True if it has been inserted in the list, false otherwise.
     */
    protected boolean add(Node node) {
        // TODO: A node should be added only if it is closer than the farthest node in the list.
        // Check if list is full
        if (this.clientList.size() >= this.maximumSize) {
            return false;
        }
        for (int i=0; i<this.clientList.size(); i++) {
            if (this.getClosest(this.clientList.get(i), node)) {
                this.clientList.add(i, node);
                return true;
            }
        }
        this.clientList.add(node);
        return true;
    }

    /**
     * Remove a given node from the client list.
     * @param node Node to remove.
     * @return True if it has been removed, false otherwise.
     */
    protected boolean remove(Node node) {
        return this.clientList.remove(node);
    }

    /**
     * Get the actual size of the client list.
     * @return The size of the list.
     */
    protected int getSize() {
        return this.clientList.size();
    }

    /**
     * Get the maximum size of the client list.
     * @return The maximum size of the list.
     */
    protected int getMaximumSize() {
        return this.maximumSize;
    }

    /**
     * Indicates which of the node is closer to the base node.
     * @param node1 First node.
     * @param node2 Second node.
     * @return True if node1 is closer to the base node, false otherwise.
     */
    private boolean getClosest(Node node1, Node node2) {
        // TODO: Constant used below should be replaced!
        byte[] baseKey = this.baseNode.getNodeKey();
        byte[] key1 = node1.getNodeKey();
        byte[] key2 = node2.getNodeKey();
        for (int i=0; i<32; i++) { // Big-endian format! (32 because 32 byte keys!)
            int distanceToComparison = (baseKey[i] & 0xff) ^ (key2[i] & 0xff); // Convert to unsigned byte before xor
            int distanceToInitial = (baseKey[i] & 0xff) ^ (key1[i] & 0xff);
            if (distanceToComparison < distanceToInitial) {
                return true;
            } else if (distanceToComparison > distanceToInitial) {
                return false;
            }
        }
        return false;
    }
}
