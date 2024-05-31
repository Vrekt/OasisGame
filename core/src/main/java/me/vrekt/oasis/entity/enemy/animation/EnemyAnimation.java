package me.vrekt.oasis.entity.enemy.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.entity.GameEntity;

/**
 * A basic enemy animation
 */
public abstract class EnemyAnimation {

    protected final GameEntity entity;
    protected boolean isAnimating, isFinished;

    public EnemyAnimation(GameEntity entity) {
        this.entity = entity;
    }

    public abstract void update(float delta);

    public abstract void render(SpriteBatch batch, TextureRegion region);

    public void activate() {
        isAnimating = true;
    }

    /**
     * @return if the animation is complete
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * @return if the animation is in progress
     */
    public boolean isAnimating() {
        return isAnimating;
    }

}
