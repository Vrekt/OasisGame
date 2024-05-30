package me.vrekt.oasis.world.systems;

import com.badlogic.ashley.utils.Bag;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.world.effects.AreaEffectCloud;

/**
 * Updates area effect clouds within a world.
 */
public final class AreaEffectUpdateSystem extends WorldSystem {

    public static final int SYSTEM_ID = 0;

    private final Bag<AreaEffectCloud> areaEffects = new Bag<>();

    public AreaEffectUpdateSystem() {
        super(SYSTEM_ID, 0.25f);
    }

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
     * TODO: Surely this can be improved
     * Iterating over every effect per entity... ouch.
     *
     * @param entity the entity
     */
    public void processEntity(Entity entity) {
        for (int i = 0; i < areaEffects.size(); i++) {
            final AreaEffectCloud effect = areaEffects.get(i);
            effect.process(entity);
        }
    }

    @Override
    public void process(float delta, float tick) {
        for (int i = 0; i < areaEffects.size(); i++) {
            final AreaEffectCloud effect = areaEffects.get(i);
            if (effect.update()) {
                areaEffects.remove(i).dispose();
            }
        }
    }
}
