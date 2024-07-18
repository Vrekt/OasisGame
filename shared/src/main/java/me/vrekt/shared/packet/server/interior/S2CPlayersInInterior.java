package me.vrekt.shared.packet.server.interior;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.codec.S2CPacketHandler;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.player.S2CNetworkPlayer;

/**
 * Send the client a list of players within an interior
 */
public final class S2CPlayersInInterior extends GamePacket {

    public static final int PACKET_ID = 1123;

    // list of all players within the server
    private S2CNetworkPlayer[] players;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPlayersInInterior(buffer));
    }

    public S2CPlayersInInterior(S2CNetworkPlayer... players) {
        this.players = players;
    }

    public S2CPlayersInInterior(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * Indicates there are no players within the server
     */
    public S2CPlayersInInterior() {
        this.players = null;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public S2CNetworkPlayer[] getPlayers() {
        return players;
    }

    /**
     * @return {@code true} if this packet contains a list of players to create
     */
    public boolean hasPlayers() {
        return players != null;
    }

    @Override
    public void encode() {
        writeId();
        if (players == null) {
            buffer.writeInt(0);
        } else {
            buffer.writeInt(players.length);
            // write all array players
            for (S2CNetworkPlayer player : players) {
                buffer.writeInt(player.entityId);
                writeString(player.username);
               // buffer.writeFloat(player.position.x);
             //   buffer.writeFloat(player.position.y);
            }
        }
    }

    @Override
    public void decode() {
        final int players = buffer.readInt();
        if (players > 0) {
            this.players = new S2CNetworkPlayer[players];
            for (int i = 0; i < players; i++) {
                final int entityId = buffer.readInt();
                final String name = readString();
                final float x = buffer.readFloat();
                final float y = buffer.readFloat();
                //this.players[i] = new S2CNetworkPlayer(entityId, name, new Vector2(x, y));
            }
        }
    }


}
