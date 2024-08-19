package me.vrekt.shared.network.state;

import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.network.server.entity.ServerEntity;

/**
 * The basic network state of all entities
 */
public abstract class AbstractNetworkEntityState {

    private final int entityId;
    private final String name;
    private final String key;

    private final float x, y;
    private final float vx, vy;
    private final EntityType type;

    public AbstractNetworkEntityState(int entityId,
                                      String name,
                                      String key,
                                      float x,
                                      float y,
                                      float vx,
                                      float vy,
                                      EntityType type) {
        this.entityId = entityId;
        this.name = name;
        this.key = key;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.type = type;
    }

    public AbstractNetworkEntityState(GameEntity entity) {
        this.entityId = entity.entityId();
        this.name = entity.name();
        this.key = entity.key();

        this.x = entity.getPosition().x;
        this.y = entity.getPosition().y;
        this.vx = entity.getVelocity().x;
        this.vy = entity.getVelocity().y;
        this.type = entity.type();
    }

    protected AbstractNetworkEntityState(ServerEntity entity) {
        this.entityId = entity.entityId();
        this.name = entity.name();
        this.key = entity.key();

        this.x = entity.getPosition().x;
        this.y = entity.getPosition().y;
        this.vx = entity.getVelocity().x;
        this.vy = entity.getVelocity().y;
        this.type = entity.type();
    }

    /**
     * @return unique entity ID.
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return name of this player
     */
    public String name() {
        return name;
    }

    /**
     * @return the entity key
     */
    public String key() {
        return key;
    }

    /**
     * @return pos x
     */
    public float x() {
        return x;
    }

    /**
     * @return pos y.
     */
    public float y() {
        return y;
    }

    /**
     * @return vel x
     */
    public float vx() {
        return vx;
    }

    /**
     * @return vel y
     */
    public float vy() {
        return vy;
    }

    /**
     * @return the type
     */
    public EntityType type() {
        return type;
    }
}
