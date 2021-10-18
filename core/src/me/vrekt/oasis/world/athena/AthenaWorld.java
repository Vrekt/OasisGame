package me.vrekt.oasis.world.athena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.ui.world.WorldGui;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.farm.FarmAllotmentManager;
import me.vrekt.oasis.world.farm.FarmingAllotment;
import me.vrekt.oasis.world.renderer.WorldRenderer;

/**
 * The world of Athena.
 */
public final class AthenaWorld extends AbstractWorld {

    // farming management
    private FarmAllotmentManager farmManager;

    public AthenaWorld(OasisGame game, Player player, World world, SpriteBatch batch) {
        super(player, world, batch, game.asset);
        this.gui = new WorldGui(game, game.asset, this, multiplexer);

        setHandlePhysics(true);
        setUpdateNetworkPlayers(true);
        setUpdatePlayer(false);
        setUpdateEntities(false);

        this.worldScale = WorldRenderer.SCALE;
    }

    public Player getPlayer() {
        return thePlayer;
    }

    @Override
    protected void preLoadWorld(TiledMap worldMap, float worldScale) {
        Gdx.app.log(ATHENA, "Finished pre-loading Athena.");
    }

    @Override
    protected void loadWorld(TiledMap worldMap, float worldScale) {
        for (FarmingAllotment allotment : this.allotments) allotment.loadAllotment(asset);
        this.farmManager = new FarmAllotmentManager(allotments, thePlayer, gui);

        Gdx.app.log(ATHENA, "Finished loading World: Athena");
    }

    @Override
    protected void handleInteractionKeyPressed() {
        farmManager.interact();

        final EntityInteractable interactable = getClosestEntity();
        if (interactable != null && !interactable.isSpeakingTo()) {
            interactable.setSpeakingTo(true);
            thePlayer.setRotation(interactable.getSpeakingRotation());

            // show dialog
            gui.getDialog().setDialogToRender(interactable, interactable.getDialogSection(), interactable.getDisplay());
            gui.getDialog().show();

            this.entityInteractingWith = interactable;
        }
    }

    @Override
    public void resize(int width, int height) {
        this.fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        this.fboTexture = null;
        this.hasFbo = false;

        renderer.resize(width, height);
        gui.resize(width, height);
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        super.renderWorld(batch, delta);
        this.farmManager.render(batch, worldScale, renderer.getCamera());
    }

    @Override
    public void renderUi() {
        gui.render();
    }

    @Override
    public void update(float d) {
        super.update(d);

        farmManager.update();
        updateDialogState();
    }

    private void updateDialogState() {
        if (gui.getDialog().isShowing()) {
            if (entityInteractingWith == null
                    || !entityInteractingWith.isSpeakingTo()
                    || !entityInteractingWith.isSpeakable()) {
                if (entityInteractingWith != null) entityInteractingWith.setSpeakingTo(false);

                entityInteractingWith = null;
                gui.getDialog().hide();
            }
        }
    }

}
