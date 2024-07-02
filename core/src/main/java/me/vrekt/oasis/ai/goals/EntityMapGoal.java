package me.vrekt.oasis.ai.goals;

import com.badlogic.gdx.math.Vector2;

/**
 * An entity goal from the Tiled map.
 */
public class EntityMapGoal {

    private final EntityGoal goal;
    private final Vector2 position;

    public EntityMapGoal(EntityGoal goal, Vector2 position) {
        this.goal = goal;
        this.position = position;
    }

    public EntityGoal goal() {
        return goal;
    }

    public Vector2 position() {
        return position;
    }
}
