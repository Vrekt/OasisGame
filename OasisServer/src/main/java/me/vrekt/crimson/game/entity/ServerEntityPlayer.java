package me.vrekt.crimson.game.entity;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.crimson.Crimson;
import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.network.ServerPlayerConnection;
import me.vrekt.crimson.game.world.interior.InteriorWorld;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;
import me.vrekt.shared.packet.server.player.S2CTeleport;
import me.vrekt.shared.packet.server.player.S2CTeleportPlayer;

/**
 * Base implementation of a player entity within the server
 */
public final class ServerEntityPlayer extends AbstractServerEntity {

    private ServerPlayerConnection connection;
    private boolean isLoaded, isLoading;

    private InteriorWorld interiorEntering;

    public ServerEntityPlayer(CrimsonGameServer server, ServerPlayerConnection connection) {
        super(server);
        this.connection = connection;
    }

    public ServerEntityPlayer(CrimsonGameServer server) {
        super(server);
    }

    public void prepareTransfer(InteriorWorld to) {
        world.removePlayerTemporarily(this);
        world.broadcastNowWithExclusion(entityId, new S2CPlayerEnteredInterior(to.type(), entityId));

        this.interiorEntering = to;
    }

    public void finalizeTransfer() {
        if (interiorEntering == null) {
            Crimson.log("Interior entering was null! I am %s", name);
        }
        interiorEntering.spawnPlayerInWorld(this);
    }

    /**
     * @return {@code true} if this player is loaded
     */
    public boolean loaded() {
        return isLoaded;
    }

    /**
     * @return {@code true} if this player is loading a world.
     */
    public boolean loading() {
        return isLoading;
    }

    /**
     * Set this player has loaded the world
     *
     * @param loaded loaded
     */
    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    /**
     * Set this player is loading a world
     *
     * @param loading loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
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
        server.disconnectPlayer(this, reason);
    }

    /**
     * Teleport this player
     *
     * @param where where to
     */
    public void teleport(Vector2 where) {
        setPosition(where);

        // notify other players we teleported
        if (inWorld) world.broadcastNowWithExclusion(entityId, new S2CTeleportPlayer(entityId, where.x, where.y));
        // teleport us
        getConnection().sendImmediately(new S2CTeleport(where.x, where.y));
    }

    @Override
    public void dispose() {
        super.dispose();
        connection = null;
        isLoaded = false;
    }
}
