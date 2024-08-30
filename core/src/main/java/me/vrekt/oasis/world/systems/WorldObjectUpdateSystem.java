package me.vrekt.oasis.world.systems;

import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Update all world objects
 */
public final class WorldObjectUpdateSystem extends WorldSystem {

    public static final int SYSTEM_ID = 2;
    private GameWorld world;

    public WorldObjectUpdateSystem(GameWorld world) {
        super(SYSTEM_ID, 0);

        this.world = world;
    }

    @Override
    protected void process(float delta, float tick) {
        // interactions
        for (AbstractInteractableWorldObject worldObject : world.interactableWorldObjects().values()) {
            if (worldObject.isEnabled()) {
                final float dist = worldObject.getPosition().dst2(world.player().getPosition());
                worldObject.setDistanceToPlayer(dist);
            }

            if (worldObject.isUpdatable() && worldObject.wasInteractedWith()) {
                worldObject.update();
            }
        }
    }

    @Override
    public void dispose() {
        world = null;
    }
}
