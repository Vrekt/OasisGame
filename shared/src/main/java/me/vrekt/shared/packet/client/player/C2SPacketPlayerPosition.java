package me.vrekt.shared.packet.client.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * A players position
 */
public final class C2SPacketPlayerPosition extends GamePacket {

    public static final int PACKET_ID = 2226;

    private int rotation;
    private float x, y;

    public C2SPacketPlayerPosition(float x, float y, int rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public C2SPacketPlayerPosition(ByteBuf buffer) {
        super(buffer);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public int rotation() {
        return rotation;
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
        buffer.writeInt(rotation);
    }

    @Override
    public void decode() {
        x = buffer.readFloat();
        y = buffer.readFloat();
        rotation = buffer.readInt();
    }
}
