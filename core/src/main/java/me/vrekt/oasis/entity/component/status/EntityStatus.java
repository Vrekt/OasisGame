package me.vrekt.oasis.entity.component.status;

import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.graphics.Drawable;

/**
 * Basic status
 */
public abstract class EntityStatus implements Drawable {

    protected final GameEntity entity;

    public EntityStatus(GameEntity entity) {
        this.entity = entity;
    }

    /**
     * Update this status
     *
     * @param delta delta
     */
    public abstract void update(float delta);

}
