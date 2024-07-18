package me.vrekt.oasis.ai.components;

import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.ai.behaviour.ApplyBehavior;
import me.vrekt.oasis.ai.goals.EntityMapGoal;
import me.vrekt.oasis.entity.GameEntity;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Walk to a random goal.
 */
public final class AiWalkToGoalsComponent extends AiComponent {

    private static final float TIME_TO_TARGET = 0.1f;
    private static final float ARRIVE_TOLERANCE = 0.01f;
    private static final float DECELERATION_RADIUS = 0.1f;

    // points where the entity can walk to
    private final Array<MappedGoal> goals = new Array<>();
    private final Arrive<Vector2> arrive;

    private MappedGoal activeGoal;
    private boolean goalExecuted;
    private boolean isAnyGoalActive;

    private float lastPathMet;
    private float pathingInterval;

    public AiWalkToGoalsComponent(GameEntity entity) {
        super(entity, AiComponentType.WALK_TO_GOALS, ApplyBehavior.DEFAULT);

        arrive = new Arrive<>(steering, location);
        arrive.setTimeToTarget(TIME_TO_TARGET);
        arrive.setArrivalTolerance(ARRIVE_TOLERANCE);
        arrive.setDecelerationRadius(DECELERATION_RADIUS);
        steering.setBehavior(arrive);
    }

    /**
     * How long to wait before choosing another point to walk to
     *
     * @param pathingInterval interval
     */
    public void setPathingInterval(float pathingInterval) {
        this.pathingInterval = pathingInterval;
    }

    /**
     * Add a goal point
     *
     * @param goal     the goal
     * @param executor the consumer when the goal is met.
     */
    public void addGoalPoint(EntityMapGoal goal, Consumer<EntityMapGoal> executor) {
        this.goals.add(new MappedGoal(goal, executor));
    }

    /**
     * @return {@code true} if this entity is within its goal
     */
    public boolean isAtGoal() {
        return activeGoal != null && activeGoal.goal.position().dst2(entity.getPosition()) <= arrive.getArrivalTolerance();
    }

    public void resume() {
        isAnyGoalActive = true;
        goalExecuted = false;
        lastPathMet = 0.0f;
        assignRandomGoal();
    }

    @Override
    public void update(float delta) {
        if (!isAnyGoalActive && (lastPathMet == 0 || GameManager.getTick() - lastPathMet >= pathingInterval)) {
            isAnyGoalActive = true;
            goalExecuted = false;
            assignRandomGoal();
        } else if (isAnyGoalActive) {
            if (isAtGoal()) {
                if (!goalExecuted) {
                    goalExecuted = true;
                    activeGoal.executor.accept(activeGoal.goal);
                }

                isAnyGoalActive = false;
                lastPathMet = GameManager.getTick();
            }
        }

        if (isAnyGoalActive) super.update(delta);
    }

    /**
     * Assign a random point to arrive to.
     */
    private void assignRandomGoal() {
        int r = ThreadLocalRandom.current().nextInt(goals.size);
        while (goals.get(r) == activeGoal) {
            r = ThreadLocalRandom.current().nextInt(goals.size);
        }

        activeGoal = goals.get(r);
        location.getPosition().set(activeGoal.goal.position());
    }

    private record MappedGoal(EntityMapGoal goal, Consumer<EntityMapGoal> executor) {
    }

}
