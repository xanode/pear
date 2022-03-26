package toxcore.dht;

import java.util.BitSet;

public class Buckets {

    private final Node baseNode;
    private KBucket[] buckets;

    Buckets(int size, final Node baseNode) {
        this.baseNode = baseNode;
        this.buckets = new KBucket[size]; // Size should be 256 because there are 256 bits keys
        for (int i=0; i<size; i++) {
            this.buckets[i] = new KBucket(32); // TODO: replace 32 by a constant
        }
    }

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

    protected void update(Node node) {
        int index = this.bucketIndex(node);
        this.buckets[index].update(node);
    }

}
