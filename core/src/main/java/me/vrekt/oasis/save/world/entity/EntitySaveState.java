package me.vrekt.oasis.save.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * Represents a game entity save
 */
public final class EntitySaveState implements Disposable {

    @Expose
    private EntityType type;
    @Expose
    private String name;
    @Expose
    private String key;
    @Expose
    private int entityId;
    @Expose
    private Vector2 position;
    @Expose
    private Vector3 size;
    @Expose
    private float health;
    @Expose
    private EntityRotation rotation;
    @Expose
    @SerializedName("move_speed")
    private float moveSpeed;
    @Expose
    @SerializedName("entity_data")
    private JsonObject data;

    public EntitySaveState(GameEntity entity) {
        this.type = entity.type();
        this.name = entity.name();
        this.key = entity.key();
        this.entityId = entity.entityId();
        this.position = entity.getPosition();
        this.size = entity.getSizeVector();
        this.health = entity.getHealth();
        this.rotation = entity.rotation();
        this.moveSpeed = entity.getMoveSpeed();
    }

    public EntitySaveState(GameEntity entity, JsonObject data) {
        this.type = entity.type();
        this.name = entity.name();
        this.key = entity.key();
        this.entityId = entity.entityId();
        this.position = entity.getPosition();
        this.size = entity.getSizeVector();
        this.health = entity.getHealth();
        this.rotation = entity.rotation();
        this.moveSpeed = entity.getMoveSpeed();
        this.data = data;
    }

    public EntitySaveState(String key, boolean dead) {
        this.key = key;
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
     * @return key
     */
    public String key() {
        return key;
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

    /**
     * @return other entity data
     */
    public JsonObject data() {
        return data;
    }

    @Override
    public void dispose() {
        name = null;
        key = null;
        position = null;
        size = null;
        data = null;
    }
}
