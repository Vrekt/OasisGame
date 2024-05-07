package me.vrekt.oasis.ai.components;

import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.utility.logging.GameLogging;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles arriving AI
 */
public final class AiArrivalComponent extends AiComponent {

    private static final float TIME_TO_TARGET = 0.1f;
    private static final float ARRIVE_TOLERANCE = 0.01f;
    private static final float DECELERATION_RADIUS = 2f;

    // points where the entity can walk to
    private final Array<Vector2> points = new Array<>();

    private final Arrive<Vector2> arrive;
    private boolean isWalkingToPath;
    private float lastPointTick, pathingInterval, arrivalTolerance;

    public AiArrivalComponent(EntityInteractable entity) {
        super(entity);

        arrive = new Arrive<>(steering, location);
        arrive.setTimeToTarget(TIME_TO_TARGET);
        arrive.setArrivalTolerance(ARRIVE_TOLERANCE);
        arrive.setDecelerationRadius(DECELERATION_RADIUS);
        steering.setBehavior(arrive);
    }

    /**
     * Add an arrival point
     *
     * @param point the point
     */
    public void addArrivalPoint(Vector2 point) {
        points.add(point);
    }

    /**
     * Set how often this component will choose a new path and walk to it.
     *
     * @param interval interval in world ticks
     */
    public void setPathingInterval(float interval) {
        this.pathingInterval = interval;
    }

    /**
     * Set how close we need to be to the target for it to count
     *
     * @param tolerance the tolerance
     */
    public void setTargetArrivalTolerance(float tolerance) {
        this.arrivalTolerance = tolerance;
    }

    /**
     * Check if the entity is within the arrival target
     *
     * @return {@code true} if so
     */
    public boolean isWithinArrivalTarget() {
        return entity.getPosition().dst2(location.getPosition()) <= arrivalTolerance;
    }

    @Override
    public void update(float delta) {
        if (!isWalkingToPath && (lastPointTick == 0
                || (entity.getWorldIn().getCurrentWorldTick() - lastPointTick) >= pathingInterval)) {
            isWalkingToPath = true;
            assignRandomPoint();
        } else if (isWalkingToPath) {
            if (isWithinArrivalTarget()) {
                isWalkingToPath = false;
                // only update this point once we arrive to ignore the time it took to get there.
                lastPointTick = entity.getWorldIn().getCurrentWorldTick();
            }
        }

        if (isWalkingToPath) super.update(delta);
    }

    /**
     * Assign a random point to arrive to.
     */
    private void assignRandomPoint() {
        location.getPosition().set(points.get(ThreadLocalRandom.current().nextInt(points.size)));
        GameLogging.info(this, "%s is walking to a new point: %s", entity.getType(), location.getPosition());
    }

}
