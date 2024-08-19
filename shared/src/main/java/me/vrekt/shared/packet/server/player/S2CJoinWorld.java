package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Sent from the server to initialize joining a new world or sub-server.
 */
public final class S2CJoinWorld extends GamePacket {

    private int worldId;
    private int entityId;
    private long serverTime;

    public S2CJoinWorld(int worldId, int entityId, long serverTime) {
        this.worldId = worldId;
        this.entityId = entityId;
        this.serverTime = serverTime;
    }

    public S2CJoinWorld(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the world ID
     */
    public int worldId() {
        return worldId;
    }

    /**
     * @return the entity ID to assign to the joining player client side
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return unused
     */
    public long getServerTime() {
        return serverTime;
    }

    @Override
    public int getId() {
        return Packets.S2C_JOIN_WORLD;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(worldId);
        buffer.writeInt(entityId);
        buffer.writeLong(serverTime);
    }

    @Override
    public void decode() {
        worldId = buffer.readInt();
        entityId = buffer.readInt();
        serverTime = buffer.readLong();
    }
}
