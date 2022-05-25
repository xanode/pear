package toxcore.dht.network;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import toxcore.dht.DHT;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Packet {

    private final PacketType type;
    private final RPCService service;
    private final byte[] senderPublicKey;
    private final byte[] nonce;
    private final byte[] identifier;
    private final byte[] payload;

    /**
     * Create a packet instance from packet information
     * @param type The packet type
     * @param senderPublicKey The sender's public key
     * @param nonce The nonce
     * @param identifier The packet identifier
     * @param payload The packet payload
     */
    public Packet(PacketType type, RPCService service, byte[] senderPublicKey, byte[] nonce, byte[] identifier, byte[] payload) {
        this.type = type;
        this.service = service;
        this.senderPublicKey = senderPublicKey;
        this.nonce = nonce;
        this.identifier = identifier;
        this.payload = payload;
    }

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
        this.senderPublicKey = Arrays.copyOfRange(
                data,
                Network.PACKET_TYPE_LENGTH,
                DHT.CRYPTO_PUBLIC_KEY_SIZE
        );
        this.nonce = Arrays.copyOfRange(
                data,
                Network.PACKET_TYPE_LENGTH + DHT.CRYPTO_PUBLIC_KEY_SIZE,
                Network.PACKET_TYPE_LENGTH + DHT.CRYPTO_PUBLIC_KEY_SIZE + DHT.CRYPTO_NONCE_SIZE
        );
        byte[] packetPayload = dht.decrypt(
                this.senderPublicKey,
                this.nonce,
                Arrays.copyOfRange(
                        data,
                        Network.PACKET_TYPE_LENGTH + DHT.CRYPTO_PUBLIC_KEY_SIZE + DHT.CRYPTO_NONCE_SIZE,
                        data.length
                )
        );
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
    }

    /**
     * Return the packet as a byte array ready for sending over the network.
     * @param dht The DHT instance to use for encryption
     * @param receiverPublicKey The receiver's public key
     * @return The packet as a byte array
     * @throws SodiumLibraryException If the cryptographic-related data avoid encrypt the packet
     */
    public byte[] toByteArray(DHT dht, byte[] receiverPublicKey) throws SodiumLibraryException {
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
        byte[] encryptedPayload = dht.encrypt(this.senderPublicKey, dht.generateNonce(), packetPayload);

        return ByteBuffer.allocate(Network.PACKET_TYPE_LENGTH + DHT.CRYPTO_PUBLIC_KEY_SIZE + DHT.CRYPTO_NONCE_SIZE + encryptedPayload.length)
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

    /**
     * Get the packet type.
     * @return The packet type
     */
    public PacketType getType() {
        return this.type;
    }

    /**
     * Get the packet service.
     * @return The packet service.
     */
    public RPCService getService() {
        return this.service;
    }

    /**
     * Get the sender's public key.
     * @return The sender's public key
     */
    public byte[] getSenderPublicKey() {
        return this.senderPublicKey;
    }

    /**
     * Get the packet's nonce.
     * @return The packet's nonce
     */
    public byte[] getNonce() {
        return this.nonce;
    }

    /**
     * Get the packet's identifier.
     * @return The packet's identifier
     */
    public byte[] getIdentifier() {
        return this.identifier;
    }

    /**
     * Get the packet's payload.
     * @return The packet's payload
     */
    public byte[] getPayload() {
        return this.payload;
    }

    /**
     * Tell if the packet equals another object.
     * @param o The object to compare
     * @return true if the object equals the packet, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Packet other)) return false;
        return this.type == other.getType()
                && this.service == other.getService()
                && this.senderPublicKey == other.senderPublicKey
                && this.identifier == other.identifier
                && this.payload == other.getPayload();
    }

    /**
     * Compute the hash code of the packet.
     * @return the hash code of the packet
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.senderPublicKey == null) ? 0 : Arrays.hashCode(this.senderPublicKey));
        result = prime * result + ((this.identifier == null) ? 0 : Arrays.hashCode(this.identifier));
        result = prime * result + ((this.payload == null) ? 0 : Arrays.hashCode(this.payload));
        return result;
    }
}
