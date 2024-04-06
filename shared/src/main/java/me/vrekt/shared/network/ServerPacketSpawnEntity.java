package me.vrekt.shared.network;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.v2.packet.GamePacket;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.entities.EntityType;

/**
 * Sent to clients to spawn an entity
 */
public final class ServerPacketSpawnEntity extends GamePacket {

    public static final int ID = 2001_1;

    private int entityId;
    private EntityType type;
    private Vector2 position;

    public ServerPacketSpawnEntity(int entityId, EntityType type, Vector2 position) {
        this.entityId = entityId;
        this.type = type;
        this.position = position;
    }

    public ServerPacketSpawnEntity(ByteBuf buffer) {
        super(buffer);
    }

    public EntityType getType() {
        return type;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeInt(type.getEntityTypeId());
        buffer.writeFloat(position.x);
        buffer.writeFloat(position.y);
    }

    @Override
    public void decode() {
        this.entityId = buffer.readInt();
        this.type = EntityType.of(buffer.readInt());
        this.position = new Vector2();
        this.position.x = buffer.readFloat();
        this.position.y = buffer.readFloat();
    }

    @Override
    public int getId() {
        return ID;
    }
}