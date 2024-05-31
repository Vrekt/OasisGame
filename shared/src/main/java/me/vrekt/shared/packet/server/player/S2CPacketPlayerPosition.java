package me.vrekt.shared.packet.server.player;

import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.S2CPacketHandler;

/**
 * Update clients on a players position
 */
public final class S2CPacketPlayerPosition extends GamePacket {

    public static final int PACKET_ID = 1119;

    private int entityId;
    private float x, y, rotation;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPacketPlayerPosition(buffer));
    }

    public S2CPacketPlayerPosition(int entityId, float rotation, float x, float y) {
        this.entityId = entityId;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    public S2CPacketPlayerPosition(int entityId, float rotation, Vector2 position) {
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
    public float getRotation() {
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
        buffer.writeFloat(rotation);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        rotation = buffer.readFloat();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }
}
