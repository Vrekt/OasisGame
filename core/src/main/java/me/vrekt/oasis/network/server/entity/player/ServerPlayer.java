package me.vrekt.oasis.network.server.entity.player;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.network.IntegratedGameServer;
import me.vrekt.oasis.network.connection.server.PlayerServerConnection;
import me.vrekt.oasis.network.server.entity.AbstractServerEntity;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.shared.packet.server.player.S2CTeleport;
import me.vrekt.shared.packet.server.player.S2CTeleportPlayer;

/**
 * A player within the server.
 */
public final class ServerPlayer extends AbstractServerEntity {

    private final PlayerServerConnection connection;
    private boolean loading, loaded;

    // the interior allowed to enter
    private Interior allowedToEnter;
    private NetworkPlayer local;

    public ServerPlayer(PlayerServerConnection connection, IntegratedGameServer server) {
        super(server);
        this.connection = connection;
    }

    /**
     * @return the connection for this player
     */
    public PlayerServerConnection getConnection() {
        return connection;
    }

    /**
     * @return ift his player has successfully loaded
     */
    public boolean loaded() {
        return loaded;
    }

    /**
     * Set loaded
     *
     * @param loaded loaded
     */
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * Set the interior allowed to enter
     *
     * @param allowedToEnter interior type
     */
    public void setAllowedToEnter(Interior allowedToEnter) {
        this.allowedToEnter = allowedToEnter;
    }

    /**
     * @return the interior allowed to enter
     */
    public Interior allowedToEnter() {
        return allowedToEnter;
    }

    public void transfer(ServerWorld into) {
        world.removePlayerFromWorld(this);

        into.transferPlayerInto(this);
        setWorldIn(into);
    }

    /**
     * Set the local game network player
     *
     * @param local player
     */
    public void setLocal(NetworkPlayer local) {
        this.local = local;
    }

    /**
     * @return local network player
     */
    public NetworkPlayer local() {
        return local;
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
        if (inWorld) world.broadcastImmediatelyExcluded(entityId, new S2CTeleportPlayer(entityId, where.x, where.y));
        // teleport us
        getConnection().sendImmediately(new S2CTeleport(where.x, where.y));
    }

    /**
     * Teleport the player but do not tell them.
     * Usually for joining worlds.
     *
     * @param where where to
     */
    public void teleportSilent(Vector2 where) {
        setPosition(where);
        if (inWorld) {
            GameLogging.info(this, "Debug: player teleported actually in world.");
            world.broadcastImmediatelyExcluded(entityId, new S2CTeleportPlayer(entityId, where.x, where.y));
        }
    }

    /**
     * Update the server position of this player
     *
     * @param x        X
     * @param y        Y
     * @param rotation rotation
     */
    public void updatePosition(float x, float y, int rotation) {
        setPosition(x, y);
        setRotation(rotation);
    }

    /**
     * Update the server velocity of this player
     *
     * @param x        X velocity
     * @param y        Y velocity
     * @param rotation rotation
     */
    public void updateVelocity(float x, float y, int rotation) {
        setVelocity(x, y);
        setRotation(rotation);
    }
}
