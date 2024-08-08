package me.vrekt.oasis.world.tutorial;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.world.GameWorld;

/**
 * Mycelia world
 */
public final class MyceliaWorld extends GameWorld {

    public static final int WORLD_ID = 1;

    public MyceliaWorld(OasisGame game, PlayerSP player) {
        super(game, player, new World(Vector2.Zero, true));

        this.worldName = "Mycelia";
        this.worldMap = Asset.MYCELIA_WORLD;
        this.flipPlayerCollision = true;
        this.worldId = WORLD_ID;
    }

    @Override
    public void loadWorldTiledMap(boolean isGameSave) {
        // scale this player before we create their new box body
        player.scalePlayerBy(0.8f);
        super.loadWorldTiledMap(isGameSave);
        if (!isWorldLoaded) {
            loadTiledMap(game.asset().getWorldMap(worldMap), OasisGameSettings.SCALE);
        }

        renderer.getCamera().zoom = 0.8f;
    }

    @Override
    public void exit() {
        // scale this player before their new box is created
        player.scalePlayerBy(1.0f);
        super.exit();

        renderer.getCamera().zoom = 1.0f;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (super.touchDown(screenX, screenY, pointer, button)) {
            return true;
        }

        player.swingItem();
        return false;
    }

}
