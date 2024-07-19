package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Check client status
 */
public final class S2CKeepAlive extends GamePacket {

    public static final int PACKET_ID = 3000_5;

    public S2CKeepAlive(ByteBuf buffer) {
        super(buffer);
    }

    public S2CKeepAlive() {
    }

    @Override
    public void encode() {
        writeId();
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }
}
