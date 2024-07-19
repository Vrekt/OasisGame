package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Notify the server this client has loaded a world or interior.
 */
public final class C2SPacketClientLoaded extends GamePacket {

    public static final int PACKET_ID = 2225;

    private ClientLoadedType loadedType;

    public C2SPacketClientLoaded(ByteBuf buffer) {
        super(buffer);
    }

    public C2SPacketClientLoaded(ClientLoadedType loadedType) {
        this.loadedType = loadedType;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(loadedType.ordinal());
    }

    @Override
    public void decode() {
        loadedType = ClientLoadedType.values()[buffer.readInt()];
    }

    public ClientLoadedType loadedType() {
        return loadedType;
    }

    public enum ClientLoadedType {
        WORLD, INTERIOR
    }

}
