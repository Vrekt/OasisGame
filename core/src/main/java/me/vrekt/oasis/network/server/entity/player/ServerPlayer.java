package me.vrekt.oasis.network.server.entity.player;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.network.IntegratedGameServer;
import me.vrekt.oasis.network.connection.server.PlayerServerConnection;
import me.vrekt.oasis.network.server.entity.AbstractServerEntity;
import me.vrekt.shared.packet.server.player.S2CTeleport;
import me.vrekt.shared.packet.server.player.S2CTeleportPlayer;

/**
 * A player within the server.
 */
public final class ServerPlayer extends AbstractServerEntity {

    private final PlayerServerConnection connection;
    private boolean loading, loaded;

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
