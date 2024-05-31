package me.vrekt.oasis.entity.enemy;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.combat.CombatDamageAnimator;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.enemy.animation.EnemyAnimation;
import me.vrekt.oasis.world.GameWorld;

/**
 * An enemy entity
 */
public abstract class EntityEnemy extends GameEntity {

    protected final EntityEnemyType type;
    protected float inaccuracy, hostileRange, attackSpeed;
    protected float attackStrength;

    protected final Vector3 worldPosition = new Vector3(),
            screenPosition = new Vector3();

    protected final CombatDamageAnimator animator;
    protected EnemyAnimation animation;

    protected boolean isDying;

    public EntityEnemy(EntityEnemyType type, GameWorld world, OasisGame game) {

        this.worldIn = world;
        this.player = game.getPlayer();

        this.type = type;
        this.animator = new CombatDamageAnimator();
    }

    /**
     * @return type of
     */
    public EntityEnemyType getType() {
        return type;
    }

    @Override
    public void update(float delta) {
        animator.update(delta);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {

    }

    @Override
    public void damage(float tick, float amount, float knockback, boolean isCritical) {
        animator.accumulateDamage(amount, rotation, isCritical);
        super.damage(amount);
    }

    @Override
    public float damage(float amount) {
        animator.accumulateDamage(amount, rotation, false);
        return super.damage(amount);
    }

    /**
     * Render damage amount animations
     *
     * @param batch       the batch
     * @param font        the font
     * @param worldCamera world cam
     * @param guiCamera   gui cam
     */
    public void renderDamageAmountAnimation(SpriteBatch batch, BitmapFont font, Camera worldCamera, Camera guiCamera) {
        worldPosition.set(worldCamera.project(worldPosition.set(body.getPosition().x + 0.1f, body.getPosition().y + 0.25f, 0.0f)));
        screenPosition.set(guiCamera.project(worldPosition));
        animator.drawAccumulatedDamage(batch, font, screenPosition.x, screenPosition.y, getWidth());
    }

}
