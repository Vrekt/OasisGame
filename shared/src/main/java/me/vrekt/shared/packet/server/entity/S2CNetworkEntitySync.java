package me.vrekt.shared.packet.server.entity;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Contains all the entities the player receiving should create.
 */
public final class S2CNetworkEntitySync extends GamePacket {

    //private NetworkEntityState[] entities;


    public S2CNetworkEntitySync(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public int getId() {
        return Packets.S2C_ENTITY_SYNC;
    }

    @Override
    public void encode() {

    }
}
