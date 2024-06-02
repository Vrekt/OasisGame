package me.vrekt.oasis.entity.player.sp.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.utility.ResourceLoader;

/**
 * Handles storing combat animations
 */
public final class PlayerCombatAnimator implements ResourceLoader {

    private final CombatAnimation[] animations = new CombatAnimation[4];

    @Override
    public void load(Asset asset) {
        right(asset);
        loadAttackAnimationUp(asset);
        loadAttackAnimationDown(asset);
        loadAttackAnimationLeft(asset);
    }

    private void right(Asset asset) {
        final CombatAnimation animation = new CombatAnimation(0.1f, 4);
        animation.addFrame(asset.get("attack", 1))
                .addFrame(asset.get("attack", 2))
                .addFrame(asset.get("attack_rightv2", 3))
                .addFrame(asset.get("attack_rightv2", 4))
                .build();

        animations[EntityRotation.RIGHT.ordinal()] = animation;
    }

    private void loadAttackAnimationUp(Asset asset) {
        final CombatAnimation animation = new CombatAnimation(0.1f, 4);
        animation.addFrame(asset.get("attack_upv2", 1))
                .addFrame(asset.get("attack_upv2", 2))
                .addFrame(asset.get("attack_upv2", 3))
                .addFrame(asset.get("attack_upv2", 4))
                .build();

        animations[EntityRotation.UP.ordinal()] = animation;
    }

    private void loadAttackAnimationLeft(Asset asset) {
        final CombatAnimation animation = new CombatAnimation(0.1f, 5);
        animation.addFrameOffsetX(asset.get("attack_leftv5", 1))
                .addFrameOffsetX(asset.get("attack_leftv5", 2))
                .addFrameOffsetX(asset.get("attack_leftv5", 3))
                .addFrameOffsetX(asset.get("attack_leftv5", 4))
                .addFrameOffsetX(asset.get("attack_leftv5", 5))
                .build();

        animations[EntityRotation.LEFT.ordinal()] = animation;
    }

    private void loadAttackAnimationDown(Asset asset) {
        final CombatAnimation animation = new CombatAnimation(0.11f, 3);
        animation.addFrameOffsetX(asset.get("attack_downv7", 1))
                .addFrameOffset(asset.get("attack_downv7", 2), 0.59f)
                .addFrameOffset(asset.get("attack_downv7", 3), 0.27f)
                .build();

        animations[EntityRotation.DOWN.ordinal()] = animation;
    }

    /**
     * Render the active animation at x y
     *
     * @param batch    batch
     * @param rotation current rotation
     * @param x        x
     * @param y        y
     * @param delta    delta
     * @return if the animation is finished
     */
    public boolean renderActiveAnimationAt(SpriteBatch batch,
                                           EntityRotation rotation,
                                           float x,
                                           float y,
                                           float delta,
                                           ItemWeapon item) {
        final CombatAnimation animation = animations[rotation.ordinal()];
        animation.update(delta);
        animation.draw(batch, x, y, item);
        final boolean fin = animation.isFinished();
        if (fin) {
            animation.reset();
        }

        return fin;
    }

    public void resetAnimation(EntityRotation rotation) {
        animations[rotation.ordinal()].reset();
    }

}
