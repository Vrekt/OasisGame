package me.vrekt.crimson.game.entity;

import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.network.ServerPlayerConnection;
import me.vrekt.shared.packet.server.S2CPacketDisconnected;

/**
 * Base implementation of a player entity within the server
 */
public abstract class ServerPlayerEntity extends ServerEntity {

    protected ServerPlayerConnection connection;
    protected boolean isLoaded;

    public ServerPlayerEntity(CrimsonGameServer server, ServerPlayerConnection connection) {
        super(server);
        this.connection = connection;
    }

    public boolean loaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    /**
     * @return this players connection
     */
    public ServerPlayerConnection getConnection() {
        return connection;
    }

    /**
     * Kick this player
     *
     * @param reason the reason
     */
    public void kick(String reason) {
        connection.sendImmediately(new S2CPacketDisconnected(reason));
        connection.disconnect();
    }

    @Override
    public void dispose() {
        super.dispose();
        connection = null;
        isLoaded = false;
    }
}
