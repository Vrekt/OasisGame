package me.vrekt.oasis.ai.components;

import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.ai.behaviour.ApplyBehavior;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Basic wandering component
 */
public final class AiWanderComponent extends AiComponent {

    private static final float ALIGN_TOLERANCE = 0.1f;
    private static final float DECELERATION_RADIUS = 1;
    private static final float TIME_TO_TARGET = 0.1f;
    private static final float WANDER_OFFSET = 0.0f;
    private static final float WANDER_RADIUS = 5f;
    private static final float WANDER_RATE = MathUtils.PI2 * 6f;

    private final Wander<Vector2> wander;
    private final float minY, maxY;

    public AiWanderComponent(GameEntity entity, float minY, float maxY) {
        super(entity, AiComponentType.WANDER, ApplyBehavior.VELOCITY_ONLY);

        this.minY = minY;
        this.maxY = maxY;

        wander = new Wander<>(steering)
                .setAlignTolerance(ALIGN_TOLERANCE)
                .setDecelerationRadius(DECELERATION_RADIUS)
                .setTimeToTarget(TIME_TO_TARGET)
                .setWanderOffset(WANDER_OFFSET)
                .setWanderRadius(WANDER_RADIUS)
                .setWanderRate(WANDER_RATE);
        steering.setBehavior(wander);
    }

    @Override
    public void update(float delta) {
        handleWanderOffset();
        super.update(delta);
    }

    /**
     * Handle wander offset is entity is getting out of bounds
     */
    private void handleWanderOffset() {
        if (entity.getPosition().y >= maxY) {
            wander.setWanderOffset(-9);
        } else if (entity.getPosition().y <= minY) {
            wander.setWanderOffset(1f);
        }
    }

}
