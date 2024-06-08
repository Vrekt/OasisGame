package me.vrekt.oasis.save.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * Represents a game entity save
 */
public abstract class AbstractEntitySaveState {

    @Expose
    protected EntityType type;
    @Expose
    protected String name;
    @Expose
    protected int entityId;
    @Expose
    protected Vector2 position;
    @Expose
    protected Vector3 size;
    @Expose
    protected float health;
    @Expose
    protected EntityRotation rotation;
    @Expose
    @SerializedName("move_speed")
    protected float moveSpeed;

    public AbstractEntitySaveState(GameEntity entity) {
        this.type = entity.type();
        this.name = entity.name();
        this.entityId = entity.entityId();
        this.position = entity.getPosition();
        this.size = entity.getSizeVector();
        this.health = entity.getHealth();
        this.rotation = entity.rotation();
        this.moveSpeed = entity.getMoveSpeed();
    }

    public AbstractEntitySaveState() {
    }

    /**
     * @return type of
     */
    public EntityType type() {
        return type;
    }

    /**
     * @return name
     */
    public String name() {
        return name;
    }

    /**
     * NOTE: Unused for now.
     *
     * @return entity ID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return position
     */
    public Vector2 position() {
        return position;
    }

    /**
     * NOTE: Unused for now.
     *
     * @return size
     */
    public Vector3 size() {
        return size;
    }

    /**
     * @return health
     */
    public float health() {
        return health;
    }

    /**
     * @return rotation
     */
    public EntityRotation rotation() {
        return rotation;
    }

    /**
     * NOTE: Unused for now.
     *
     * @return moving speed
     */
    public float moveSpeed() {
        return moveSpeed;
    }

}
