package toxcore.dht;

import java.util.ArrayList;

public class KBucket {

    private final int k;
    private final ArrayList<Node> bucket;

    protected KBucket(final int k) {
        this.k = k; // Original paper advise 20
        this.bucket = new ArrayList<>();
    }

    /**
     * Update this KBucket with a new node.
     * @param node The node to update the KBucket.
     */
    protected void update(Node node) {
        // If the node already exist in the bucket, move it to the tail of the list
        if (this.bucket.contains(node)) {
            this.bucket.remove(node);
            this.bucket.add(node);
        } else {
            // If not in the bucket and the bucket isn't full, insert it at the tail
            if (this.bucket.size() < this.k) {
                this.bucket.add(node);
            } else {
                // If the bucket is full
                if (!this.bucket.get(this.k-1).isAlive()) {
                    // Replace last node if it isn't alive anymore
                    this.bucket.set(this.k-1, node);
                }
            }
        }

    }

    /**
     * Get the actual size of the KBucket.
     * @return The size of the KBucket.
     */
    protected int getSize() {
        return this.bucket.size();
    }

    /**
     * Return the ArrayList to be able to iterate over the KBucket.
     * @return The ArrayList of the KBucket.
     */
    protected ArrayList<Node> toArrayList() {
        return this.bucket;
    }
}
