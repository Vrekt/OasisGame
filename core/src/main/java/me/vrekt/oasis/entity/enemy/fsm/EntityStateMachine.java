package me.vrekt.oasis.entity.enemy.fsm;

import me.vrekt.oasis.entity.GameEntity;

/**
 * Basic state machine
 */
public final class EntityStateMachine {

    private final GameEntity owner;
    private ProcessingState state;

    public EntityStateMachine(GameEntity owner) {
        this.owner = owner;
    }

    /**
     * Set the initial state
     *
     * @param state state
     */
    public void initial(ProcessingState state) {
        this.state = state;
        this.state.enter();
    }

    /**
     * Enter a new state
     *
     * @param state the state
     */
    public void enter(ProcessingState state) {
        if (this.state != null) this.state.exit();

        state.enter();
        this.state = state;
    }

    public void exit() {
        if (this.state != null) state.exit();
        this.state = null;
    }

    /**
     * Update current state
     *
     * @param delta delta
     */
    public void update(float delta) {
        if (state != null) state.update(delta);
    }

    /**
     * Check if other is this state
     *
     * @param other state ID
     * @return {@code true} if so
     */
    public boolean isInSameState(int other) {
        return state != null && state.processingStateId == other;
    }

    public boolean isInState(ProcessingState state) {
        return this.state != null && this.state.processingStateId == state.processingStateId;
    }

    public ProcessingState state() {
        return state;
    }

    public void stop() {
        this.state = null;
    }

}
