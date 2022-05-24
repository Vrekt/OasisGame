package me.vrekt.oasis.world.server;

import gdx.lunar.server.world.ServerWorld;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import me.vrekt.oasis.world.OasisWorld;

public final class WorldServer extends ServerWorld {

    private final OasisWorld localWorld;

    public WorldServer(OasisWorld world, String worldName) {
        super(new ServerWorldConfiguration(), worldName);
        this.localWorld = world;
    }

    @Override
    public void tick() {

    }
}
