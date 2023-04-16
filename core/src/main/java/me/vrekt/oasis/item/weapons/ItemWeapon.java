package me.vrekt.oasis.item.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.item.ItemEquippable;

public abstract class ItemWeapon extends ItemEquippable {

    protected float baseDamage = 1.0f, rotationAngle, criticalHitChance, criticalHitDamage;
    protected float swingCooldown, lastSwing, swingTime, range;
    protected boolean isSwinging, isResettingSwing, drawSwipe;
    protected Sprite swipe;

    protected Rectangle bounds;

    public ItemWeapon(String itemName, int itemId, String description) {
        super(itemName, itemId, description);

        bounds = new Rectangle();
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

    public void swingItem(float delta) {
        if (isResettingSwing) return;
        rotationAngle += delta * 360f / swingTime;
        isSwinging = true;
        drawSwipe = true;
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

    @Override
    public void update(float delta) {
        bounds.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight() + range);

        if (isSwinging) {
            swingItem(delta);
            if (rotationAngle >= 60) {
                isSwinging = false;
                isResettingSwing = true;
            }
        } else if (isResettingSwing) {
            rotationAngle -= delta * 360f / swingTime;
            if (rotationAngle <= 0.0f) {
                isResettingSwing = false;
            }
        }
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public float getBaseDamage() {
        return baseDamage;
    }
}
