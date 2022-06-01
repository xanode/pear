package fr.xanode.pear.core.dht.network;

import com.muquit.libsodiumjna.SodiumUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.xanode.pear.core.dht.DHT;
import fr.xanode.pear.core.dht.async.AsyncTask;

import java.net.InetAddress;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PacketManagementTask implements AsyncTask<Callable<?>> {

    @NonNull private final DHT dht;
    @NonNull private final byte[] data;
    @NonNull private final InetAddress nodeInetAddress;

    @Override
    public void onPreCall() {
        log.info("Managing new packet!");
    }

    @Override
    public void onPostCall(Callable<?> result) {
        if (result != null) {
            try {
                log.info("Calling callback...");
                result.call();
                log.info("Callback done.");
            } catch (Exception e) {
                log.error("Callback failed!", e);
            }
        } else log.info("No callback.");
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
                                    this.dht.getPublicKey(),
                                    packet.getIdentifier(),
                                    new byte[0]
                            );
                            this.dht.getNetwork().sendPacket(response, node, null);
                            log.info("Response sent.");
                        } else {
                            if (this.dht.isInsertable(node)) { // If the requesting node is closer than at least one of the nodes in the buckets
                                // Add node
                                this.dht.addNode(node);
                                // Send response
                                Packet response = new Packet(
                                        PacketType.RESPONSE,
                                        RPCService.PING,
                                        this.dht.getPublicKey(),
                                        packet.getIdentifier(),
                                        new byte[0]
                                );
                                this.dht.getNetwork().sendPacket(response, node, null);
                                log.info("Response sent.");
                            }
                        }
                    }
                    case RESPONSE -> {
                        log.info("Managing a response from " + SodiumUtils.binary2Hex(packet.getSenderPublicKey()));
                        return this.dht.getNetwork().getCallback(packet.getIdentifier());
                    }
                }
            }
            case FIND_NODE -> log.warn("Packet type FIND_NODE not supported yet.");
            default -> log.error("Packet type " + packet.getType() + " not recognized.");
        }
        return null;
    }
}
