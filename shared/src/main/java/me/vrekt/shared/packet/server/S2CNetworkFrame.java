package me.vrekt.shared.packet.server;

import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.shared.codec.S2CPacketHandler;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkPlayerState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.network.state.NetworkWorldState;
import me.vrekt.shared.packet.GamePacket;

/**
 * Single network frame
 * TODO: Validation
 */
public final class S2CNetworkFrame extends GamePacket {

    public static final int ID = 3000_7;

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
        return ID;
    }

    @Override
    public void encode() {
        writeId();

        // write world information first
        writeString(state.world().worldName());
        buffer.writeFloat(state.world().worldTick());
        // client will use this to predict/correct entity movement
        buffer.writeLong(state.timeSent());

        final boolean hasPlayers = state.players() != null && state.players().length > 0;
        buffer.writeBoolean(hasPlayers);

        if (hasPlayers) {
            // write player data
            buffer.writeInt(state.players().length);
            for (int i = 0; i < state.players().length; i++) {
                final NetworkPlayerState player = state.players()[i];
                buffer.writeInt(player.entityId());
                writeVector2(player.x(), player.y());
                writeVector2(player.vx(), player.vy());
                buffer.writeBoolean(player.isHost());
                writeString(player.name());
            }
        }

        // write entity data
        buffer.writeInt(state.entities().length);
        for (int i = 0; i < state.entities().length; i++) {
            final NetworkEntityState entity = state.entities()[i];
            buffer.writeInt(entity.entityId());
            writeVector2(entity.x(), entity.y());
            writeVector2(entity.vx(), entity.vy());
            writeString(entity.name());
            buffer.writeInt(entity.type().ordinal());
        }
    }

    @Override
    public void decode() {
        final String worldName = readString();
        final float worldTick = buffer.readFloat();
        final long timeSent = buffer.readLong();
        final NetworkWorldState world = new NetworkWorldState(worldName, worldTick);

        // read network player data
        final boolean hasPlayers = buffer.readBoolean();
        NetworkPlayerState[] players = null;

        if (hasPlayers) {
            final int pLength = buffer.readInt();
            players = new NetworkPlayerState[pLength];

            for (int i = 0; i < pLength; i++) {
                final int entityId = buffer.readInt();
                final float x = buffer.readFloat();
                final float y = buffer.readFloat();
                final float vx = buffer.readFloat();
                final float vy = buffer.readFloat();
                final boolean isHost = buffer.readBoolean();
                final String name = readString();
                players[i] = new NetworkPlayerState(entityId, name, x, y, vx, vy, isHost);
            }
        }

        // read entity data
        final int length = buffer.readInt();
        final NetworkEntityState[] entities = new NetworkEntityState[length];
        for (int i = 0; i < length; i++) {
            final int entityId = buffer.readInt();
            final float x = buffer.readFloat();
            final float y = buffer.readFloat();
            final float vx = buffer.readFloat();
            final float vy = buffer.readFloat();
            final String name = readString();
            final int ordinal = buffer.readInt();
            entities[i] = new NetworkEntityState(entityId, name, x, y, vx, vy, EntityType.values()[ordinal]);
        }

        this.state = new NetworkState(world, players, entities, timeSent);
    }
}
