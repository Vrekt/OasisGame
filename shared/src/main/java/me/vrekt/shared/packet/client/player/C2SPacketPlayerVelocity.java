package me.vrekt.shared.packet.client.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.codec.C2SPacketHandler;
import me.vrekt.shared.packet.GamePacket;

public final class C2SPacketPlayerVelocity extends GamePacket {

    public static final int PACKET_ID = 2227;

    private float velocityX, velocityY;
    private int rotation;

    public static void handle(C2SPacketHandler handler, ByteBuf buffer) {
        handler.handle(new C2SPacketPlayerVelocity(buffer));
    }

    public C2SPacketPlayerVelocity(float velocityX, float velocityY, int rotation) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotation = rotation;
    }

    private C2SPacketPlayerVelocity(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return x vel
     */
    public float x() {
        return velocityX;
    }

    /**
     * @return y vel
     */
    public float y() {
        return velocityY;
    }

    /**
     * @return the rotation value
     */
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
        buffer.writeFloat(velocityX);
        buffer.writeFloat(velocityY);
        buffer.writeInt(rotation);
    }

    @Override
    public void decode() {
        velocityX = buffer.readFloat();
        velocityY = buffer.readFloat();
        rotation = buffer.readInt();
    }
}
