package me.vrekt.shared.network.state;

import me.vrekt.oasis.world.GameWorld;

/**
 * Network world state.
 * TODO: update world tick time
 */
public class NetworkWorldState {

    private final String worldName;
    private final float worldTick;

    public NetworkWorldState(GameWorld world) {
        this.worldName = world.getWorldName();
        this.worldTick = 0.0f; // TODO
    }

    public NetworkWorldState(String worldName, float worldTick) {
        this.worldName = worldName;
        this.worldTick = worldTick;
    }

    public String worldName() {
        return worldName;
    }

    public float worldTick() {
        return worldTick;
    }
}
