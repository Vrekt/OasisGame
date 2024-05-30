package me.vrekt.oasis.item.weapons;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.ItemEquippable;
import me.vrekt.oasis.item.Items;

public abstract class ItemWeapon extends ItemEquippable {

    protected float baseDamage = 1.0f, criticalHitChance, criticalHitDamage, knockbackMultiplier;
    protected float itemPositionAngle;
    protected float itemRotationAngle;
    protected float swingCooldown, lastSwing, swingTime, range;
    protected boolean isSwinging, isResettingSwing;

    protected Rectangle bounds;
    protected float targetAngle;
    protected float swingTick;

    public ItemWeapon(Items itemType, String key, String name, String description) {
        super(itemType, key, name, description);
        bounds = new Rectangle();
    }

    public float getKnockbackMultiplier() {
        return knockbackMultiplier;
    }

    @Override
    public void equip(PlayerSP player) {
        player.equipItem(this);
    }

    public boolean isOnSwingCooldown(float tick) {
        if (getLastSwing() == 0.0f) {
            return false;
        }
        return tick - getLastSwing() < swingCooldown;
    }

    public float getCriticalHitDamage() {
        return criticalHitDamage;
    }

    public float getCriticalHitChance() {
        return criticalHitChance;
    }

    public boolean isCriticalHit() {
        final float percentage = ((float) (Math.random() * 100));
        return percentage < criticalHitChance;
    }

    public float getLastSwing() {
        return lastSwing;
    }

    public void setLastSwing(float lastSwing) {
        this.lastSwing = lastSwing;
    }

    public void swingItem() {
        isSwinging = true;

        //  isSwinging = true;
        //   if (isResettingSwing) {
        // we can reset IF delay passed
        //       if (swingTick > (swingTime / 2f)) {
        //           isResettingSwing = false;
        //   sprite.setRotation(itemPositionAngle);


    }

    public float getRange() {
        return range;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isSwinging() {
        return isSwinging;
    }

    public boolean isSwingingOrResetting() {
        return isSwinging || isResettingSwing;
    }


    @Override
    public void update(float delta, EntityRotation rotation) {


      /*  // TODO: Fix this, bounding box is correct position wise but does not actually extend correctly
        final float posX = rotation == EntityRotation.LEFT ?
                sprite.getX() - (sprite.getWidth() + range) : sprite.getX();
        final float posY = rotation == EntityRotation.DOWN ?
                sprite.getY() - (sprite.getHeight() + range) : sprite.getY();

        final float boundsX = rotation == EntityRotation.RIGHT ?
                sprite.getWidth() + range : sprite.getWidth();
        final float boundsY = rotation == EntityRotation.UP ?
                sprite.getHeight() + range : sprite.getHeight();

        bounds.set(posX, posY, boundsX, boundsY);
        itemRotationAngle = sprite.getRotation();

        if (isSwingingOrResetting()) {
            swingTick += delta;
            final float progress = Math.min(swingTick / 0.5f, 1f);
            float finalAngle;

            if (swingTick > swingTime) {
                isResettingSwing = true;
                isSwinging = false;
                // reset from the current angle of the item, to the original of its position
                final float angle = MathUtils.lerp(itemRotationAngle, itemPositionAngle, progress * 2f);

                if (MathUtils.isEqual(angle, itemPositionAngle, 0.1f)) {
                    // at this point, consider the resetting of the angle complete.
                    swingTick = 0.0f;
                    isResettingSwing = false;
                    finalAngle = itemPositionAngle;
                } else {
                    finalAngle = angle;
                }
            } else {
                isSwinging = true;
                isResettingSwing = false;
                // otherwise, item swing still in process, lerp to the next angle position?
                finalAngle = MathUtils.lerp(itemRotationAngle, targetAngle, progress * 2f);//(progress - 0.5f) * 2f);
            }

            sprite.setRotation(finalAngle);
        } else {
          //  sprite.setRotation(itemPositionAngle);
        }

        animator.setAnimate(isSwinging || isResettingSwing);

        if (animator.isAnimating()) {
            if (isResettingSwing) {
                // reset swing so the animation doesn't look weird, I guess?
                animator.resetAnimationTime();
            }
            animator.updateAnimationTime(delta);
        } else {
            animator.resetAnimationTime();
        }*/
    }

    public float getBaseDamage() {
        return baseDamage;
    }
}
