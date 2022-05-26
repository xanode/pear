package fr.xanode.pear.core.dht.network;

import lombok.extern.slf4j.Slf4j;
import fr.xanode.pear.core.dht.DHT;
import fr.xanode.pear.core.dht.async.AsyncTask;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PacketManagementTask implements AsyncTask<Callable<?>> {

    private final byte[] data;
    private final DHT dht;
    private final ConcurrentHashMap<byte[], Callable<?>> trackingSentPackets;

    PacketManagementTask(DHT dht, byte[] data, ConcurrentHashMap<byte[], Callable<?>> trackingSentPackets) {
        this.dht = dht;
        this.data = data;
        this.trackingSentPackets = trackingSentPackets;
    }

    @Override
    public void onPreCall() {
        log.info("Managing new packet!");
    }

    @Override
    public void onPostCall(Callable<?> result) {
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
    public Callable<?> call() throws Exception {
        Packet packet = new Packet(this.dht, this.data);
        switch(packet.getType()) {
            case REQUEST -> // TODO: Add the node in buckets and answer
                    log.info("Managing a request from " + Arrays.toString(packet.getSenderPublicKey()));
            case RESPONSE -> {
                log.info("Managing a response from " + Arrays.toString(packet.getSenderPublicKey()));
                return this.trackingSentPackets.get(packet.getIdentifier());
            }
        }
        return null;
    }
}
