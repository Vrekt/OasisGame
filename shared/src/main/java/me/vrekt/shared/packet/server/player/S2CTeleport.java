package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Tell the client to teleport to a position
 */
public final class S2CTeleport extends GamePacket {

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
        return Packets.S2C_TELEPORT;
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
