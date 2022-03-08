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

}
