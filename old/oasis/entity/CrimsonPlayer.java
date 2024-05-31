package me.vrekt.oasis.entity;

import me.vrekt.crimson.game.entity.ServerPlayerEntity;
import gdx.lunar.server.game.LunarServer;
import me.vrekt.crimson.game.network.ServerAbstractConnection;
import me.vrekt.oasis.world.CrimsonServerWorld;

/**
 * Represents a network player in this world
 */
public final class CrimsonPlayer extends ServerPlayerEntity {

    private CrimsonServerWorld worldIn;

    public CrimsonPlayer(LunarServer server, ServerAbstractConnection connection) {
        super(server, connection);
    }

    public void setWorldIn(CrimsonServerWorld worldIn) {
        this.worldIn = worldIn;
    }
}
