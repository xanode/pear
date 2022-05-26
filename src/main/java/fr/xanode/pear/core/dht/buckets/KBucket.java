package fr.xanode.pear.core.dht.buckets;

import fr.xanode.pear.core.dht.network.Node;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@Getter @RequiredArgsConstructor
public class KBucket {

    @NonNull private final int k; // Original paper advise 20
    private final ArrayList<Node> bucket = new ArrayList<>();

    /**
     * Update this KBucket with a new node.
     * @param node The node to update the KBucket.
     */
    public void update(Node node) {
        log.info("Updating bucket with new node...");
        // If the node already exist in the bucket, move it to the tail of the list
        if (this.bucket.contains(node)) {
            log.info("Node already exists in the bucket.");
            log.info("Moving node to the tail of the bucket...");
            this.bucket.remove(node);
            this.bucket.add(node);
            log.info("Node moved to the tail of the list.");
        } else {
            // If not in the bucket and the bucket isn't full, insert it at the tail
            if (this.bucket.size() < this.k) {
                log.info("The node isn't in the bucket and the bucket isn't full.");
                log.info("Adding node...");
                this.bucket.add(node);
                log.info("Node added.");
            } else {
                // If the bucket is full
                log.info("The node isn't in the bucket but the bucket is full.");
                log.info("Testing if the node is alive...");
                if (!this.bucket.get(this.k-1).isAlive()) {
                    // Replace last node if it isn't alive anymore
                    log.info("The node seems alive, adding it.");
                    this.bucket.set(this.k-1, node);
                    log.info("Node added.");
                }
            }
        }

    }

    /**
     * Get the actual size of the KBucket.
     * @return The size of the KBucket.
     */
    public int getSize() {
        return this.bucket.size();
    }

    /**
     * Returns if the node is in the KBucket.
     * @param node The node to check.
     * @return True if the node is in the KBucket, false otherwise.
     */
    public Node contains(Node node) {
        for (Node n : this.bucket) {
            if (n.equals(node)) {
                return n;
            }
        }
        return null;
    }
}
