package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Notify players that a player teleported.
 */
public final class S2CTeleportPlayer extends GamePacket {

    public static final int PACKET_ID = 1124;
    private int entityId;
    private float x, y;

    public S2CTeleportPlayer(ByteBuf buffer) {
        super(buffer);
    }

    public S2CTeleportPlayer(int entityId, float x, float y) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
    }

    public int entityId() {
        return entityId;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }

}
