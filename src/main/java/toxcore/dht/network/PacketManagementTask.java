package toxcore.dht.network;

import lombok.extern.slf4j.Slf4j;
import toxcore.dht.DHT;
import toxcore.dht.async.AsyncTask;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PacketManagementTask implements AsyncTask<Callable> {

    private final byte[] data;
    private final DHT dht;
    private final ConcurrentHashMap<byte[], Callable> trackingSentPackets;

    PacketManagementTask(DHT dht, byte[] data, ConcurrentHashMap<byte[], Callable> trackingSentPackets) {
        this.dht = dht;
        this.data = data;
        this.trackingSentPackets = trackingSentPackets;
    }

    @Override
    public void onPreCall() {
        log.info("Managing new packet!");
    }

    @Override
    public void onPostCall(Callable result) {
        if (result != null) {
            try {
                result.call();
            } catch (Exception e) {
                log.error("Callback failed!", e);
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error while managing packet!", throwable);
    }

    @Override
    public Callable call() throws Exception {
        Packet packet = new Packet(this.dht, this.data);
        switch(packet.getType()) {
            case REQUEST -> {
                log.info("Managing a request from " + Arrays.toString(packet.getSenderPublicKey()));
                // TODO: Add the node in buckets and answer
            }
            case RESPONSE -> {
                log.info("Managing a response from " + Arrays.toString(packet.getSenderPublicKey()));
                return this.trackingSentPackets.get(packet.getIdentifier());
            }
        }
        return null;
    }
}
