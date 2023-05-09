package me.vrekt.shared.network;

import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Notify other players a player has equipped an item
 */
public final class ServerPlayerEquippedItem extends Packet {

    public static final int ID = 2001_3;

    private int entityId;
    private int itemId;

    public ServerPlayerEquippedItem(int entityId, int itemId) {
        this.entityId = entityId;
        this.itemId = itemId;
    }

    public ServerPlayerEquippedItem(ByteBuf buffer) {
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