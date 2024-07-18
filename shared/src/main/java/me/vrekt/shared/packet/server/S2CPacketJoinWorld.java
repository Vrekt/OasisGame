package me.vrekt.shared.packet.server;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.S2CPacketHandler;

/**
 * Sent from the server to initialize joining a new world or sub-server.
 */
public final class S2CPacketJoinWorld extends GamePacket {

    public static final int PACKET_ID = 1114;

    private int worldId;
    private int entityId;
    private long serverTime;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPacketJoinWorld(buffer));
    }

    public S2CPacketJoinWorld(int worldId, int entityId, long serverTime) {
        this.worldId = worldId;
        this.entityId = entityId;
        this.serverTime = serverTime;
    }

    public S2CPacketJoinWorld(ByteBuf buffer) {
        super(buffer);
    }

    public int worldId() {
        return worldId;
    }

    public int getEntityId() {
        return entityId;
    }

    public long getServerTime() {
        return serverTime;
    }

    @Override
    public int getId() {
        return PACKET_ID;
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
