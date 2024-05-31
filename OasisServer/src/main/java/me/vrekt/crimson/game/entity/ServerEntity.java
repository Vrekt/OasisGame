package me.vrekt.crimson.game.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.world.World;

/**
 * Represents a base entity
 */
public abstract class ServerEntity implements Disposable {

    protected CrimsonGameServer server;
    protected World world;
    protected boolean inWorld;

    protected int entityId;
    protected String name;

    protected Vector2 position, velocity;
    protected float rotation;

    public ServerEntity(CrimsonGameServer server) {
        this.server = server;

        this.position = new Vector2();
        this.velocity = new Vector2();
    }

    /**
     * @return the server
     */
    public CrimsonGameServer server() {
        return server;
    }

    /**
     * Set the world this entity is in
     *
     * @param world the world
     */
    public void setWorldIn(World world) {
        this.world = world;
        this.inWorld = world != null;
    }

    /**
     * @return the world this entity is in
     */
    public World world() {
        return world;
    }

    /**
     * @ {@code true} if this entity is in a world.
     */
    public boolean isInWorld() {
        return inWorld;
    }

    /**
     * Set the entityId of this entity
     *
     * @param id the id
     */
    public void setEntityId(int id) {
        this.entityId = id;
    }

    /**
     * @return this entities unique ID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * Set the name of this entity
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name of this entity.
     * Possibly {@code null} depending on the situation
     */
    public String name() {
        return name;
    }

    /**
     * Set the position of this entity
     *
     * @param x x
     * @param y y
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    /**
     * Set the position of this entity
     *
     * @param position position
     */
    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    /**
     * @return the position of this entity
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Set the velocity of this entity
     *
     * @param x velocity X
     * @param y velocity Y
     */
    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }

    /**
     * Set the velocity of this entity
     *
     * @param velocity velocity
     */
    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    /**
     * @return the velocity of this entity
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Set the rotation of this entity
     *
     * @param rotation rotation
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     * @return the rotation of this entity
     */
    public float getRotation() {
        return rotation;
    }

    @Override
    public void dispose() {
        world = null;
        inWorld = false;
        entityId = -1;
        name = null;
    }

}
