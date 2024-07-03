package me.vrekt.oasis.ai.goals;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * An entity goal from the Tiled map.
 */
public class EntityMapGoal {

    private final EntityGoal goal;
    private final Vector2 position;
    private final EntityRotation goalRotation;

    public EntityMapGoal(EntityGoal goal, Vector2 position, EntityRotation rotation) {
        this.goal = goal;
        this.position = position;
        this.goalRotation = rotation;
    }

    public EntityGoal goal() {
        return goal;
    }

    public Vector2 position() {
        return position;
    }

    public EntityRotation goalRotation() {
        return goalRotation;
    }
}
