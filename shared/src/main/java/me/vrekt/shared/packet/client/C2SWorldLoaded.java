package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Notify the server we have loaded the world.
 */
public final class C2SWorldLoaded extends GamePacket {

    private AtomicInteger worldId;

    public C2SWorldLoaded(ByteBuf buffer) {
        super(buffer);
    }

    public C2SWorldLoaded(int worldId) {
        this.worldId = new AtomicInteger(worldId);
    }

    /**
     * @return the world ID.
     */
    public int worldId() {
        return worldId.get();
    }

    @Override
    public int getId() {
        return Packets.C2S_WORLD_LOADED;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(worldId.get());
    }

    @Override
    public void decode() {
        if (buffer.isReadable(4)) {
            this.worldId = new AtomicInteger(buffer.readInt());
        }
    }
}
