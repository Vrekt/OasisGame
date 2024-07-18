package me.vrekt.shared.network.state;

import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Represents a basic entity with a type.
 */
public final class NetworkEntityState extends AbstractNetworkEntityState {

    private final EntityType type;

    public NetworkEntityState(int entityId, String name, float x, float y, float vx, float vy, EntityType type) {
        super(entityId, name, x, y, vx, vy);
        this.type = type;
    }

    public NetworkEntityState(GameEntity entity) {
        super(entity);
        this.type = entity.type();
    }

    /**
     * @return the type of entity, players should be excluded from this.
     */
    public EntityType type() {
        return type;
    }
}
