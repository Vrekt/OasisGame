package me.vrekt.oasis.ai.goals;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.ai.components.AiFollowPathComponent;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * Walk a path and stop at the end.
 */
public final class EntityWalkPathGoal extends AiFollowPathComponent {

    private boolean isFinished;
    private EntityRotation finalRotation;

    public EntityWalkPathGoal(GameEntity entity, Array<Vector2> waypoints, boolean teleportToFirst) {
        super(entity, waypoints, teleportToFirst);
    }

    public void setFinalRotation(EntityRotation finalRotation) {
        this.finalRotation = finalRotation;
    }

    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void applyResult(Vector2 linear) {
        super.applyResult(linear);
    }

    @Override
    public void update(float delta) {
        // stop at last waypoint
        if (currentPathSegment + 1 >= waypoints.size && !isFinished) {
            entity.setVelocity(0, 0);
            entity.setRotation(finalRotation);
            isFinished = true;
        } else if (!isFinished) {
            super.update(delta);
        }
    }
}
