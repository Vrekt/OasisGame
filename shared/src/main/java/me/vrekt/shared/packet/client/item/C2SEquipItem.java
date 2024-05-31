package me.vrekt.shared.packet.client.item;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Player has equipped an item, notify all other players
 */
public final class C2SEquipItem extends GamePacket {

    public static final int ID = 2001_2;

    private int entityId;
    private String itemKey;

    public C2SEquipItem(int entityId, String itemKey) {
        this.entityId = entityId;
        this.itemKey = itemKey;
    }

    public C2SEquipItem(ByteBuf buffer) {
        super(buffer);
    }

    public int getEntityId() {
        return entityId;
    }

    public String getItemKey() {
        return itemKey;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        writeString(itemKey);
    }

    @Override
    public void decode() {
        this.entityId = buffer.readInt();
        itemKey = readString();
    }

    @Override
    public int getId() {
        return ID;
    }
}
