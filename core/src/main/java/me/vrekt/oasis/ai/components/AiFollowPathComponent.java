package me.vrekt.oasis.ai.components;

import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.ai.behaviour.ApplyBehavior;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.utility.logging.GameLogging;

/**
 * Follows a predefined path within the Tiled map.
 */
public abstract class AiFollowPathComponent extends AiComponent {

    private static final float ZERO_MARGIN = 0.1f;

    protected final Array<Vector2> waypoints;
    protected final FollowPath<Vector2, LinePath.LinePathParam> followPath;
    protected final LinePath<Vector2> linePath;

    protected final float startTime;
    protected boolean isInitialized;
    protected int currentPathSegment;
    protected int lastPathSegment = -1;

    public AiFollowPathComponent(GameEntity entity, Array<Vector2> waypoints, boolean teleportToFirst) {
        super(entity, AiComponentType.FOLLOW_PATH, ApplyBehavior.VELOCITY_ONLY);

        this.waypoints = waypoints;
        linePath = new LinePath<>(waypoints);
        followPath = new FollowPath<>(steering, linePath, 1);
        GameLogging.info(this, "Loaded %d waypoints for %s", waypoints.size, entity.name());

        if (teleportToFirst) entity.setPosition(waypoints.first().x, waypoints.first().y);
        steering.setBehavior(followPath);
        this.applySelf = true;

        // offset the position so the character more closely is in the middle of the line segment (in theory)
        steering.setOffsetPosition(true);
        startTime = GameManager.tick();
    }

    /**
     * Run extending class updates
     *
     * @param delta delta
     */
    protected void run(float delta) {

    }

    @Override
    public void update(float delta) {
        currentPathSegment = followPath.getPathParam().getSegmentIndex();
        run(delta);

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

        entity.setVelocity(linear);
    }

    /**
     * @return if we have a new target after stopping.
     */
    public boolean hasNewTarget() {
        return lastPathSegment != currentPathSegment;
    }
}
