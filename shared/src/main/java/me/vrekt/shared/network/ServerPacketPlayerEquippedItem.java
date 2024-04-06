package me.vrekt.shared.network;

import gdx.lunar.v2.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Notify other players a player has equipped an item
 */
public final class ServerPacketPlayerEquippedItem extends GamePacket {

    public static final int ID = 2001_3;

    private int entityId;
    private int itemId;

    public ServerPacketPlayerEquippedItem(int entityId, int itemId) {
        this.entityId = entityId;
        this.itemId = itemId;
    }

    public ServerPacketPlayerEquippedItem(ByteBuf buffer) {
        super(buffer);
    }

    public int getEntityId() {
        return entityId;
    }

    public int getItemId() {
        return itemId;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeInt(itemId);
    }

    @Override
    public void decode() {
        this.entityId = buffer.readInt();
        this.itemId = buffer.readInt();
    }

    @Override
    public int getId() {
        return ID;
    }
}