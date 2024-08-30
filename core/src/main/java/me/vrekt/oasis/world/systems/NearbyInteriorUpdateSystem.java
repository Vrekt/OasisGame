package me.vrekt.oasis.world.systems;

import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;

/**
 * Will update nearby interiors.
 */
public final class NearbyInteriorUpdateSystem extends WorldSystem {

    public static final int SYSTEM_ID = 1;
    private GameWorld world;

    public NearbyInteriorUpdateSystem(GameWorld world) {
        super(SYSTEM_ID, 0.1f);
        this.world = world;
    }

    @Override
    protected void process(float delta, float tick) {
        for (GameWorldInterior interior : world.interiorWorlds().values()) {
            if (!interior.requiresNearUpdating()) continue;

            if (interior.isNear()) {
                interior.updateWhilePlayerIsNear(world);
            }

            final boolean within = interior.isWithinEnteringDistance(world.player().getPosition());

            if (within && !interior.isNear()) {
                interior.setNear(true);
            } else if (!within && interior.isNear()) {
                interior.invalidatePlayerNearbyState();
                interior.setNear(false);
            }
        }
    }

    @Override
    public void dispose() {
        world = null;
    }
}
