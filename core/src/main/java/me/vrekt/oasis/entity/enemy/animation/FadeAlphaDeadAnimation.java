package me.vrekt.oasis.entity.enemy.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Fades out the enemy when they die
 */
public final class FadeAlphaDeadAnimation extends EnemyAnimation {

    private float alpha = 1.0f;

    public FadeAlphaDeadAnimation(GameEntity entity) {
        super(entity);
    }

    @Override
    public void update(float delta) {
        alpha -= delta;

        if (alpha < 0.0f) {
            isAnimating = false;
            isFinished = true;
        }
    }

    @Override
    public void render(SpriteBatch batch, TextureRegion region) {
        batch.setColor(1, 1, 1, alpha);
        entity.drawCurrentPosition(batch, region);
        batch.setColor(Color.WHITE);
    }

}
