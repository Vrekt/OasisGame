package me.vrekt.oasis.ai.goals;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.ai.components.AiFollowPathComponent;
import me.vrekt.oasis.ai.utility.AiVectorUtility;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Follow a path and traverse it forever.
 */
public final class EntityFollowPathGoal extends AiFollowPathComponent {

    private boolean waitForNextTarget;

    private int lastPathRotationSegment;
    private boolean rotationLocked;

    private int reversingDirectionSegment;
    private boolean reversingDirection;

    public EntityFollowPathGoal(GameEntity entity, Array<Vector2> waypoints, boolean teleportToFirst) {
        super(entity, waypoints, teleportToFirst);
    }

    @Override
    protected void run(float delta) {
        if (lastPathRotationSegment != currentPathSegment) {
            rotationLocked = false;
            lastPathRotationSegment = currentPathSegment;
        }
    }

    @Override
    public void applyResult(Vector2 linear) {
        super.applyResult(linear);

        if (!rotationLocked) steering.setDirectionMoving(AiVectorUtility.velocityToDirection(linear));
    }

    /**
     * If the owner of this component does not stop at waypoints, this method can be ignored.
     *
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
