package fr.xanode.pear.core.dht.network;

import com.muquit.libsodiumjna.SodiumUtils;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import fr.xanode.pear.core.dht.DHT;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Slf4j
@Getter @RequiredArgsConstructor @EqualsAndHashCode
public class Packet {

    @NonNull private final PacketType type;
    @NonNull private final RPCService service;
    @NonNull private final byte[] senderPublicKey;
    @NonNull private final byte[] identifier;
    @NonNull private final byte[] payload;

    /**
     * Create a packet instance from a real packet received from the network.
     * @param dht The DHT instance
     * @param data The packet data
     * @throws IllegalArgumentException If the packet type is invalid
     * @throws SodiumLibraryException If the cryptographic-related data avoid decrypt the encrypted part of the packet
     */
    public Packet(DHT dht, byte[] data) throws IllegalArgumentException, SodiumLibraryException {
        /*
         * Structure of a packet: [type][sender_public_key][nonce][*type][*identifier][*payload] (* = encrypted)
         *
         * - Packet type (1 byte)
         * - Sender public key (32 bytes)
         * - Random nonce (24 bytes)
         * (Encrypted part)
         * - Packet type (1 byte)
         * - Identifier (8 bytes)
         * - Payload
         */
        log.info("Decoding packet...");
        this.senderPublicKey = Arrays.copyOfRange(
                data,
                Network.PACKET_TYPE_LENGTH,
                DHT.CRYPTO_KEY_SIZE
        );
        byte[] nonce = Arrays.copyOfRange(
                data,
                Network.PACKET_TYPE_LENGTH + DHT.CRYPTO_KEY_SIZE,
                Network.PACKET_TYPE_LENGTH + DHT.CRYPTO_KEY_SIZE + DHT.CRYPTO_NONCE_SIZE
        );
        log.info("Decrypting payload...");
        byte[] packetPayload = dht.decrypt(
                this.senderPublicKey,
                nonce,
                Arrays.copyOfRange(
                        data,
                        Network.PACKET_TYPE_LENGTH + DHT.CRYPTO_KEY_SIZE + DHT.CRYPTO_NONCE_SIZE,
                        data.length
                )
        );
        log.info("Decrypted.");
        if (data[Network.PACKET_TYPE_LENGTH - 1] != packetPayload[Network.PACKET_TYPE_LENGTH - 1]) {
            throw new IllegalArgumentException("Packet type mismatch");
        }
        switch (data[Network.PACKET_TYPE_LENGTH - 1]) {
            case Network.PACKET_PING_REQUEST_TYPE -> {
                this.type = PacketType.REQUEST;
                this.service = RPCService.PING;
            }
            case Network.PACKET_PING_RESPONSE_TYPE -> {
                this.type = PacketType.RESPONSE;
                this.service = RPCService.PING;
            }
            case Network.PACKET_FIND_NODE_REQUEST_TYPE -> {
                this.type = PacketType.REQUEST;
                this.service = RPCService.FIND_NODE;
            }
            case Network.PACKET_FIND_NODE_RESPONSE_TYPE -> {
                this.type = PacketType.RESPONSE;
                this.service = RPCService.FIND_NODE;
            }
            default -> throw new IllegalArgumentException("Invalid packet type");
        }
        this.identifier = Arrays.copyOfRange(
                packetPayload,
                Network.PACKET_TYPE_LENGTH,
                Network.PACKET_TYPE_LENGTH + Network.ID_LENGTH
        );
        this.payload = Arrays.copyOfRange(
                packetPayload,
                Network.PACKET_TYPE_LENGTH + Network.ID_LENGTH,
                packetPayload.length
        );
        log.info("Packet decoded.");
    }

    /**
     * Return the packet as a byte array ready for sending over the network.
     * @param dht The DHT instance to use for encryption
     * @param receiverPublicKey The receiver's public key
     * @return The packet as a byte array
     * @throws SodiumLibraryException If the cryptographic-related data avoid encrypt the packet
     */
    public byte[] toByteArray(DHT dht, byte[] receiverPublicKey) throws SodiumLibraryException {
        log.info("Transforming packet " + SodiumUtils.binary2Hex(this.identifier) + " into byte array...");
        // Encrypt
        byte type;
        if (this.type == PacketType.REQUEST && this.service == RPCService.PING) {
            type = Network.PACKET_PING_REQUEST_TYPE;
        } else if (this.type == PacketType.RESPONSE && this.service == RPCService.PING) {
            type = Network.PACKET_PING_RESPONSE_TYPE;
        } else if (this.type == PacketType.REQUEST && this.service == RPCService.FIND_NODE) {
            type = Network.PACKET_FIND_NODE_REQUEST_TYPE;
        } else {
            type = Network.PACKET_FIND_NODE_RESPONSE_TYPE;
        }
        byte[] packetPayload = ByteBuffer.allocate(Network.PACKET_TYPE_LENGTH + Network.ID_LENGTH + this.payload.length)
                .put(type)
                .put(this.identifier)
                .put(this.payload)
                .array();
        log.info("Encrypting payload...");
        byte[] nonce = dht.generateNonce();
        byte[] encryptedPayload = dht.encrypt(this.senderPublicKey, nonce, packetPayload);
        log.info("Payload encrypted.");

        log.info("Packet " + SodiumUtils.binary2Hex(this.identifier) + " transformed into a byte array (just before return).");

        return ByteBuffer.allocate(Network.PACKET_TYPE_LENGTH + DHT.CRYPTO_KEY_SIZE + DHT.CRYPTO_NONCE_SIZE + encryptedPayload.length)
                .put(type)
                .put(receiverPublicKey)
                .put(nonce)
                .put(encryptedPayload)
                .array();
    }

    /**
     * Return the packet as a byte array ready for sending over the network.
     * @param dht The DHT instance to use for encryption
     * @return The packet as a byte array
     * @throws SodiumLibraryException If the cryptographic-related data avoid encrypt the packet
     */
    public byte[] toByteArray(DHT dht) throws SodiumLibraryException {
        return this.toByteArray(dht, this.senderPublicKey);
    }
}
