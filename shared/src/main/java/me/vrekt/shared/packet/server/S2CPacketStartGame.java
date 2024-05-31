package me.vrekt.shared.packet.server;

import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.S2CPacketHandler;

/**
 * Tells the client to start the game once their client has loaded
 */
public final class S2CPacketStartGame extends GamePacket {

    public static final int PACKET_ID = 1122;

    // current server time or server tick
    private long serverTime;
    // list of all players within the server
    private BasicServerPlayer[] players;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPacketStartGame(buffer));
    }

    public S2CPacketStartGame(long serverTime, BasicServerPlayer... players) {
        this.serverTime = serverTime;
        this.players = players;
    }

    public S2CPacketStartGame(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * Indicates there are no players within the server
     *
     * @param serverTime current time
     */
    public S2CPacketStartGame(long serverTime) {
        this.serverTime = serverTime;
        this.players = null;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public long getServerTime() {
        return serverTime;
    }

    public BasicServerPlayer[] getPlayers() {
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
        buffer.writeLong(serverTime);
        if (players == null) {
            buffer.writeInt(0);
        } else {
            buffer.writeInt(players.length);
            // write all array players
            for (BasicServerPlayer player : players) {
                buffer.writeInt(player.entityId);
                writeString(player.username);
                buffer.writeFloat(player.position.x);
                buffer.writeFloat(player.position.y);
            }
        }
    }

    @Override
    public void decode() {
        serverTime = buffer.readLong();
        final int players = buffer.readInt();
        if (players > 0) {
            this.players = new BasicServerPlayer[players];
            for (int i = 0; i < players; i++) {
                final int entityId = buffer.readInt();
                final String name = readString();
                final float x = buffer.readFloat();
                final float y = buffer.readFloat();
                this.players[i] = new BasicServerPlayer(entityId, name, new Vector2(x, y));
            }
        }
    }

    /**
     * Server player data object
     */
    public static final class BasicServerPlayer {
        public int entityId;
        public String username;
        public Vector2 position;

        public BasicServerPlayer(int entityId, String username, Vector2 position) {
            this.entityId = entityId;
            this.username = username;
            this.position = position;
        }
    }

}
