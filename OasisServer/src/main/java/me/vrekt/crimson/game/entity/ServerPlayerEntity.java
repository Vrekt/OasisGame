package me.vrekt.crimson.game.entity;

import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.network.ServerPlayerConnection;
import me.vrekt.crimson.game.world.interior.InteriorWorld;
import me.vrekt.shared.packet.server.S2CPacketDisconnected;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;

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

    public void transfer(InteriorWorld to) {
        world.removePlayerTemporarily(this);
        world.broadcastNowWithExclusion(entityId, new S2CPlayerEnteredInterior(to.type(), entityId));

        to.spawnPlayerInWorld(this);
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
