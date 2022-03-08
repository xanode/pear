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
     * Add a given public key in the client list
     * @param publicKey Key to add in the list
     * @return
     */
    protected byte[] add(byte[] publicKey) {
        return null;
    }
}
