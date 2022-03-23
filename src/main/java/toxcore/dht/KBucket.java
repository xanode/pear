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
            if (this.bucket.size() < k) {
                this.bucket.add(node);
            } else {
                // Should check if this.bucket.get(k-1) is still alive
                // if not, replace it
                // else discard node
            }
        }

    }
}
