package me.vrekt.shared.network;

import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

public final class ClientPacketSwingItem extends GamePacket {

    public static final int ID = 2001_4;

    private int entityId;
    private int itemId;

    public ClientPacketSwingItem(int entityId, int itemId) {
        this.entityId = entityId;
        this.itemId = itemId;
    }

    public ClientPacketSwingItem(ByteBuf buffer) {
        super(buffer);
    }

    public int getItemId() {
        return itemId;
    }

    public int getEntityId() {
        return entityId;
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