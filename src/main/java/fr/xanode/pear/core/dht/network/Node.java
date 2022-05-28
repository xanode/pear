package fr.xanode.pear.core.dht.network;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import fr.xanode.pear.core.dht.DHT;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Getter @EqualsAndHashCode
public class Node {

    private final DHT dht;
    private final byte[] nodeKey;
    private final InetAddress nodeAddress;
    private final int port;

    public Node(final DHT dht, final byte[] nodeKey, final InetAddress nodeAddress, final int port) {
        if (nodeAddress.isMulticastAddress()) {
            // Do not accept multicast address since it cannot represent a node
            throw new IllegalArgumentException("Multicast address not allowed");
        }
        this.dht = dht;
        this.nodeKey = nodeKey;
        this.nodeAddress = nodeAddress;
        this.port = port;
    }

    /**
     * Tell if a node is alive.
     * @return True if the node is alive, false either.
     */
    public boolean isAlive() {
        log.info("Testing if this node is alive...");
        int counter = 1;
        while (this.dht.getNetwork().pingSocket == null && counter < 21) { // Check if we can send packets through network
            try {
                log.error("Socket is still not open! Waiting 100ms (" + counter + "/20)...");
                synchronized (this) {
                    this.wait(100);
                }
                counter++;
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting for socket to be opened: " + e.getMessage());
            }
        }
        if (this.dht.getNetwork().pingSocket == null) { // If we can't, abort
            log.error("Socket is still not open. Aborting.");
        } else { // Otherwise, let's go!
            if (this.nodeAddress.isAnyLocalAddress()) {
                log.info("It is a local node (probably me)!");
                return false;
            } else {
                log.info("Preparing a ping...");
                byte[] pingId = new byte[Network.ID_LENGTH];
                new SecureRandom().nextBytes(pingId);
                Ping ping = new Ping(this, pingId);
                log.info("Ping prepared.");
                try {
                    log.info("Sending the ping...");
                    ping.send(PacketType.REQUEST); // Send ping
                    log.info("Ping sent.");
                    log.info("Waiting for response...");
                    while (ping.isPending() && ((new Date()).getTime() - ping.getSentDate().getTime()) < Network.PING_TIMEOUT) { // Waiting for response
                        try {
                            synchronized (this) {
                                this.wait(100); // Wait 100ms
                            }
                        } catch (InterruptedException e) {
                            log.warn("Interrupted while waiting for ping response: " + e.getMessage());
                        }
                    }
                    if (ping.isPending()) {
                        log.info("Ping timeout.");
                        return false;
                    }
                } catch (SodiumLibraryException e) {
                    e.printStackTrace(); // Should never happen
                }
                log.info("This node is alive!");
                return true;
            }
        }
        return false;
    }
}
