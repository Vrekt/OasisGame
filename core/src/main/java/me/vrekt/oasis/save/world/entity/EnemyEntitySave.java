package me.vrekt.oasis.save.world.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.enemy.EntityEnemyType;

/**
 * Enemy save data
 */
public final class EnemyEntitySave extends AbstractEntitySaveState {

    @Expose
    private EntityEnemyType type;

    @Expose
    @SerializedName("is_dead")
    private boolean isDead;

    public EnemyEntitySave(EntityEnemy entity) {
        super(entity);

        this.type = entity.type();
        this.isDead = false;
    }

    public EnemyEntitySave(EntityEnemyType deadEntityType) {
        this.type = deadEntityType;
        this.is = "enemy";
        this.isDead = true;
    }

    /**
     * @return type of
     */
    public EntityEnemyType type() {
        return type;
    }

    /**
     * @return if this entity is dead
     */
    public boolean isDead() {
        return isDead;
    }
}
