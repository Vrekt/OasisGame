package me.vrekt.oasis.save.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

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

}
