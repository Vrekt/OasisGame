package me.vrekt.oasis.combat;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import org.apache.commons.lang3.RandomUtils;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Animates damage numbers
 */
public final class CombatDamageAnimator {

    private final LinkedList<EntityStoredDamage> damage = new LinkedList<>();

    public void accumulateDamage(float damage, EntityRotation rotation, boolean isCritical) {
        this.damage.add(new EntityStoredDamage(damage, rotation, isCritical));
    }

    public boolean hasDamage() {
        return !damage.isEmpty();
    }

    /**
     * Draw recent damage to entity
     *
     * @param batch drawing
     * @param font  font
     * @param x     X
     * @param y     Y
     */
    public void drawAccumulatedDamage(SpriteBatch batch, BitmapFont font, float x, float y, float width) {
        for (EntityStoredDamage esd : damage) {
            if (esd.isCritical) {
                font.setColor(255, 0, 0, esd.fade);
            } else {
                font.setColor(255, 77, 56, esd.fade);
            }

            switch (esd.rotation) {
                case UP:
                case DOWN:
                    font.draw(batch, "-" + esd.damage, x + esd.offsetX, y + esd.offsetY);
                    break;
                case LEFT:
                    font.draw(batch, "-" + esd.damage, x - ((width * 4f) + esd.offsetX), y + esd.offsetY);
                    break;
                case RIGHT:
                    font.draw(batch, "-" + esd.damage, (x + esd.offsetX) + width, y + esd.offsetY);
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
                switch (esd.rotation) {
                    case UP:
                    case DOWN:
                        esd.offsetY += 0.5f;
                        break;
                    case LEFT:
                    case RIGHT:
                        esd.offsetX += 0.5f;
                        esd.offsetY += RandomUtils.nextFloat(0.25f, 0.5f);
                        break;
                }
                esd.fade -= delta;
            }
        }
    }

    public static final class EntityStoredDamage {
        final float damage;
        float offsetX, offsetY, fade;
        final boolean isCritical;
        final EntityRotation rotation;

        public EntityStoredDamage(float damage, EntityRotation rotation, boolean isCritical) {
            this.damage = damage;
            this.rotation = rotation;
            this.fade = 1.0f;
            this.isCritical = isCritical;
        }
    }

}
