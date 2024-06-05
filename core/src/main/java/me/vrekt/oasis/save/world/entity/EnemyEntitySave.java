package me.vrekt.oasis.save.world.entity;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.enemy.EntityEnemyType;

/**
 * Enemy save data
 */
public final class EnemyEntitySave extends AbstractEntitySaveState {

    @Expose
    private EntityEnemyType type;

    public EnemyEntitySave(EntityEnemy entity) {
        super(entity);

        this.type = entity.type();
    }

    /**
     * @return type of
     */
    public EntityEnemyType type() {
        return type;
    }
}
