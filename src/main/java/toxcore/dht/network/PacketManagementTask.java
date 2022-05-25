package toxcore.dht.network;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import lombok.extern.slf4j.Slf4j;
import toxcore.dht.DHT;
import toxcore.dht.async.AsyncTask;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PacketManagementTask implements AsyncTask<Callable> {

    private final byte[] data;
    private final DHT dht;
    private final ConcurrentHashMap<byte[], Callable> trackingSentPackets;

    private Packet packet;

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
        this.packet = new Packet(this.dht, this.data);
        switch(this.packet.getType()) {
            case REQUEST -> {
                // TODO: Add the node in buckets and answer
            }
            case RESPONSE -> {
                return this.trackingSentPackets.get(this.packet.getIdentifier());
            }
        }
        return null;
    }
}
