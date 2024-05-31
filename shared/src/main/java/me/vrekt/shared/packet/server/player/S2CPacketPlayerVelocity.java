package me.vrekt.shared.packet.server.player;

import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.S2CPacketHandler;

/**
 * Update clients on a players velocity
 */
public final class S2CPacketPlayerVelocity extends GamePacket {

    public static final int PACKET_ID = 1120;

    private int entityId;
    private float x, y, rotation;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPacketPlayerVelocity(buffer));
    }

    public S2CPacketPlayerVelocity(int entityId, float rotation, float x, float y) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public S2CPacketPlayerVelocity(int entityId, float rotation, Vector2 velocity) {
        this(entityId, rotation, velocity.x, velocity.y);
    }

    private S2CPacketPlayerVelocity(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return EID
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @return x vel
     */
    public float getX() {
        return x;
    }

    /**
     * @return y vel
     */
    public float getY() {
        return y;
    }

    /**
     * @return the rotation value
     */
    public float getRotation() {
        return rotation;
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
        buffer.writeFloat(rotation);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        x = buffer.readFloat();
        y = buffer.readFloat();
        rotation = buffer.readFloat();
    }
}
