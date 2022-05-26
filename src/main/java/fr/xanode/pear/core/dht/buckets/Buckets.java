package fr.xanode.pear.core.dht.buckets;

import lombok.extern.slf4j.Slf4j;
import fr.xanode.pear.core.dht.DHT;
import fr.xanode.pear.core.dht.network.Node;

import java.util.BitSet;

@Slf4j
public class Buckets {

    private final Node baseNode;
    private final KBucket[] buckets;

    public Buckets(final Node baseNode, int size) {
        this.baseNode = baseNode;
        this.buckets = new KBucket[size]; // Size should be 256 because there are 256 bits keys
        for (int i=0; i<size; i++) {
            this.buckets[i] = new KBucket(DHT.CRYPTO_KEY_SIZE);
        }
    }

    /**
     * Indicates the KBucket in which the node should be added.
     * @param node The node to add.
     * @return The index of the KBucket to be filled.
     */
    private int bucketIndex(Node node) {
        BitSet nodeBitSet = BitSet.valueOf(node.getNodeKey());
        nodeBitSet.xor(BitSet.valueOf(this.baseNode.getNodeKey()));
        int i;
        for (i=(8 * DHT.CRYPTO_KEY_SIZE - 1); i>=0; i--) { // 256 bits key
            if (nodeBitSet.get(i)) {
                break;
            }
        }
        return i;
    }

    /**
     * Update a KBucket with a new node.
     * @param node The node that may be added to the KBucket.
     */
    public void update(Node node) {
        int index = this.bucketIndex(node);
        this.buckets[index].update(node);
    }

    /**
     * Get the list of the nearest known nodes.
     * @param max The number of nodes wanted.
     * @return The list of the nearest known nodes.
     */
    public ClientList getClosest(int max) {
        // Use getClosestTo with the base node as parameter?
        ClientList closest = new ClientList(max, this.baseNode);
        for (KBucket bucket: this.buckets) {
            for (Node node: bucket.getBucket()) {
                if (!closest.add(node)) {
                    break;
                }
            }
        }
        return closest;
    }

    /**
     * Get the list of the nearest known nodes from a given node.
     * @param node The node from which the list of the nearest known nodes is wanted.
     * @param max The number of nodes wanted.
     * @return The list of the nearest known nodes.
     */
    public ClientList getClosestTo(Node node, int max) {
        ClientList closest = new ClientList(max, node);
        for (Node currentNode: this.buckets[this.bucketIndex(node)].getBucket()) { // TODO: If there is nothing in that bucket?
            if (!closest.add(currentNode)) {
                break;
            }
        }
        return closest;
    }

    /**
     * Returns if the node is in the buckets.
     * @param node The node to check.
     * @return True if the node is in the buckets, false otherwise.
     */
    public Node contains(Node node) {
        for (KBucket bucket: this.buckets) {
            if (bucket.contains(node) != null) {
                return bucket.contains(node);
            }
        }
        return null;
    }

    public boolean isInsertable(Node node) {
        if (this.contains(node) == null) {
            ClientList closestToNode = this.getClosestTo(node, 1);
            ClientList closestToBaseNode = new ClientList(ClientList.CLIENT_LIST_SIZE, this.baseNode);
            closestToBaseNode.add(node);
            closestToBaseNode.add(closestToNode.getNode(0));
            return closestToBaseNode.getNode(0).equals(node); // If <node> is at first position in the client list, it is closer to the base node
        }
        return false;
    }

}
