package me.vrekt.oasis.network.server.entity;

import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.network.IntegratedGameServer;
import me.vrekt.oasis.network.server.world.ServerWorld;

/**
 * Default adapter for {@link AbstractServerEntity}
 */
public final class ServerEntity extends AbstractServerEntity {

    public ServerEntity(IntegratedGameServer server, ServerWorld world, GameEntity entity) {
        super(server);

        setName(entity.name());
        setKey(entity.key());
        setEntityId(entity.entityId());
        setPosition(entity.getPosition());
        setVelocity(entity.getVelocity());
        setType(entity.type());
        setWorldIn(world);
    }
}
