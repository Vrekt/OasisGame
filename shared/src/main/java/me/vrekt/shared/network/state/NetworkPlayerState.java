package me.vrekt.shared.network.state;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;

/**
 * Represents the state of a single player.
 */
public final class NetworkPlayerState extends AbstractNetworkEntityState {

    // if this player is host
    private boolean isHost;

    public NetworkPlayerState(int entityId,
                              String name,
                              float x,
                              float y,
                              float vx,
                              float vy,
                              boolean isHost) {
        super(entityId, name, x, y, vx, vy);
        this.isHost = isHost;
    }

    public NetworkPlayerState(int entityId, String name, Vector2 position, Vector2 velocity, boolean isHost) {
        super(entityId, name, position, velocity);
        this.isHost = isHost;
    }

    public NetworkPlayerState(NetworkPlayer entity) {
        super(entity);

        this.isHost = false;
    }

    /**
     * @return {@code true} if this player is host
     */
    public boolean isHost() {
        return isHost;
    }
}
