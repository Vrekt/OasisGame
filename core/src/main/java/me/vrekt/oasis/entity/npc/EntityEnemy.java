package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.combat.CombatDamageAnimator;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;

public abstract class EntityEnemy extends EntityDamageable {
    private final Vector3 worldPosition = new Vector3();
    private final Vector3 screenPosition = new Vector3();
    private final CombatDamageAnimator animator;

    public EntityEnemy(String name, Vector2 position, OasisPlayerSP player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(name, position, player, worldIn, game, type);
        this.animator = new CombatDamageAnimator();
        this.speakable = false;
        this.isEnemy = true;
        this.setDrawDialogAnimationTile(false);
    }

    /**
     * "
     * TODO
     *
     * @param rotation rotation of local player
     * @return {@code  true} if the player is facing this entity
     */
    public boolean isFacingEntity(float rotation) {
        return true;
    }

    @Override
    public void damage(float tick, float amount, boolean isCritical) {
        this.animator.accumulateDamage(amount, game.getPlayer().getRotation(), isCritical);
        this.health -= amount;
    }

    @Override
    public void update(float v) {
        super.update(v);
        this.bounds.set(getPosition().x, getPosition().y, getWidthScaled(), getHeightScaled());
        animator.update(v);
    }

    public void drawDamageIndicator(SpriteBatch batch) {
        if (animator.hasDamage()) {
            worldPosition.set(game.getRenderer().getCamera().project(worldPosition.set(getPosition().x + 1.0f, getPosition().y + 2.5f, 0.0f)));
            screenPosition.set(game.getGui().getCamera().project(worldPosition));
            animator.drawAccumulatedDamage(batch, game.getAsset().getBoxy(), screenPosition.x, screenPosition.y, getWidth(), getHeight());
        }
    }

}
