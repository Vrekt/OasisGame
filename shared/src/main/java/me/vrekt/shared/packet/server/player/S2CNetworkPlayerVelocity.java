package me.vrekt.shared.packet.server.player;

import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Update clients on a players velocity
 */
public final class S2CNetworkPlayerVelocity extends GamePacket {

    private int entityId, rotation;
    private float x, y;

    public S2CNetworkPlayerVelocity(int entityId, int rotation, float x, float y) {
        this.entityId = entityId;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    public S2CNetworkPlayerVelocity(int entityId, int rotation, Vector2 velocity) {
        this(entityId, rotation, velocity.x, velocity.y);
    }

    public S2CNetworkPlayerVelocity(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return EID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return x vel
     */
    public float x() {
        return x;
    }

    /**
     * @return y vel
     */
    public float y() {
        return y;
    }

    /**
     * @return the rotation value
     */
    public int rotation() {
        return rotation;
    }

    @Override
    public int getId() {
        return Packets.S2C_PLAYER_VELOCITY;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeInt(rotation);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        x = buffer.readFloat();
        y = buffer.readFloat();
        rotation = buffer.readInt();
    }
}
