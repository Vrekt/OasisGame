package me.vrekt.shared.packet.server.player;

import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.S2CPacketHandler;

/**
 * A packet of all players within a certain world or interior
 */
public final class S2CPacketPlayers extends GamePacket {

    public static final int PACKET_ID = 1122;

    private String worldKey;
    private InteriorWorldType type;
    private boolean interior;
    // list of all players within the server
    private S2CNetworkPlayer[] players;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPacketPlayers(buffer));
    }

    public S2CPacketPlayers(String worldKey, boolean interior, S2CNetworkPlayer... players) {
        this.worldKey = worldKey;
        this.interior = interior;
        this.players = players;
    }

    public S2CPacketPlayers(String worldKey, S2CNetworkPlayer... players) {
        this.worldKey = worldKey;
        this.players = players;
    }

    public S2CPacketPlayers(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * Indicates there are no players within the server
     */
    public S2CPacketPlayers(String worldKey) {
        this.players = null;
        this.worldKey = worldKey;
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

    /**
     * @return world or interior key
     */
    public String worldKey() {
        return worldKey;
    }

    /**
     * @return if the packet contains players in an interior
     */
    public boolean interior() {
        return interior;
    }

    @Override
    public void encode() {
        writeId();
        writeString(worldKey);
        buffer.writeBoolean(interior);

        if (players == null) {
            buffer.writeInt(0);
        } else {
            buffer.writeInt(players.length);
            // write all array players
            for (S2CNetworkPlayer player : players) {
                buffer.writeInt(player.entityId);
                writeString(player.username);
                buffer.writeFloat(player.position.x);
                buffer.writeFloat(player.position.y);
            }
        }
    }

    @Override
    public void decode() {
        this.worldKey = readString();
        this.interior = buffer.readBoolean();
        final int players = buffer.readInt();
        if (players > 0) {
            this.players = new S2CNetworkPlayer[players];
            for (int i = 0; i < players; i++) {
                final int entityId = buffer.readInt();
                final String name = readString();
                final float x = buffer.readFloat();
                final float y = buffer.readFloat();
                this.players[i] = new S2CNetworkPlayer(entityId, name, new Vector2(x, y));
            }
        }
    }

}
