package me.vrekt.shared.network.state;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.GameEntity;

/**
 * The basic network state of all entities
 */
public abstract class AbstractNetworkEntityState {

    private final int entityId;
    private final String name;

    private final float x, y;
    private final float vx, vy;

    public AbstractNetworkEntityState(int entityId,
                                      String name,
                                      float x,
                                      float y,
                                      float vx,
                                      float vy) {
        this.entityId = entityId;
        this.name = name;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public AbstractNetworkEntityState(int entityId, String name, Vector2 position, Vector2 velocity) {
        this.entityId = entityId;
        this.name = name;
        this.x = position.x;
        this.y = position.y;
        this.vx = velocity.x;
        this.vy = velocity.y;
    }

    public AbstractNetworkEntityState(GameEntity entity) {
        this.entityId = entity.entityId();
        this.name = entity.name();

        this.x = entity.getPosition().x;
        this.y = entity.getPosition().y;
        this.vx = entity.getVelocity().x;
        this.vy = entity.getVelocity().y;
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
}
