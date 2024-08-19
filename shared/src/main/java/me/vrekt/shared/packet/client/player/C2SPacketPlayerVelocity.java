package me.vrekt.shared.packet.client.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

public final class C2SPacketPlayerVelocity extends GamePacket {

    private float velocityX, velocityY;
    private int rotation;

    public C2SPacketPlayerVelocity(float velocityX, float velocityY, int rotation) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotation = rotation;
    }

    public C2SPacketPlayerVelocity(ByteBuf buffer) {
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
        return Packets.C2S_VELOCITY;
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
