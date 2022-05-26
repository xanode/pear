package fr.xanode.pear.core.dht.buckets;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.xanode.pear.core.dht.network.Node;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class ClientList {

    @Getter @NonNull private final int maximumSize;
    @Getter @NonNull private final Node baseNode;
    private final ArrayList<Node> clientList = new ArrayList<>();

    protected static final int CLIENT_LIST_SIZE = 32;

    /**
     * Add a given node in the client list, sorted by proximity to the base node.
     * @param node Node to add in the list.
     * @return True if it has been inserted in the list, false otherwise.
     */
    public boolean add(Node node) {
        // TODO: A node should be added only if it is closer than the farthest node in the list.
        // Check if list is full
        log.info("Adding node in client list...");
        if (this.clientList.size() >= this.maximumSize) {
            log.info("The client list is full.");
            return false;
        }
        for (int i=0; i<this.clientList.size(); i++) {
            if (this.getClosest(this.clientList.get(i), node)) {
                this.clientList.add(i, node);
                log.info("Node added.");
                return true;
            }
        }
        this.clientList.add(node);
        log.info("Node added.");
        return true;
    }

    /**
     * Remove a given node from the client list.
     * @param node Node to remove.
     * @return True if it has been removed, false otherwise.
     */
    public boolean remove(Node node) {
        return this.clientList.remove(node);
    }

    /**
     * Get the actual size of the client list.
     * @return The size of the list.
     */
    public int getSize() {
        return this.clientList.size();
    }

    /**
     * Indicates which of the node is closer to the base node.
     * @param node1 First node.
     * @param node2 Second node.
     * @return True if node1 is closer to the base node, false otherwise.
     */
    public boolean getClosest(Node node1, Node node2) {
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

    /**
     * Get a node in the list.
     * @param index Index of the node to get.
     * @return The node to get if it exists.
     */
    public Node getNode(int index) {
        return this.clientList.get(index);
    }
}
