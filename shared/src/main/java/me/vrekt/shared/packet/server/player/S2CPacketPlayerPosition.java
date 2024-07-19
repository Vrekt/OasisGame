package me.vrekt.shared.packet.server.player;

import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Update clients on a players position
 */
public final class S2CPacketPlayerPosition extends GamePacket {

    public static final int PACKET_ID = 1119;

    private int entityId, rotation;
    private float x, y;

    public S2CPacketPlayerPosition(int entityId, int rotation, float x, float y) {
        this.entityId = entityId;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    public S2CPacketPlayerPosition(int entityId, int rotation, Vector2 position) {
        this(entityId, rotation, position.x, position.y);
    }

    public S2CPacketPlayerPosition(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return EID
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @return rotation index
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * @return y
     */
    public float getY() {
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
        buffer.writeInt(rotation);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        rotation = buffer.readInt();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }
}
