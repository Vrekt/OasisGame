package me.vrekt.oasis.entity.enemy.fsm;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents a processing state
 */
public abstract class ProcessingState {

    // ideally, unique ID.
    protected final int processingStateId;

    public ProcessingState(int processingStateId) {
        this.processingStateId = processingStateId;
    }

    /**
     * Enter this state
     */
    public void enter() {

    }

    /**
     * Exit this state
     */
    public void exit() {

    }

    /**
     * Update this state
     *
     * @param delta delta
     */
    public abstract void update(float delta);

    /**
     * Optional state rendering
     *
     * @param batch batch
     * @param delta delta
     */
    public void render(SpriteBatch batch, float delta) {

    }

}
