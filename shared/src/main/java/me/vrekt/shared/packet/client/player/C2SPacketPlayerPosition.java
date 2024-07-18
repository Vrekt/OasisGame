package me.vrekt.shared.packet.client.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.codec.C2SPacketHandler;
import me.vrekt.shared.packet.GamePacket;

/**
 * A players position
 */
public final class C2SPacketPlayerPosition extends GamePacket {

    public static final int PACKET_ID = 2226;

    private float rotation, x, y;

    public static void handle(C2SPacketHandler handler, ByteBuf buffer) {
        handler.handle(new C2SPacketPlayerPosition(buffer));
    }

    public C2SPacketPlayerPosition(float x, float y, float rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    private C2SPacketPlayerPosition(ByteBuf buffer) {
        super(buffer);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float rotation() {
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
        buffer.writeFloat(rotation);
    }

    @Override
    public void decode() {
        x = buffer.readFloat();
        y = buffer.readFloat();
        rotation = buffer.readFloat();
    }
}
