package me.vrekt.oasis.save.world.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.enemy.EntityEnemy;

/**
 * Enemy save data
 */
public final class EnemyEntitySave extends AbstractEntitySaveState {

    @Expose
    @SerializedName("is_dead")
    private boolean isDead;

    public EnemyEntitySave(EntityEnemy entity) {
        super(entity);

        this.type = entity.type();
        this.isDead = false;
    }

    public EnemyEntitySave(EntityType deadEntityType) {
        this.type = deadEntityType;
        this.isDead = true;
    }

    /**
     * @return if this entity is dead
     */
    public boolean isDead() {
        return isDead;
    }
}
