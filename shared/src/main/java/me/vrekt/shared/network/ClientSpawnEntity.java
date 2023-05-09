package me.vrekt.shared.network;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.entities.EntityType;

/**
 * Request to spawn an entity
 */
public final class ClientSpawnEntity extends Packet {

    public static final int ID = 2001_0;

    private EntityType type;
    private Vector2 position;

    public ClientSpawnEntity(EntityType type, Vector2 position) {
        this.type = type;
        this.position = position;
    }

    public ClientSpawnEntity(ByteBuf buffer) {
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
        buffer.writeInt(type.getEntityTypeId());
        buffer.writeFloat(position.x);
        buffer.writeFloat(position.y);
    }

    @Override
    public void decode() {
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
