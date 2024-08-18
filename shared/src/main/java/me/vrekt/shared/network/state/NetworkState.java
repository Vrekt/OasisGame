package me.vrekt.shared.network.state;

import com.badlogic.gdx.utils.TimeUtils;

/**
 * Represents a network/world state. All active objects and entities should be interpolated from this state.
 */
public final class NetworkState {

    private final NetworkWorldState world;
    private final NetworkEntityState[] entities;

    private final long timeSent;

    private boolean wasSent;

    public NetworkState(NetworkWorldState world,
                        NetworkEntityState[] entities,
                        long now) {
        this.world = world;
        this.entities = entities;
        this.timeSent = now;
    }

    /**
     * @return world related information
     */
    public NetworkWorldState world() {
        return world;
    }

    /**
     * @return all loaded entities
     */
    public NetworkEntityState[] entities() {
        return entities;
    }

    /**
     * @return when this packet was built
     */
    public long timeSent() {
        return timeSent;
    }

    /**
     * @return calculates the time it took to arrive
     */
    public long timeToArrive() {
        return TimeUtils.nanoTime() - timeSent;
    }

    /**
     * @return {@code true} if this state was already sent.
     */
    public boolean wasSent() {
        return wasSent;
    }

    /**
     * Set
     *
     * @param wasSent state
     */
    public void setWasSent(boolean wasSent) {
        this.wasSent = wasSent;
    }
}
