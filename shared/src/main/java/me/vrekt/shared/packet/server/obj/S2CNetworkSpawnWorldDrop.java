package me.vrekt.shared.packet.server.obj;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.Items;
import me.vrekt.shared.packet.GamePacket;

/**
 * Spawn a world drop.
 */
public final class S2CNetworkSpawnWorldDrop extends GamePacket {

    public static final int PACKET_ID = 3000_11;

    private Item item;
    private Vector2 position;
    private int objectId;

    public S2CNetworkSpawnWorldDrop(ByteBuf buffer) {
        super(buffer);
    }

    public S2CNetworkSpawnWorldDrop(Item item, Vector2 position, int objectId) {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(position);
        Preconditions.checkArgument(objectId != 0);

        this.item = item;
        this.position = position;
        this.objectId = objectId;
    }

    /**
     * @return item
     */
    public Item item() {
        return item;
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
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(objectId);
        writeString(item.type().name());
        buffer.writeInt(item.amount());
        writeVector2(position.x, position.y);
    }

    @Override
    public void decode() {
        objectId = buffer.readInt();

        final String of = readString();
        final Items type = Items.valueOf(of);
        final int amount = buffer.readInt();
        final float x = buffer.readFloat();
        final float y = buffer.readFloat();

        this.position = new Vector2(x, y);
        this.item = ItemRegistry.createItem(type, amount);
    }
}
