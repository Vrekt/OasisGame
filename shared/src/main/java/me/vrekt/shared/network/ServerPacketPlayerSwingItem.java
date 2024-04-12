package me.vrekt.shared.network;

import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

public class ServerPacketPlayerSwingItem extends GamePacket {

    public static final int ID = 2001_5;

    private int entityId;
    private int itemId;

    public ServerPacketPlayerSwingItem(int entityId, int itemId) {
        this.entityId = entityId;
        this.itemId = itemId;
    }

    public ServerPacketPlayerSwingItem(ByteBuf buffer) {
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
