package me.vrekt.oasis.combat;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Renders damage numbers next to the entity when they are hurt
 */
public final class EntityDamageAnimator implements Disposable {

    private final IntMap<Queue<EntityDamage>> damageQueue = new IntMap<>();
    private final BitmapFont drawFont;

    private final Vector2 position = new Vector2();

    public EntityDamageAnimator() {
        this.drawFont = GameManager.asset().getBoxy();
    }

    /**
     * Store damage to be rendered later
     *
     * @param damage damage
     * @param type   type
     */
    public void store(GameEntity entity, float damage, DamageType type) {
        if (!damageQueue.containsKey(entity.entityId())) {
            final LinkedList<EntityDamage> list = new LinkedList<>();
            list.add(new EntityDamage(damage, type));

            damageQueue.put(entity.entityId(), list);
        } else {
            damageQueue.get(entity.entityId()).add(new EntityDamage(damage, type));
        }
    }

    /**
     * Update the queue
     *
     * @param delta delta time
     */
    public void update(float delta) {
        Iterator<EntityDamage> iterator;

        for (IntMap.Entry<Queue<EntityDamage>> entry : damageQueue.entries()) {
            for (iterator = entry.value.iterator(); iterator.hasNext(); ) {
                final EntityDamage damage = iterator.next();
                if (damage.fade <= 0.0f) {
                    iterator.remove();
                } else {
                    damage.offsetY -= MathUtils.random(0.5f, 1.0f);
                    damage.fade -= delta;
                }
            }
        }
    }

    /**
     * Render the damage queue
     *
     * @param batch    batch
     * @param entity   entity
     * @param rotation rotation
     * @param x        x
     * @param y        y
     * @param width    width
     */
    public void render(SpriteBatch batch, GameEntity entity, EntityRotation rotation, float x, float y, float width) {
        if (!damageQueue.containsKey(entity.entityId())) return;

        for (EntityDamage damage : damageQueue.get(entity.entityId())) {
            damage.type.useWith(drawFont, damage.fade);
            applyOffset(rotation, damage, x, y, width);

            drawFont.draw(batch, StringUtils.EMPTY + damage.damage, this.position.x, this.position.y);
        }
    }

    /**
     * Render the damage queue
     *
     * @param batch    batch
     * @param entity   entity
     * @param rotation rotation
     * @param position position
     * @param width    width
     */
    public void render(SpriteBatch batch, GameEntity entity, EntityRotation rotation, Vector3 position, float width) {
        if (!damageQueue.containsKey(entity.entityId())) return;

        for (EntityDamage damage : damageQueue.get(entity.entityId())) {
            damage.type.useWith(drawFont, damage.fade);
            applyOffset(rotation, damage, position.x, position.y, width);

            drawFont.draw(batch, StringUtils.EMPTY + damage.damage, this.position.x, this.position.y);
        }
    }

    /**
     * Apply font offset
     *
     * @param rotation current entity rotation
     * @param damage   damage
     * @param x        x
     * @param y        y
     * @param width    width
     */
    private void applyOffset(EntityRotation rotation, EntityDamage damage, float x, float y, float width) {
        switch (rotation) {
            case UP, DOWN -> position.set(x + damage.offsetX, y + damage.offsetY);
            case RIGHT -> position.set(x + damage.offsetX + width, y + damage.offsetY);
            case LEFT -> position.set(x - (width / 2f), y + damage.offsetY);
        }
    }

    @Override
    public void dispose() {
        damageQueue.clear();
    }
}
