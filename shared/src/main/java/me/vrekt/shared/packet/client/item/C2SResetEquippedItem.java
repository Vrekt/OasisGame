package me.vrekt.shared.packet.client.item;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Player is not equipping an item anymore.
 */
public final class C2SResetEquippedItem extends GamePacket {

    public static final int ID = 2001_5;

    public C2SResetEquippedItem() {

    }

    public C2SResetEquippedItem(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void encode() {
        writeId();
    }
}
