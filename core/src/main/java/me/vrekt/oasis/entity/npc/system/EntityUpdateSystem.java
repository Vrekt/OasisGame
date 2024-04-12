package me.vrekt.oasis.entity.npc.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Handles updating entities
 */
public final class EntityUpdateSystem extends EntitySystem {

    private final OasisGame game;
    private final OasisWorld world;
    private final OrthographicCamera gameCamera;

    public EntityUpdateSystem(OasisGame game, OasisWorld world) {
        super();
        this.game = game;
        this.world = world;
        this.gameCamera = game.getRenderer().getCamera();
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : world.getEntities().values()) {
            // update entities we can see, or are within update distance
            final float distance = entity.getPosition().dst2(game.getPlayer().getPosition());
            entity.setDistanceToPlayer(distance);

            if (distance <= OasisGameSettings.ENTITY_UPDATE_DISTANCE
                    || entity.isInView(gameCamera)) {
                entity.update(deltaTime);

                if (entity.isInteractable() && !entity.isNearby()) {
                    entity.setNearby(true);
                    world.getNearbyEntities().put(entity.asInteractable(), distance);
                }
            } else {
                if (entity.isInteractable() && entity.isNearby()) {
                    entity.setNearby(false);
                    world.getNearbyEntities().remove(entity.asInteractable());
                }
            }
        }
    }
}
