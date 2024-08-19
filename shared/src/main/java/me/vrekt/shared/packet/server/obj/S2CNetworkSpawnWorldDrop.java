package me.vrekt.shared.packet.server.obj;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.Items;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Spawn a world drop.
 */
public final class S2CNetworkSpawnWorldDrop extends GamePacket {

    private Items item;
    private int amount;
    private Vector2 position;
    private int objectId;

    public S2CNetworkSpawnWorldDrop(ByteBuf buffer) {
        super(buffer);
    }

    public S2CNetworkSpawnWorldDrop(Items item, int amount, Vector2 position, int objectId) {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(position);
        Preconditions.checkArgument(objectId != -1);

        this.item = item;
        this.amount = amount;
        this.position = position;
        this.objectId = objectId;
    }

    public S2CNetworkSpawnWorldDrop(Item item, Vector2 position, int objectId) {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(position);
        Preconditions.checkArgument(objectId != -1);

        this.item = item.type();
        this.amount = item.amount();
        this.position = position;
        this.objectId = objectId;
    }

    /**
     * @return the item type
     */
    public Items item() {
        return item;
    }

    /**
     * @return the amount of the item
     */
    public int amount() {
        return amount;
    }

    /**
     * @return dropped item position
     */
    public Vector2 position() {
        return position;
    }

    /**
     * @return object ID
     */
    public int objectId() {
        return objectId;
    }

    @Override
    public int getId() {
        return Packets.S2C_CREATE_WORLD_DROP;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(objectId);
        buffer.writeInt(item.ordinal());
        buffer.writeInt(amount);
        writeVector2(position.x, position.y);
    }

    @Override
    public void decode() {
        objectId = buffer.readInt();

        final int ordinal = buffer.readInt();
        this.item = Items.values()[ordinal];
        this.amount = buffer.readInt();

        final float x = buffer.readFloat();
        final float y = buffer.readFloat();
        this.position = new Vector2(x, y);
    }
}
