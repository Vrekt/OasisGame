package me.vrekt.oasis.entity.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.world.GameWorld;

import java.util.Iterator;

/**
 * Handles updating entities
 */
public final class EntityUpdateSystem extends EntitySystem {

    private final OasisGame game;
    private final GameWorld world;
    private final OrthographicCamera gameCamera;

    public EntityUpdateSystem(OasisGame game, GameWorld world) {
        super(0);
        this.game = game;
        this.world = world;
        this.gameCamera = game.getRenderer().getCamera();
    }

    @Override
    public void update(float deltaTime) {
        for (Iterator<GameEntity> it = world.entities().values().iterator(); it.hasNext(); ) {
            final GameEntity entity = it.next();
            if (entity.queuedForRemoval()) {
                world.removeDeadEntityNow(entity);
                it.remove();
                continue;
            }

            // update entities we can see, or are within update distance
            final float distance = entity.getPosition().dst2(game.getPlayer().getPosition());
            entity.setDistanceToPlayer(distance);

            // TODO: Will cause issues when a entity is out of view, if dying
            // TODO: Very low priority
            if (distance <= OasisGameSettings.ENTITY_UPDATE_DISTANCE
                    || entity.isInView(gameCamera)) {

                entity.update(deltaTime);
                if (!entity.isDead()) entity.checkAreaEffects();

                if (entity.isInteractable() && !entity.nearby()) {
                    entity.setNearby(true);
                    world.addNearbyEntity(entity.asInteractable());
                }
            } else {
                if (entity.isInteractable() && entity.nearby()) {
                    entity.setNearby(false);
                    world.removeNearbyEntity(entity.asInteractable());
                }
            }
        }
    }
}
