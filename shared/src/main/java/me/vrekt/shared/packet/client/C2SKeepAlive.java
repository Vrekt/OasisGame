package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Basic packet indicating the client is still alive
 */
public final class C2SKeepAlive extends GamePacket {

    public C2SKeepAlive(ByteBuf buffer) {
        super(buffer);
    }

    public C2SKeepAlive() {
    }

    @Override
    public int getId() {
        return Packets.C2S_KEEP_ALIVE;
    }

    @Override
    public void encode() {
        writeId();
    }

}
