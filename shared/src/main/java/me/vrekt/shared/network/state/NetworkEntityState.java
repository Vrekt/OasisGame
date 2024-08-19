package me.vrekt.shared.network.state;

import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.network.server.entity.ServerEntity;

/**
 * Represents a basic entity with a type.
 */
public final class NetworkEntityState extends AbstractNetworkEntityState {

    public NetworkEntityState(int entityId, String name, String key, float x, float y, float vx, float vy, EntityType type) {
        super(entityId, name, key, x, y, vx, vy, type);
    }

    public NetworkEntityState(GameEntity entity) {
        super(entity);
    }

    public NetworkEntityState(ServerEntity entity) {
        super(entity);
    }
}
