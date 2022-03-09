package toxcore.dht;

import java.util.HashMap;

public class Buckets {

    private int size;
    private final byte[] baseKey;
    private HashMap<Integer, ClientList> bucket;

    Buckets(int size, final byte[] baseKey) {
        this.size = size;
        this.baseKey = baseKey;
        this.bucket = new HashMap<>();
    }


}
