package fr.xanode.pear.core.dht;

import com.muquit.libsodiumjna.SodiumUtils;
import fr.xanode.pear.core.dht.buckets.KBucket;
import fr.xanode.pear.core.dht.network.Node;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class CheckBucketsAgent implements Runnable {

    public static final long CHECK_NODE_INTERVAL = 25000; // ms
    public static final int CHECK_ATTEMPTS = 3;
    public static final long CHECK_ATTEMPT_INTERVAL = 1000; // ms

    @NonNull private final DHT dht;
    private boolean running;

    @Override
    public void run() {
        this.running = true;
        while (running) {
            // Go through every KBucket
            log.info("Running new check");
            for (int i=0; i<8 * DHT.CRYPTO_KEY_SIZE - 1; i++) {
                KBucket bucket = this.dht.getBuckets().getBucket(i);
                //log.info(String.valueOf(this.dht.getBuckets().getBucket(i).getNode(0).getLastCheck()));
                if (bucket.getSize() == 0) continue; // Empty bucket, skipping
                for (int j=0; j < bucket.getSize(); j++) {
                    Node node = bucket.getNode(j);
                    if (bucket.getLastCheck(node) == null) { // Node waiting for response, skipping
                        log.info("Skipping node " + SodiumUtils.binary2Hex(bucket.getNode(j).getNodeKey()) + " (lc=" + bucket.getLastCheck(node) + ")");
                        continue;
                    }
                    if ((new Date().getTime() - bucket.getLastCheck(node).getTime()) > CHECK_NODE_INTERVAL) {
                        log.info("Checking node " + SodiumUtils.binary2Hex(bucket.getNode(j).getNodeKey()));
                        int counter = 0;
                        boolean alive;
                        while (!(alive = bucket.getNode(j).isAlive()) && counter < CHECK_ATTEMPTS) {
                            log.info("Attempt " + counter + 1 + "/" + CHECK_ATTEMPTS + "...");
                            synchronized (this) {
                                try {
                                    this.wait(CHECK_ATTEMPT_INTERVAL);
                                } catch (InterruptedException e) {
                                    log.warn("Interrupted while waiting during check attempt (" + e.getMessage() + "), stopped");
                                    break;
                                }
                            }
                            counter++;
                        }
                        if (!alive) {
                            log.info("Node " + SodiumUtils.binary2Hex(bucket.getNode(j).getNodeKey()) + " not alive after " + CHECK_ATTEMPTS + " attempts: remove it.");
                            bucket.remove(bucket.getNode(j));
                        } else log.info("Node " + SodiumUtils.binary2Hex(bucket.getNode(j).getNodeKey()) + " alive.");
                    }
                    if (!running) break;
                }
                if (!running) break;
            }
            //if ((new Date().getTime() - beginning.getTime()) > (new Date().getTime() - earliestCheck.getTime()))
                synchronized (this) {
                    try {
                        log.info("Waiting for " + CHECK_NODE_INTERVAL + "ms before new check");
                        this.wait(CHECK_NODE_INTERVAL);
                    } catch (InterruptedException e) {
                        log.warn("Interrupted while waiting before new check (" + e.getMessage() + "), stopped");
                        break;
                    }
                }
        }
    }

    public void close() {
        running = false;
    }
}
