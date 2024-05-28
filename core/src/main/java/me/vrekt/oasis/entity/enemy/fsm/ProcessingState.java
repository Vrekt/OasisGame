package me.vrekt.oasis.entity.enemy.fsm;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class ProcessingState {

    protected final int processingStateId;

    public ProcessingState(int processingStateId) {
        this.processingStateId = processingStateId;
    }

    public void enter() {

    }

    public void exit() {

    }

    public abstract void update(float delta);

    public void render(SpriteBatch batch, float delta) {

    }

}
