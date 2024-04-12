package me.vrekt.shared.network;

import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Player has equipped an item, notify all other players
 */
public final class ClientPacketEquipItem extends GamePacket {

    public static final int ID = 2001_2;

    private int entityId;
    private String itemKey;

    public ClientPacketEquipItem(int entityId, String itemKey) {
        this.entityId = entityId;
        this.itemKey = itemKey;
    }

    public ClientPacketEquipItem(ByteBuf buffer) {
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
