package me.vrekt.oasis.save.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

/**
 * Data about an entity
 */
public abstract class GameEntitySave {

    @Expose
    private String is;
    @Expose
    private String name;
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

    public GameEntitySave(GameEntity entity) {
        this.is = entity.isInteractable() ? "interactable" : "enemy";
        this.name = entity.name();
        this.entityId = entity.entityId();
        this.position = entity.getPosition();
        this.size = entity.getSizeVector();
        this.health = entity.getHealth();
        this.rotation = entity.rotation();
        this.moveSpeed = entity.getMoveSpeed();
    }

    public GameEntitySave() {
    }

    public String is() {
        return is;
    }

    public String name() {
        return name;
    }

    public int entityId() {
        return entityId;
    }

    public Vector2 position() {
        return position;
    }

    public Vector3 size() {
        return size;
    }

    public float health() {
        return health;
    }

    public EntityRotation rotation() {
        return rotation;
    }

    public float moveSpeed() {
        return moveSpeed;
    }

    /**
     * Handles deserializing of either interactable or enemy
     */
    public static class GameEntitySaveAdapter implements JsonDeserializer<GameEntitySave> {
        @Override
        public GameEntitySave deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject src = json.getAsJsonObject();
            final String is = src.get("is").getAsString();
            if (StringUtils.equals(is, "interactable")) {
                return context.deserialize(json, InteractableEntitySave.class);
            } else {
                return context.deserialize(json, EnemyEntitySave.class);
            }
        }
    }

}
