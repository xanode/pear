package fr.xanode.pear.core.dht.network;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.Callable;

@Slf4j
@Getter @RequiredArgsConstructor @EqualsAndHashCode
public class Ping implements Callable<Integer> {

    @NonNull private final Node sendingNode;
    @NonNull private final byte[] pingId;
    private Date sentDate;
    private Date receivedDate;

    /**
     * Set the received date.
     * @param receivedDate the received date
     * @throws IllegalStateException if the ping has already been received or if the ping has expired
     * @throws IllegalArgumentException if the received date is incorrect
     */
    public void setReceivedDate(Date receivedDate) {
        if (this.receivedDate != null) {
            throw new IllegalStateException("Ping already received");
        }
        if (isExpired()) {
            throw new IllegalStateException("Ping has expired");
        }
        if (receivedDate.getTime() < sentDate.getTime()) {
            throw new IllegalArgumentException("Received date must be after sent date");
        }
        if (receivedDate.getTime() > new Date().getTime()) {
            throw new IllegalArgumentException("Received date must be in the past");
        }
        this.receivedDate = receivedDate;
    }

    /**
     * Check if the ping has been received.
     * @return true if the ping has been received
     */
    public boolean isPending() {
        return receivedDate == null;
    }

    /**
     * Tell if the ping has expired.
     * @return true if the ping has expired
     */
    public boolean isExpired() {
        return receivedDate != null && receivedDate.getTime() < sentDate.getTime() + Network.PING_TIMEOUT;
    }

    /**
     * Send ping packet.
     * @param type Ping packet type
     * @throws SodiumLibraryException If the cryptographic-related data avoid encrypt the packet
     */
    public void send(PacketType type) throws SodiumLibraryException {
        log.info("Creating packet...");
        Packet packet = new Packet(
                type,
                RPCService.PING,
                this.sendingNode.getNodeKey(),
                this.pingId,
                new byte[0] // A ping packet has an empty payload
        );
        log.info("Packet created.");
        log.info("Sending...");
        this.sendingNode
                .getDht()
                .getNetwork()
                .sendPacket(packet, this.sendingNode, this);
        log.info("Sended.");
        this.sentDate = new Date();
        log.info("Sent date settled.");
    }

    /**
     * Set received date when callback is called.
     */
    @Override
    public Integer call() {
        log.info("Informed that the response has been received via a callback. Setting received date.");
        this.setReceivedDate(new Date());
        return 0;
    }
}
