package me.vrekt.shared.packet.server;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.codec.S2CPacketHandler;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.network.state.NetworkWorldState;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Single network frame
 */
public final class S2CNetworkFrame extends GamePacket {

    private NetworkState state;

    public static void handle(S2CPacketHandler handler, ByteBuf in) {
        handler.handle(new S2CNetworkFrame(in));
    }

    public S2CNetworkFrame(NetworkState state) {
        this.state = state;
    }

    public S2CNetworkFrame(ByteBuf buffer) {
        super(buffer);
    }

    public NetworkState state() {
        return state;
    }

    @Override
    public int getId() {
        return Packets.S2C_NETWORK_FRAME;
    }

    @Override
    public void encode() {
        writeId();

        // write world information first
        writeString(state.world().worldName());
        buffer.writeFloat(state.world().worldTick());
        // client will use this to predict/correct entity movement
        buffer.writeLong(state.timeSent());

        // write entity data
        buffer.writeInt(state.entities().length);
        for (int i = 0; i < state.entities().length; i++) {
            writeSingleEntityState(state.entities()[i]);
        }
    }

    @Override
    public void decode() {
        final String worldName = readString();
        final float worldTick = buffer.readFloat();
        final long timeSent = buffer.readLong();
        final NetworkWorldState world = new NetworkWorldState(worldName, worldTick);

        // read entity data
        final int length = buffer.readInt();
        final NetworkEntityState[] entities = new NetworkEntityState[length];
        for (int i = 0; i < length; i++) {
            entities[i] = readSingleEntityState();
        }

        this.state = new NetworkState(world, entities, timeSent);
    }
}
