package toxcore.dht;

import java.util.ArrayList;

public class ClientList {

    private final int size;
    private final byte[] baseKey;
    private ArrayList<byte[]> clientList;

    ClientList(final int size, final byte[] baseKey) {
        this.size = size;
        this.baseKey = baseKey;
    }

    /**
     * Add a given public key in the client list.
     * @param publicKey Key to add in the list.
     * @return true if it has been inserted in the list, false otherwise.
     */
    protected boolean add(byte[] publicKey) {
        // Check if list is full
        if (this.clientList.size() >= this.size) {
            return false;
        }
        for (int i=0; i<this.clientList.size(); i++) {
            if (DHT.getClosest(this.baseKey, this.clientList.get(i), publicKey)) {
                this.clientList.set(i, publicKey);
                return true;
            }
        }
        this.clientList.add(publicKey);
        return true;
    }

    /**
     * Remove a given public key in the client list.
     * @param publicKey Key to remove.
     * @return true if it has been removed, false otherwise.
     */
    protected boolean remove(byte[] publicKey) {
        return this.clientList.remove(publicKey);
    }
}
