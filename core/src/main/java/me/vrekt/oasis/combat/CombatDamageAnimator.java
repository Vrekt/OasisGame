package me.vrekt.oasis.combat;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Animates damage numbers
 */
public final class CombatDamageAnimator {

    private final LinkedList<EntityStoredDamage> damage = new LinkedList<>();

    public void accumulateDamage(float tick, float damage, boolean isCritical) {
        this.damage.add(new EntityStoredDamage(damage, tick, isCritical));
    }

    public boolean hasDamage() {
        return damage.size() > 0;
    }

    /**
     * Draw recent damage to entity
     *
     * @param batch drawing
     * @param font  font
     * @param x     X
     * @param y     Y
     */
    public void drawAccumulatedDamage(SpriteBatch batch, BitmapFont font, float x, float y) {
        for (EntityStoredDamage esd : damage) {
            if (esd.isCritical) {
                font.setColor(255, 0, 0, esd.fade);
            } else {
                font.setColor(1, 1, 1, esd.fade);
            }
            font.draw(batch, Float.toString(esd.damage), x + esd.offsetX, y + esd.offsetY);
        }
    }

    /**
     * Update the animations
     */
    public void update(float tick, float delta) {
        for (Iterator<CombatDamageAnimator.EntityStoredDamage> it = damage.iterator(); it.hasNext(); ) {
            final EntityStoredDamage esd = it.next();
            if ((tick - esd.tick >= 1.0f)) {
                it.remove();
            } else {
                esd.offsetY += 0.5f;
                esd.fade -= delta;
            }
        }
    }

    public static final class EntityStoredDamage {
        final float damage, tick;
        float offsetX, offsetY, fade;
        final boolean isCritical;

        public EntityStoredDamage(float damage, float tick, boolean isCritical) {
            this.damage = damage;
            this.tick = tick;
            this.fade = 1.0f;
            this.isCritical = isCritical;
        }
    }

}
