package me.vrekt.oasis.world.server;

import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.world.impl.WorldAdapter;
import lunar.shared.entity.player.impl.LunarPlayer;

public final class WorldServer extends WorldAdapter {

    public WorldServer(LunarPlayer player, World world) {
        super(player, world);
    }
}
