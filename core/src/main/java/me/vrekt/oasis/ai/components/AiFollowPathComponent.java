package me.vrekt.oasis.ai.components;

import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.ai.behaviour.ApplyBehavior;
import me.vrekt.oasis.ai.utility.AiVectorUtility;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Follows a predefined path within the Tiled map.
 */
public final class AiFollowPathComponent extends AiComponent {

    private static final float TARGET_ARRIVAL_TOLERANCE = 0.2f;
    private static final float ZERO_MARGIN = 0.1f;

    private final Array<Vector2> waypoints;
    private final FollowPath<Vector2, LinePath.LinePathParam> followPath;
    private final LinePath<Vector2> linePath;

    private final float startTime;

    private int currentPathSegment;
    private int lastPathSegment = -1;

    private boolean waitForNextTarget;
    private boolean isInitialized;

    private int lastPathRotationSegment;
    private boolean rotationLockOverride, rotationLocked;

    private int reversingDirectionSegment;
    private boolean reversingDirection;

    public AiFollowPathComponent(GameEntity entity, Array<Vector2> waypoints) {
        super(entity, AiComponentType.FOLLOW_PATH, ApplyBehavior.VELOCITY_ONLY);

        this.waypoints = waypoints;
        linePath = new LinePath<>(waypoints);
        followPath = new FollowPath<>(steering, linePath, 1);

        entity.setPosition(waypoints.first().x, waypoints.first().y, true);
        steering.setBehavior(followPath);
        this.applySelf = true;

        // offset the position so the character more closely is in the middle of the line segment (in theory)
        steering.setOffsetPosition(true);
        startTime = GameManager.getTick();
    }

    public void setRotationLocked(boolean rotationLocked) {
        this.rotationLockOverride = rotationLocked;
    }

    @Override
    public void update(float delta) {
        currentPathSegment = followPath.getPathParam().getSegmentIndex();

        if (lastPathRotationSegment != currentPathSegment) {
            rotationLocked = false;
            lastPathRotationSegment = currentPathSegment;
        } else {
            if (rotationLockOverride) rotationLocked = true;
        }

        // let's start moving this component now
        // player will enter the area and see the entity already moving
        // this too prevents getting stuck on the first path point
        // we wait for 1 second for gdx-ai to pick a new segment.
        if (!isInitialized) isInitialized = GameManager.hasTimeElapsed(startTime, 1.0f);

        super.update(delta);
    }

    @Override
    public void applyResult(Vector2 linear) {

        final float leny = linear.y * linear.y;
        final float lenx = linear.x * linear.x;

        final boolean isZeroY = leny < ZERO_MARGIN;
        final boolean isZeroX = lenx < ZERO_MARGIN;

        // prevent small corrections
        // stops weird bobbing movement
        if (isZeroY) linear.y = 0.0f;
        if (isZeroX) linear.x = 0.0f;

        entity.setVelocity(linear, true);

        // only update rotation if we acquired a new target
        if (!rotationLocked) steering.setDirectionMoving(AiVectorUtility.velocityToDirection(linear));
    }

    public void pickRandomPoint() {

    }

    /**
     * @return if we have a new target after stopping.
     */
    public boolean hasNewTarget() {
        return lastPathSegment != currentPathSegment;
    }

    /**
     * @param tolerance how close to the target position to get before stopping/waiting
     * @return if we are within target.
     */
    public boolean isWithinTarget(float tolerance) {
        // prevent infinite loop because we stopped at the target and a new one
        // hasn't been decided yet by the gdx-ai
        if (waitForNextTarget) {
            // we can unlock since gdx-ai picked a new segment
            if (hasNewTarget()) {
                waitForNextTarget = false;
            } else {
                return false;
            }
        }

        // only calculate if we are within if the current path segment is valid
        // sometimes the path segment is greater than the list for whatever reason.
        boolean result = false;
        if (currentPathSegment + 1 < waypoints.size) {
            result = entity.getBody().getPosition().dst2(waypoints.get(currentPathSegment + 1)) <= tolerance && isInitialized;
        }

        // TODO: A little quirky, but works better.
        if (currentPathSegment + 1 >= waypoints.size
                && isInitialized
                && !reversingDirection
                && MathUtils.randomBoolean()) {
            // a small chance go back to where we came from
            followPath.setPathOffset(-1);
            reversingDirectionSegment = currentPathSegment - 1;
            reversingDirection = true;
        } else if (reversingDirection
                && currentPathSegment != reversingDirectionSegment
                && MathUtils.randomBoolean(0.25f)) {
            // random chance to now turn back
            followPath.setPathOffset(1);
            reversingDirection = false;
        }

        // ensure we tell the next time we check to wait.
        if (result) {
            lastPathSegment = currentPathSegment;
            waitForNextTarget = true;
        }

        return result;
    }
}
