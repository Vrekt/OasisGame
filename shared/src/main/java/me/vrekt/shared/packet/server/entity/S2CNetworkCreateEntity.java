package me.vrekt.shared.packet.server.entity;

import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.network.server.entity.ServerEntity;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Tell the client to create an entity
 */
public final class S2CNetworkCreateEntity extends GamePacket {

    private NetworkEntityState entityState;

    public S2CNetworkCreateEntity(ByteBuf buffer) {
        super(buffer);
    }

    public S2CNetworkCreateEntity(ServerEntity entity) {
        this.entityState = new NetworkEntityState(entity);
    }

    /**
     * @return the state
     */
    public NetworkEntityState state() {
        return entityState;
    }

    @Override
    public int getId() {
        return Packets.S2C_CREATE_ENTITY;
    }

    @Override
    public void encode() {
        writeId();
        writeSingleEntityState(entityState);
    }

    @Override
    public void decode() {
        this.entityState = readSingleEntityState();
    }
}
