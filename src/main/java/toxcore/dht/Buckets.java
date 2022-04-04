package toxcore.dht;

import java.util.BitSet;

public class Buckets {

    private final Node baseNode;
    private KBucket[] buckets;

    Buckets(final Node baseNode, int size) {
        this.baseNode = baseNode;
        this.buckets = new KBucket[size]; // Size should be 256 because there are 256 bits keys
        for (int i=0; i<size; i++) {
            this.buckets[i] = new KBucket(DHT.CRYPTO_PUBLIC_KEY_SIZE);
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
        for (i=255; i>=0; i--) { // 256 bits key
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
    protected void update(Node node) {
        int index = this.bucketIndex(node);
        this.buckets[index].update(node);
    }

    /**
     * Get the list of the nearest known nodes.
     * @param max The number of nodes wanted.
     * @return The list of the nearest known nodes.
     */
    protected ClientList getClosest(int max) {
        ClientList closest = new ClientList(max, this.baseNode);
        for (KBucket bucket: this.buckets) {
            for (Node node: bucket.toArrayList()) {
                if (!closest.add(node)) {
                    break;
                }
            }
        }
        return closest;
    }

}
