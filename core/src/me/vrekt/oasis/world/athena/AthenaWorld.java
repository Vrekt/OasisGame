package me.vrekt.oasis.world.athena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.ui.world.GameWorldInterface;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.farm.FarmAllotmentManager;
import me.vrekt.oasis.world.farm.FarmingAllotment;
import me.vrekt.oasis.world.renderer.WorldRenderer;
import me.vrekt.oasis.world.shop.Shop;

/**
 * The world of Athena.
 */
public final class AthenaWorld extends AbstractWorld {

    private final GameWorldInterface ui;

    // farming management
    private FarmAllotmentManager farmManager;

    public AthenaWorld(OasisGame game, Player player, World world, SpriteBatch batch) {
        super(player, world, batch, game.asset);
        this.ui = new GameWorldInterface(game, game.asset, this);

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
        this.farmManager = new FarmAllotmentManager(allotments, thePlayer, ui);

        Gdx.app.log(ATHENA, "Finished loading World: Athena");
    }

    @Override
    public void handleInteractionKeyPressed() {
        farmManager.interact();

        for (Shop shop : shops) {
            shop.interact(thePlayer, ui);
        }
        final EntityInteractable interactable = getClosestEntity();
        if (interactable != null && !interactable.isSpeakingTo()) {
            interactable.setSpeakingTo(true);
            thePlayer.setRotation(interactable.getSpeakingRotation());
            ui.showDialog(interactable, interactable.getDialogSection(), interactable.getDisplay());

            this.entityInteractingWith = interactable;
        }
    }

    @Override
    public GameWorldInterface getUi() {
        return ui;
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
        ui.resize(width, height);
    }

    @Override
    public void pause() {
        ui.pause();
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        super.renderWorld(batch, delta);
        this.farmManager.render(batch, worldScale, renderer.getCamera());
    }

    @Override
    public void renderUi() {
        ui.render();
    }

    @Override
    public void update(float d) {
        super.update(d);

        farmManager.update();
        updateDialogState();
    }

    private void updateDialogState() {
        if (ui.isShowingDialog()) {
            if (entityInteractingWith == null
                    || !entityInteractingWith.isSpeakingTo()
                    || !entityInteractingWith.isSpeakable()) {
                if (entityInteractingWith != null) entityInteractingWith.setSpeakingTo(false);

                entityInteractingWith = null;
                ui.hideDialog();
            }
        }
    }

}
