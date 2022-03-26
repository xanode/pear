package toxcore.dht;

import java.util.ArrayList;

public class KBucket {

    private final int k;
    private final ArrayList<Node> bucket;

    protected KBucket(final int k) {
        this.k = k; // Original paper advise 20
        this.bucket = new ArrayList<>();
    }

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

    protected int getSize() {
        return this.bucket.size();
    }

    protected ArrayList<Node> toArrayList() {
        return this.bucket;
    }
}
