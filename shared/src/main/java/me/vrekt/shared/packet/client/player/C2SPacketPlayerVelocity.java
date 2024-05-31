package me.vrekt.shared.packet.client.player;

import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.C2SPacketHandler;

public final class C2SPacketPlayerVelocity extends GamePacket {

    public static final int PACKET_ID = 2227;

    private float velocityX, velocityY, rotation;

    public static void handle(C2SPacketHandler handler, ByteBuf buffer) {
        handler.handle(new C2SPacketPlayerVelocity(buffer));
    }

    public C2SPacketPlayerVelocity(float velocityX, float velocityY, float rotation) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotation = rotation;
    }

    public C2SPacketPlayerVelocity(Vector2 velocity, float rotation) {
        this(velocity.x, velocity.y, rotation);
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
        buffer.writeFloat(velocityX);
        buffer.writeFloat(velocityY);
        buffer.writeFloat(rotation);
    }

    @Override
    public void decode() {
        velocityX = buffer.readFloat();
        velocityY = buffer.readFloat();
        rotation = buffer.readFloat();
    }
}
