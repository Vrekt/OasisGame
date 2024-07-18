package me.vrekt.shared.packet.server;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.codec.S2CPacketHandler;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.player.S2CNetworkPlayer;

/**
 * Send to client to start the game
 */
public final class S2CStartGame extends GamePacket {

    public static final int PACKET_ID = 1123;

    private int worldId;
    // if the server has active players
    private boolean hasPlayers;
    private S2CNetworkPlayer[] players;

    public static void handle(S2CPacketHandler handler, ByteBuf in) {
        handler.handle(new S2CStartGame(in));
    }

    public S2CStartGame(int worldId, S2CNetworkPlayer... players) {
        this.worldId = worldId;
        this.hasPlayers = players != null && players.length > 0;
        this.players = players;
    }

    public S2CStartGame(int worldId) {
        this.worldId = worldId;
        this.hasPlayers = false;
        this.players = null;
    }

    public S2CStartGame(ByteBuf in) {
        super(in);
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public int worldId() {
        return worldId;
    }

    public boolean hasPlayers() {
        return hasPlayers;
    }

    public S2CNetworkPlayer[] players() {
        return players;
    }

    @Override
    public void encode() {
        writeId();

        buffer.writeInt(worldId);
        buffer.writeBoolean(hasPlayers);
        if (hasPlayers) {
            buffer.writeInt(players.length);
            // write all array players
            for (S2CNetworkPlayer player : players) {
                buffer.writeInt(player.entityId);
                writeString(player.username);
                buffer.writeFloat(player.x);
                buffer.writeFloat(player.y);
            }
        }
    }

    @Override
    public void decode() {
        this.worldId = buffer.readInt();
        this.hasPlayers = buffer.readBoolean();

        if (hasPlayers) {
            final int players = buffer.readInt();
            this.players = new S2CNetworkPlayer[players];
            for (int i = 0; i < players; i++) {
                final int entityId = buffer.readInt();
                final String name = readString();
                final float x = buffer.readFloat();
                final float y = buffer.readFloat();
                this.players[i] = new S2CNetworkPlayer(entityId, name, x, y);
            }
        }
    }

}
