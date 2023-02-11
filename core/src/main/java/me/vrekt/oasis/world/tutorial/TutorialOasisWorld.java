package me.vrekt.oasis.world.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.entity.player.spells.test.PlayerSpellTest;
import me.vrekt.oasis.item.tools.TutorialWand;
import me.vrekt.oasis.world.OasisWorld;

/**
 * This world acts as a debug/tutorial level for now.
 */
public final class TutorialOasisWorld extends OasisWorld {

    private PlayerSpellTest test;

    public TutorialOasisWorld(OasisGame game, OasisPlayerSP player, World world) {
        super(game, player, world);

        getConfiguration().worldScale = OasisGameSettings.SCALE;
        getConfiguration().handlePhysics = true;
        getConfiguration().updateEngine = true;
        getConfiguration().updateEntities = false;
        getConfiguration().updateNetworkPlayers = true;
        getConfiguration().updatePlayer = true;
    }

    @Override
    public void loadIntoWorld() {
        super.loadIntoWorld();
        loadWorld(game.getAsset().getWorldMap(Asset.TUTORIAL_WORLD), OasisGameSettings.SCALE);

        // disable tutorial world interactions until they are unlocked by respective NPC
       // environments.forEach(e -> e.getInteraction().setInteractable(false));

        player.getInventory().giveEntityItem(TutorialWand.class, 1);

        test = new PlayerSpellTest(game);
        test.load(game.getAsset());
    }

    @Override
    public float update(float d) {
        test.update(d);
        return super.update(d);
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        super.renderWorld(batch, delta);
        test.render(batch, delta);
        endRender();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        interactWithEntity();
        interactWithObject();
        interactWithInterior();
        test.reset();
        return false;
    }
}
