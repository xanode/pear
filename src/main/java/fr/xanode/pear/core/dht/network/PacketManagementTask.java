package fr.xanode.pear.core.dht.network;

import com.muquit.libsodiumjna.SodiumUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.xanode.pear.core.dht.DHT;
import fr.xanode.pear.core.dht.async.AsyncTask;

import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class PacketManagementTask implements AsyncTask<Callable<?>> {

    @NonNull private final DHT dht;
    @NonNull private final byte[] data;
    @NonNull private final InetAddress nodeInetAddress;
    @NonNull private final ConcurrentHashMap<byte[], Callable<?>> trackingSentPackets;

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
        switch (packet.getService()) {
            case PING -> {
                switch (packet.getType()) {
                    case REQUEST -> {
                        log.info("Managing a request from " + SodiumUtils.binary2Hex(packet.getSenderPublicKey()));
                        Node node = new Node(
                                this.dht,
                                packet.getSenderPublicKey(),
                                this.nodeInetAddress,
                                Network.DHT_PORT
                        );
                        if (this.dht.isNodeKnown(node) != null) { // If node is already known
                            // Send response
                            Packet response = new Packet(
                                    PacketType.RESPONSE,
                                    RPCService.PING,
                                    packet.getSenderPublicKey(),
                                    packet.getIdentifier(),
                                    new byte[0]
                            );
                            this.dht.getNetwork().sendPacket(response, node, null);
                        } else {
                            if (this.dht.isInsertable(node)) { // If the requesting node is closer than at least one of the nodes in the buckets
                                // Add node
                                this.dht.addNode(node);
                                // Send response
                                Packet response = new Packet(
                                        PacketType.RESPONSE,
                                        RPCService.PING,
                                        packet.getSenderPublicKey(),
                                        packet.getIdentifier(),
                                        new byte[0]
                                );
                                this.dht.getNetwork().sendPacket(response, node, null);
                            }
                        }
                    }
                    case RESPONSE -> {
                        log.info("Managing a response from " + SodiumUtils.binary2Hex(packet.getSenderPublicKey()));
                        return this.trackingSentPackets.get(packet.getIdentifier());
                    }
                }
            }
            case FIND_NODE -> log.warn("Packet type FIND_NODE not supported yet.");
            default -> log.error("Packet type " + packet.getType() + " not recognized.");
        }
        return null;
    }
}
