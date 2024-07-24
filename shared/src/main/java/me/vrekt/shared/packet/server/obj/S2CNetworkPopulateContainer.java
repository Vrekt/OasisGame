package me.vrekt.shared.packet.server.obj;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.item.Item;
import me.vrekt.shared.packet.GamePacket;

/**
 * Spawn and populate a container.
 */
public class S2CNetworkPopulateContainer extends GamePacket {

    public static final int PACKET_ID = 3000_12;

    private int size;
    private Item[] contents;
    private String containerType;
    private Vector2 position;

    public S2CNetworkPopulateContainer(ByteBuf buffer) {
        super(buffer);
    }

    public S2CNetworkPopulateContainer(ContainerInventory inventory, String containerType, Vector2 position) {
        Preconditions.checkNotNull(inventory);
        Preconditions.checkNotNull(position);
        Preconditions.checkNotNull(containerType);

        this.position = position;
        this.containerType = containerType;
        this.size = inventory.getSize();

        contents = new Item[inventory.getSize()];
        for (int i = 0; i < inventory.getSize(); i++) {
            contents[i] = inventory.get(i);
        }
    }

    public S2CNetworkPopulateContainer(Item[] contents) {
        this.contents = contents;
    }

    /**
     * @return the size, including null contents.
     */
    public int size() {
        return size;
    }

    /**
     * @return total contents
     */
    public Item[] contents() {
        return contents;
    }

    /**
     * @return container texture type
     */
    public String containerType() {
        return containerType;
    }

    /**
     * @return position including offsets.
     */
    public Vector2 position() {
        return position;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();

        writeString(containerType);
        writeVector2(position.x, position.y);

        buffer.writeInt(contents.length);
        for (int i = 0; i < contents.length; i++) {
            // write boolean if an item exists at the index.
            buffer.writeBoolean(contents[i] != null);
            if (contents[i] != null) {
                buffer.writeInt(i);
                writeItem(contents[i]);
            }
        }
    }

    @Override
    public void decode() {
        containerType = readString();
        final float x = buffer.readFloat();
        final float y = buffer.readFloat();
        this.position = new Vector2(x, y);

        size = buffer.readInt();
        contents = new Item[size];
        for (int i = 0; i < contents.length; i++) {
            final boolean has = buffer.readBoolean();
            if (has) {
                final int slot = buffer.readInt();
                contents[slot] = readItem();
            }
        }
    }
}
