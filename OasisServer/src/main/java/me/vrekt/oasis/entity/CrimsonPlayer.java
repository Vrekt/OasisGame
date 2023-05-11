package me.vrekt.oasis.entity;

import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.network.connection.ServerAbstractConnection;
import me.vrekt.oasis.world.CrimsonServerWorld;

/**
 * Represents a network player in this world
 */
public final class CrimsonPlayer extends LunarServerPlayerEntity {

    private CrimsonServerWorld worldIn;

    public CrimsonPlayer(boolean initializeComponents, LunarServer server, ServerAbstractConnection connection) {
        super(initializeComponents, server, connection);
    }

    public void setWorldIn(CrimsonServerWorld worldIn) {
        this.worldIn = worldIn;
    }
}
