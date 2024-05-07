package me.vrekt.oasis.save.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;

/**
 * Represents an entities state
 */
public class EntitySaveProperties {

    @Expose
    private String name;
    @Expose
    private int entityId;

    @Expose
    private EntityNPCType type;

    @Expose
    private Vector2 position;
    @Expose
    private Vector3 size;

    @Expose
    private float health;
    @Expose
    private boolean interactable;
    @Expose
    private boolean isEnemy;

    private void createState(Entity entity) {
        this.position = entity.getPosition();
        this.size = new Vector3(entity.getWidth(), entity.getHealth(), entity.getWorldScale());
        this.name = entity.getName();
        this.entityId = entity.getEntityId();
        this.health = entity.getHealth();
        this.interactable = entity.isInteractable();

        if (entity instanceof EntityInteractable entityInteractable) {
            this.type = entityInteractable.getType();
            this.isEnemy = false; // FIXME Enemies
        }

    }

    public String getName() {
        return name;
    }

    public int getEntityId() {
        return entityId;
    }

    public EntityNPCType getType() {
        return type;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector3 getSize() {
        return size;
    }

    public float getHealth() {
        return health;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    public void reset(Entity entity) {
        position = null;
        size = null;
        name = null;
        entityId = 0;
        health = 0.0f;
        interactable = false;

        createState(entity);
    }

}
