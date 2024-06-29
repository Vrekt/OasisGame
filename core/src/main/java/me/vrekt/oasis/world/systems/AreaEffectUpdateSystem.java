package me.vrekt.oasis.world.systems;

import me.vrekt.oasis.world.effects.AreaEffectCloud;

/**
 * Updates area effect clouds within a world.
 */
public final class AreaEffectUpdateSystem extends WorldSystem {

    public static final int SYSTEM_ID = 0;
    private final AreaEffectCloudManager manager;

    public AreaEffectUpdateSystem(AreaEffectCloudManager manager) {
        super(SYSTEM_ID, 0.25f);
        this.manager = manager;
    }

    @Override
    public void process(float delta, float tick) {
        for (int i = 0; i < manager.areaEffects.size(); i++) {
            final AreaEffectCloud effect = manager.areaEffects.get(i);
            if (effect.update()) {
                manager.areaEffects.remove(i).dispose();
            }
        }
    }
}
