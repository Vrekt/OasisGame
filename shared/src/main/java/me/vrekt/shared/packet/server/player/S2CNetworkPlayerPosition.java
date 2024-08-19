package me.vrekt.shared.packet.server.player;

import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Update clients on a players position
 */
public final class S2CNetworkPlayerPosition extends GamePacket {

    private int entityId, rotation;
    private float x, y;

    public S2CNetworkPlayerPosition(int entityId, int rotation, float x, float y) {
        this.entityId = entityId;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    public S2CNetworkPlayerPosition(int entityId, int rotation, Vector2 position) {
        this(entityId, rotation, position.x, position.y);
    }

    public S2CNetworkPlayerPosition(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return EID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return rotation index
     */
    public int rotation() {
        return rotation;
    }

    /**
     * @return x
     */
    public float x() {
        return x;
    }

    /**
     * @return y
     */
    public float y() {
        return y;
    }

    @Override
    public int getId() {
        return Packets.S2C_PLAYER_POSITION;
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
