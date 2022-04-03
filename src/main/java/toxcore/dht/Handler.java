package toxcore.dht;

import java.util.concurrent.ConcurrentHashMap;

public abstract class Handler<K, V> {

    private ConcurrentHashMap<K, V> IPCCache;

    protected Handler(){
        this.IPCCache = new ConcurrentHashMap<K, V>();
    }

    protected void handleIPCCall(K key, V value) {
        this.IPCCache.put(key, value);
    }

}
