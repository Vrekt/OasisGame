package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Send all players in the world or interior to the player
 */
public final class S2CNetworkPlayerSync extends GamePacket {

    public S2CNetworkPlayerSync(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public int getId() {
        return Packets.S2C_PLAYER_SYNC;
    }

    @Override
    public void encode() {

    }
}
