package me.vrekt.oasis.combat;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import org.apache.commons.lang3.RandomUtils;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Animates damage numbers
 */
public final class CombatDamageAnimator implements Disposable {

    private final LinkedList<EntityStoredDamage> damage = new LinkedList<>();

    public void accumulateDamage(float damage, EntityRotation rotation, boolean isCritical) {
        this.damage.add(new EntityStoredDamage(damage, rotation, isCritical));
    }

    public void accumulateDamage(float damage, DamageType type) {
        this.damage.add(new EntityStoredDamage(damage, type));
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
            font.setColor(esd.type.color.r, esd.type.color.g, esd.type.color.b, esd.fade);

            switch (esd.rotation) {
                case UP:
                case DOWN:
                    font.draw(batch, "-" + esd.damage, x + esd.offsetX, y + esd.offsetY);
                    break;
                case LEFT:
                    font.draw(batch, "-" + esd.damage, x - ((width * 2f)), y + esd.offsetY);
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
                        esd.offsetY += RandomUtils.nextFloat(0.25f, 0.5f);
                        break;
                }
                esd.fade -= delta;
            }
        }
    }

    @Override
    public void dispose() {
        damage.clear();
    }

    public static final class EntityStoredDamage {
        final float damage;
        float offsetX, offsetY, fade;
        final boolean isCritical;
        final EntityRotation rotation;
        final DamageType type;

        EntityStoredDamage(float damage, EntityRotation rotation, boolean isCritical) {
            this.damage = damage;
            this.rotation = rotation;
            this.fade = 1.0f;
            this.isCritical = isCritical;
            this.type = isCritical ? DamageType.CRITICAL_HIT : DamageType.NORMAL;
        }

        EntityStoredDamage(float damage, DamageType type) {
            this.damage = damage;
            this.type = type;

            this.rotation = EntityRotation.DOWN;
            this.fade = 1.0f;
            this.isCritical = false;
        }

    }

}
