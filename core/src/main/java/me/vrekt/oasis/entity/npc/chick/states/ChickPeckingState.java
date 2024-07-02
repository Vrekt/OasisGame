package me.vrekt.oasis.entity.npc.chick.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.component.animation.EntityAnimation;
import me.vrekt.oasis.entity.enemy.fsm.ProcessingState;
import me.vrekt.oasis.entity.npc.chick.ChickEntity;

/**
 * Chick pecking animation state
 */
public final class ChickPeckingState extends ProcessingState {

    public static final int STATE_ID = 2;

    private final ChickEntity entity;
    private final EntityAnimation peckingAnimation;

    private boolean shouldPeck = true;

    private float entered, stateTime;
    private float last = 0;

    public ChickPeckingState(ChickEntity entity, EntityAnimation animation) {
        super(STATE_ID);
        this.entity = entity;
        this.peckingAnimation = animation;
    }

    public void setStateTime(float time) {
        this.stateTime = time;
    }

    public boolean isActive() {
        return shouldPeck;
    }

    public boolean isFinished() {
        return GameManager.hasTimeElapsed(entered, stateTime);
    }

    @Override
    public void enter() {
        entered = GameManager.getTick();
        last = GameManager.getTick();
        entity.setVelocity(0, 0, true);
    }

    @Override
    public void exit() {
        shouldPeck = true;
    }

    @Override
    public void update(float delta) {
        if (shouldPeck && peckingAnimation.isFinished()) {
            shouldPeck = false;

            peckingAnimation.reset();
            last = GameManager.getTick();
        } else if (!shouldPeck) {
            shouldPeck = GameManager.hasTimeElapsed(last, 1.25f);
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (shouldPeck) entity.drawCurrentPosition(batch, peckingAnimation.animate(delta));
    }
}
