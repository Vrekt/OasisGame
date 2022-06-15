package me.vrekt.oasis.world.instance;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Just a default world for interiors!
 * TODO: Reusable!?
 */
public final class SeparateWorldInstance extends OasisWorld {

    private final String mapName;

    public SeparateWorldInstance(OasisGame game, OasisPlayerSP player, String mapName) {
        super(game, player, new World(Vector2.Zero, true));
        this.mapName = mapName;
    }

    @Override
    public void loadIntoWorld() {
        super.loadIntoWorld();
        loadWorld(game.getAsset().getWorldMap(mapName), OasisGameSettings.SCALE);
    }

}
