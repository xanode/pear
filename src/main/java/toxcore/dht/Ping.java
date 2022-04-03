package toxcore.dht;

import java.util.Date;

public class Ping {

    private Node node;
    private byte[] pingId;
    private Date sentDate;
    private Date receivedDate;

    public Ping(Node node, byte[] pingId) {
        this.node = node;
        this.pingId = pingId;
        this.sentDate = new Date();
    }

    /**
     * Get the node associated with this ping.
     * @return the node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Get the ping ID.
     * @return the ping ID
     */
    public byte[] getPingId() {
        return pingId;
    }

    /**
     * Get the date this ping was sent.
     * @return the sent date
     */
    public Date getSentDate() {
        return sentDate;
    }

    /**
     * Get the date this ping was received.
     * @return the received date
     */
    public Date getReceivedDate() {
        return receivedDate;
    }

    /**
     * Set the received date.
     * @param receivedDate the received date
     * @throws IllegalStateException if the ping has already been received
     */
    public void setReceivedDate(Date receivedDate) {
        if (this.receivedDate != null) {
            throw new IllegalStateException("Ping already received");
        }
        this.receivedDate = receivedDate;
    }

    /**
     * Check if the ping has been received.
     * @return true if the ping has been received
     */
    public boolean isReceived() {
        return receivedDate != null;
    }


}
