package toxcore.dht;

import java.util.BitSet;

public class Buckets {

    private final byte[] baseKey;
    private ClientList[] buckets;

    Buckets(int size, final byte[] baseKey) {
        this.baseKey = baseKey;
        this.buckets = new ClientList[size]; // Should be 256 because there are 256 bits keys
        for (int i=0; i<size; i++) {
            this.buckets[i] = new ClientList(size, baseKey);
        }
    }

    private int bucketIndex(byte[] nodeKey) {
        BitSet nodeBitSet = BitSet.valueOf(nodeKey);
        nodeBitSet.xor(BitSet.valueOf(this.baseKey));
        int i;
        for (i=255; i>=0; i--) { // 256 bits key
            if (nodeBitSet.get(i)) {
                break;
            }
        }
        return i;
    }

    protected void update(byte[] nodeKey) {
        int index = this.bucketIndex(nodeKey);
        this.buckets[index].add(nodeKey);
    }

}
