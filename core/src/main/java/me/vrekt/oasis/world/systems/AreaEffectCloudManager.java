package me.vrekt.oasis.world.systems;

import com.badlogic.ashley.utils.Bag;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.world.effects.AreaEffectCloud;

/**
 * Manages area effect clouds
 * Clouds of different effects like poison
 */
public final class AreaEffectCloudManager implements Disposable {

    final Bag<AreaEffectCloud> areaEffects = new Bag<>();

    /**
     * Spawn the cloud.
     *
     * @param cloud cloud
     */
    public void create(AreaEffectCloud cloud) {
        areaEffects.add(cloud);
    }

    /**
     * Process an entity
     * Maybe in the future only check per X.0 seconds
     *
     * @param entity the entity
     */
    public void processEntity(GameEntity entity) {
        if (entity.cloudApartOf() != null) {
            final boolean valid = entity.cloudApartOf().isEntityInside(entity);
            if (!valid) {
                entity.cloudApartOf().removeEntityInside(entity);
                entity.setCloudApartOf(null);
            }

            // TODO: May cause issues with multiple cloud effects
            // TODO: But that's not really intended mechanics
            return;
        }

        for (int i = 0; i < areaEffects.size(); i++) {
            final AreaEffectCloud effect = areaEffects.get(i);
            effect.process(entity);
        }
    }

    @Override
    public void dispose() {
        areaEffects.clear();
    }
}
