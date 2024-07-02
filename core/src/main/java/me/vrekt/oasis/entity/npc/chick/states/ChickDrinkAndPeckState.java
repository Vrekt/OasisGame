package me.vrekt.oasis.entity.npc.chick.states;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.ai.goals.EntityGoal;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.animation.EntityAnimation;
import me.vrekt.oasis.entity.enemy.fsm.ProcessingState;
import me.vrekt.oasis.entity.npc.chick.ChickEntity;

/**
 * Chick pecking animation state
 */
public final class ChickDrinkAndPeckState extends ProcessingState {

    public static final int STATE_ID = 2;

    private final ChickEntity entity;
    private EntityAnimation animation;

    private Animation<TextureRegion> splashAnimation;
    private float splashAnimationTime;

    private boolean animate = true;
    private EntityGoal goal;

    private float entered, stateTime;
    private float last = 0;

    public ChickDrinkAndPeckState(ChickEntity entity) {
        super(STATE_ID);
        this.entity = entity;
    }

    public void setSplashAnimation(Animation<TextureRegion> splashAnimation) {
        this.splashAnimation = splashAnimation;
    }

    public void setStateTime(float time) {
        this.stateTime = time;
    }

    public void setActiveAnimation(EntityAnimation animation, EntityGoal goal) {
        this.animation = animation;
        this.goal = goal;
    }

    public boolean isActive() {
        return animate;
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
        animate = true;
        splashAnimationTime = 0.0f;
    }

    @Override
    public void update(float delta) {
        if (animate && animation.isFinished()) {
            animate = false;

            animation.reset();
            last = GameManager.getTick();
        } else if (!animate) {
            animate = GameManager.hasTimeElapsed(last, 1.25f);
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (goal == EntityGoal.DRINK) {
            // play particle drink effect
            splashAnimationTime += delta;
            final TextureRegion frame = splashAnimation.getKeyFrame(splashAnimationTime);

            batch.draw(frame,
                    entity.bodyPosition().x + 0.1f,
                    entity.bodyPosition().y - 0.2f,
                    frame.getRegionWidth() * OasisGameSettings.SCALE,
                    frame.getRegionHeight() * OasisGameSettings.SCALE);
        }

        if (animate) entity.drawCurrentPosition(batch, animation.animate(delta));
    }
}
