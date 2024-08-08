package me.vrekt.oasis.item.weapons;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.ItemEquippable;
import me.vrekt.oasis.item.Items;

public abstract class ItemWeapon extends ItemEquippable {

    protected float baseDamage = 1.0f, criticalHitChance, criticalHitDamage, knockbackMultiplier;
    protected float swingCooldown, lastSwing, range;
    protected boolean isSwinging;


    protected final Rectangle bounds;
    protected float width, height;

    public ItemWeapon(Items itemType, String key, String name, String description) {
        super(itemType, key, name, description);
        bounds = new Rectangle();
    }

    @Override
    public void equip(PlayerSP player) {
        player.equipItem(this);
    }

    public void updateItemPosition(float x, float y) {
        bounds.setPosition(x, y);
    }

    public void updateItemBoundingSize(EntityRotation rotation) {
        bounds.setSize(
                bounds.width + rotation.vector2.x * OasisGameSettings.SCALE,
                bounds.height + rotation.vector2.y * OasisGameSettings.SCALE
        );
    }

    public void resetBoundingSize() {
        bounds.setSize(width, height);
    }

    public float getKnockbackMultiplier() {
        return knockbackMultiplier;
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

    /**
     * @return {@code true} if this item can be swung
     */
    public boolean canSwing() {
        return GameManager.hasTimeElapsed(lastSwing, swingCooldown);
    }

    /**
     * @return {@code true} if this item is actively swinging
     */
    public boolean isSwinging() {
        return isSwinging;
    }

    /**
     * Swing this weapon
     */
    public void swingItem() {
        isSwinging = true;
        lastSwing = GameManager.tick();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void resetSwing() {
        isSwinging = false;
    }

    public float getRange() {
        return range;
    }

    public float getBaseDamage() {
        return baseDamage;
    }
}
