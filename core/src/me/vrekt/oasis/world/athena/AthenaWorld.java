package me.vrekt.oasis.world.athena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.farm.FarmingAllotment;
import me.vrekt.oasis.world.farm.ui.AllotmentInteractionOption;
import me.vrekt.oasis.world.renderer.WorldRenderer;

/**
 * The world of Athena.
 */
public final class AthenaWorld extends AbstractWorld {

    private final AthenaWorldAssets assets = new AthenaWorldAssets();
    private final OasisGame game;

    private FarmingAllotment allotment;
    private AllotmentInteractionOption allotmentInteractionOption;
    private TextureRegion interactionTexture;

    private AthenaWorldScreen screen;
    private TextureAtlas interactions;

    public AthenaWorld(OasisGame game, Player player, World world, SpriteBatch batch) {
        super(player, world, batch);
        this.game = game;

        setHandlePhysics(true);
        setUpdatePlayer(true);
        setUpdateNetworkPlayers(true);
        this.updateEntities = true;
        this.worldScale = WorldRenderer.SCALE;
    }

    public AthenaWorldAssets getAssets() {
        return assets;
    }

    public Player getPlayer() {
        return thePlayer;
    }

    public AthenaWorldScreen getScreen() {
        return screen;
    }

    @Override
    protected void loadWorld(TiledMap worldMap, float worldScale) {
        this.assets.loadAssets();
        for (FarmingAllotment allotment : this.allotments) {
            allotment.loadAllotment(assets);
        }

        this.interactions = assets.getAtlas("ui/interaction/Interactions.atlas");
        this.screen = new AthenaWorldScreen(game, game.getBatch(), this);
        loadAnimations();

        Gdx.app.log(ATHENA, "Finished loading World: Athena");
    }

    /**
     * Load animations in this world.
     */
    private void loadAnimations() {

    }

    @Override
    protected void preLoadWorld(TiledMap worldMap, float worldScale) {
        Gdx.app.log(ATHENA, "Finished pre-loading Athena.");
    }

    @Override
    public void update(float d) {
        if (this.allotment != null) {
            // ensure we are still close to this allotment.
            if (thePlayer.getPosition().dst2(allotment.getCenter()) >= 11.5) {
                // player is too far away.
                this.screen.hideInteraction();
                this.allotment = null;
                this.allotmentInteractionOption = null;
                this.interactionTexture = null;
                this.screen.hideInteraction();
            }
        } else {
            final FarmingAllotment allotment = getClosestAllotment();
            if (allotment != null && !allotment.isInteractingWith()) {
                this.allotment = allotment;
                this.allotmentInteractionOption = allotment.getInteraction();
                // no interaction can be done (placeholder)
                if (this.allotmentInteractionOption == AllotmentInteractionOption.NONE) return;

                this.interactionTexture = interactions.findRegion(allotmentInteractionOption.getAsset());
                this.screen.showInteractionTexture(interactionTexture);
            }
        }

        super.update(d);
    }

    public void checkInteraction() {
        if (this.allotment != null
                && this.allotmentInteractionOption != null) {
            // interact
            this.allotment.interact(allotmentInteractionOption, thePlayer);

            // reset interaction
            this.screen.hideInteraction();
            this.allotment = null;
            this.allotmentInteractionOption = null;
            this.interactionTexture = null;
        }
    }

    private FarmingAllotment getClosestAllotment() {
        for (FarmingAllotment allotment : allotments) {
            if (thePlayer.getPosition().dst2(allotment.getCenter()) <= 11) {
                return allotment;
            }
        }
        return null;
    }
}
