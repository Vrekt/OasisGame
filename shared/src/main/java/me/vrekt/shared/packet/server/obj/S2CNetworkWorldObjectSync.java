package me.vrekt.shared.packet.server.obj;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Contains all the objects the player receiving should create.
 */
public final class S2CNetworkWorldObjectSync extends GamePacket {

    private WorldNetworkObject[] objects;

    public S2CNetworkWorldObjectSync(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public int getId() {
        return Packets.S2C_OBJECT_SYNC;
    }

    @Override
    public void encode() {

    }
}
