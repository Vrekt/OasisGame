package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Tell the client to teleport to a position
 */
public final class S2CTeleport extends GamePacket {

    public static final int PACKET_ID = 1124;
    private float x, y;

    public S2CTeleport(ByteBuf buffer) {
        super(buffer);
    }

    public S2CTeleport(float x, float y) {
        this.x = x;
        this.y = y;
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
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        x = buffer.readFloat();
        y = buffer.readFloat();
    }
}
