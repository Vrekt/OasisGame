package me.vrekt.oasis.entity.npc.chick.states;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.ai.goals.EntityGoal;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.animation.EntityAnimation;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
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
    private Animation<TextureRegion> peckAnimation;
    private float splashAnimationTime, peckAnimationTime;

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

    public void setPeckAnimation(Animation<TextureRegion> peckAnimation) {
        this.peckAnimation = peckAnimation;
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
        peckAnimationTime = 0.0f;
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

        final float offsetX = entity.rotation() == EntityRotation.RIGHT ? -0.2f : 0.1f;
        final float offsetY = entity.rotation() == EntityRotation.DOWN ? -0.2f : 0.1f;

        TextureRegion frame = null;
        if (goal == EntityGoal.DRINK) {
            // play particle drink effect
            splashAnimationTime += delta;
            frame = splashAnimation.getKeyFrame(splashAnimationTime);
        } else if (goal == EntityGoal.PECKING) {
            peckAnimationTime += delta;
            frame = peckAnimation.getKeyFrame(peckAnimationTime);
        }

        if (frame != null) batch.draw(frame,
                entity.bodyPosition().x + offsetX,
                entity.bodyPosition().y - offsetY,
                frame.getRegionWidth() * OasisGameSettings.SCALE,
                frame.getRegionHeight() * OasisGameSettings.SCALE);

        if (animate) entity.drawCurrentPosition(batch, animation.animate(delta));
    }
}
