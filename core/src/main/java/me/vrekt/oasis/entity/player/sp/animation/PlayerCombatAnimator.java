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
        loadAttackAnimationRight(asset);
        loadAttackAnimationUp(asset);
        loadAttackAnimationDown(asset);
        loadAttackAnimationLeft(asset);
    }

    private void loadAttackAnimationRight(Asset asset) {
        final CombatAnimation animation = new CombatAnimation(0.1f, 4);
        animation.add(asset.get("attack", 1))
                .add(asset.get("attack", 2))
                .add(asset.get("attack_rightv2", 3))
                .add(asset.get("attack_rightv2", 4))
                .build();

        animations[EntityRotation.RIGHT.ordinal()] = animation;
    }

    private void loadAttackAnimationUp(Asset asset) {
        final CombatAnimation animation = new CombatAnimation(0.1f, 4);
        animation.add(asset.get("attack_upv2", 1))
                .add(asset.get("attack_upv2", 2))
                .add(asset.get("attack_upv2", 3))
                .add(asset.get("attack_upv2", 4))
                .build();

        animations[EntityRotation.UP.ordinal()] = animation;
    }

    private void loadAttackAnimationLeft(Asset asset) {
        final CombatAnimation animation = new CombatAnimation(0.1f, 5);
        animation.addWithOffsetX(asset.get("attack_leftv5", 1))
                .addWithOffsetX(asset.get("attack_leftv5", 2))
                .addWithOffsetX(asset.get("attack_leftv5", 3))
                .addWithOffsetX(asset.get("attack_leftv5", 4))
                .addWithOffsetX(asset.get("attack_leftv5", 5))
                .build();

        animations[EntityRotation.LEFT.ordinal()] = animation;
    }

    private void loadAttackAnimationDown(Asset asset) {
        final CombatAnimation animation = new CombatAnimation(0.12f, 3);
        animation.addWithOffsetX(asset.get("attack_downv7", 1))
                .addWithOffsets(asset.get("attack_downv7", 2), 0.93f)
                .addWithOffsets(asset.get("attack_downv7", 3), 0.322f)
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
                                           float scaling,
                                           float delta,
                                           ItemWeapon item) {
        final CombatAnimation animation = animations[rotation.ordinal()];
        animation.update(delta);
        animation.draw(batch, x, y, scaling, item);
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
