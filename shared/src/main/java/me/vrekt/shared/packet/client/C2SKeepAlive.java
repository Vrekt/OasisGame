package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Basic packet indicating the client is still alive
 */
public final class C2SKeepAlive extends GamePacket {

    public static final int PACKET_ID = 2001_8;

    public C2SKeepAlive(ByteBuf buffer) {
        super(buffer);
    }

    public C2SKeepAlive() {
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
    }

}
