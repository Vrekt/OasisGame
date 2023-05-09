package me.vrekt.shared.network;

import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

public final class ClientSwingItem extends Packet {

    public static final int ID = 2001_4;

    private int entityId;
    private int itemId;

    public ClientSwingItem(int entityId, int itemId) {
        this.entityId = entityId;
        this.itemId = itemId;
    }

    public ClientSwingItem(ByteBuf buffer) {
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