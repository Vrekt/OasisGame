package me.vrekt.oasis.entity.enemy.fsm.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.entity.enemy.animation.EnemyAnimation;
import me.vrekt.oasis.entity.enemy.fsm.ProcessingState;

/**
 * Handles a special animation state
 */
public final class AnimationProcessingState extends ProcessingState {

    public static final int STATE_ID = 1;

    private EnemyAnimation animation;
    private TextureRegion idle;

    public AnimationProcessingState() {
        super(STATE_ID);
    }

    public void animate(EnemyAnimation animation) {
        this.animation = animation;
    }

    public void idle(TextureRegion idle) {
        this.idle = idle;
    }

    public boolean isFinished() {
        return animation.isFinished();
    }

    @Override
    public void enter() {
        animation.activate();
    }

    @Override
    public void update(float delta) {
        if (!animation.isFinished()) animation.update(delta);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (!animation.isFinished()) animation.render(batch, idle);
    }
}
