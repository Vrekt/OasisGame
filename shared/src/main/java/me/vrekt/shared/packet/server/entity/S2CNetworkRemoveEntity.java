package me.vrekt.shared.packet.server.entity;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

public final class S2CNetworkRemoveEntity extends GamePacket {

    public S2CNetworkRemoveEntity(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public int getId() {
        return Packets.S2C_REMOVE_ENTITY;
    }

    @Override
    public void encode() {

    }
}
