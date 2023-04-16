package me.vrekt.oasis.combat;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lunar.shared.drawing.Rotation;
import org.apache.commons.lang3.RandomUtils;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Animates damage numbers
 */
public final class CombatDamageAnimator {

    private final LinkedList<EntityStoredDamage> damage = new LinkedList<>();

    public void accumulateDamage(float damage, float rotation, boolean isCritical) {
        this.damage.add(new EntityStoredDamage(damage, rotation, isCritical));
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
    public void drawAccumulatedDamage(SpriteBatch batch, BitmapFont font, float x, float y, float width, float height) {
        for (EntityStoredDamage esd : damage) {
            if (esd.isCritical) {
                font.setColor(255, 0, 0, esd.fade);
            } else {
                font.setColor(255, 77, 56, esd.fade);
            }

            switch (Rotation.of(esd.rotation)) {
                case FACING_UP:
                case FACING_DOWN:
                    font.draw(batch, Float.toString(esd.damage), x + esd.offsetX, y + esd.offsetY);
                    break;
                case FACING_LEFT:
                    font.draw(batch, Float.toString(esd.damage), x - ((width * 4f) + esd.offsetX), y + esd.offsetY);
                    break;
                case FACING_RIGHT:
                    font.draw(batch, Float.toString(esd.damage), (x + esd.offsetX) + width, y + esd.offsetY);
                    break;
            }
        }
    }

    /**
     * Update the animations
     */
    public void update(float delta) {
        for (Iterator<CombatDamageAnimator.EntityStoredDamage> it = damage.iterator(); it.hasNext(); ) {
            final EntityStoredDamage esd = it.next();
            if (esd.fade <= 0.0f) {
                it.remove();
            } else {
                switch (Rotation.of(esd.rotation)) {
                    case FACING_UP:
                    case FACING_DOWN:
                        esd.offsetY += 0.5f;
                        break;
                    case FACING_LEFT:
                    case FACING_RIGHT:
                        esd.offsetX += 0.5f;
                        esd.offsetY += RandomUtils.nextFloat(0.25f, 0.5f);
                        break;
                }
                //  esd.offsetY += 0.5f;
                esd.fade -= delta;
            }
        }
    }

    public static final class EntityStoredDamage {
        final float damage;
        float offsetX, offsetY, fade;
        final boolean isCritical;
        final float rotation;

        public EntityStoredDamage(float damage, float rotation, boolean isCritical) {
            this.damage = damage;
            this.rotation = rotation;
            this.fade = 1.0f;
            this.isCritical = isCritical;
        }
    }

}
