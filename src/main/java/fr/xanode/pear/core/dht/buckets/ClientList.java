package fr.xanode.pear.core.dht.buckets;

import fr.xanode.pear.core.dht.DHT;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.xanode.pear.core.dht.network.Node;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class ClientList implements Comparator<Node> {

    @Getter @NonNull private final int maximumSize;
    @Getter @NonNull private final Node baseNode;
    private final TreeSet<Node> clientList = new TreeSet<>(this); // TODO: not thread-safe

    protected static final int CLIENT_LIST_SIZE = 32;

    /**
     * Add a given node in the client list, sorted by proximity to the base node.
     * @param node Node to add in the list.
     * @return True if it has been inserted in the list, false otherwise.
     */
    public boolean add(Node node) {
        log.info("Adding node in client list...");
        if (this.maximumSize != 0 && this.clientList.size() >= this.maximumSize) { // 0 mean no limit
            log.info("The client list is full.");
            Node higherNode;
            if ((higherNode = this.clientList.higher(node)) != null) { // Is the node closer than another in the client list?
                this.clientList.remove(higherNode);
                this.clientList.add(node);
                return true;
            }
            return false;
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
     * Tells if a greater node exists or not
     * @param node The value to match
     * @return the least element greater than e, or null if there is no such element.
     */
    public boolean existsHigher(Node node) {
        return this.clientList.higher(node) != null;
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
     * @return 1 if node2 is closer to the base node, 0 if both nodes are equals, -1 otherwise.
     */
    public int compare(Node node1, Node node2) {
        // TODO: Constant used below should be replaced!
        byte[] baseKey = this.baseNode.getNodeKey();
        byte[] key1 = node1.getNodeKey();
        byte[] key2 = node2.getNodeKey();
        for (int i=0; i< DHT.CRYPTO_KEY_SIZE; i++) { // Big-endian format! (32 because 32 byte keys!)
            int distanceToKey2 = (baseKey[i] & 0xff) ^ (key2[i] & 0xff); // Convert to unsigned byte before xor
            int distanceToKey1 = (baseKey[i] & 0xff) ^ (key1[i] & 0xff);
            if (distanceToKey2 < distanceToKey1) return 1;
            else if (distanceToKey2 > distanceToKey1) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * Get a node in the list.
     * @param index Index of the node to get.
     * @return The node to get if it exists.
     */
    public Node getNode(int index) {
        List<Node> clients = this.clientList.stream().toList();
        return clients.get(index);
    }
}
