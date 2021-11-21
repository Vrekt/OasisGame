package me.vrekt.oasis.world.athena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.world.domains.AbstractDomain;
import me.vrekt.oasis.world.interior.AbstractInterior;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

/**
 * The world of Athena.
 */
public final class AthenaWorld extends AbstractWorld {

    // dialog animation above entities heads.
    private float dialogAnimationTime;
    private Animation<TextureRegion> dialogAnimation;

    public AthenaWorld(OasisGame game, Asset asset, Player player, World world, SpriteBatch batch) {
        super(game, player, world, batch, game.getAsset());
        game.getMultiplexer().addProcessor(player.getInventory());

        setHandlePhysics(true);
        setUpdateNetworkPlayers(true);
        setUpdatePlayer(false);
        setUpdateEntities(false);

        this.worldScale = GlobalGameRenderer.SCALE;
    }

    public Player getPlayer() {
        return thePlayer;
    }

    @Override
    protected void preLoadWorld(TiledMap worldMap, float worldScale) {

    }

    @Override
    protected void loadWorld(TiledMap worldMap, float worldScale) {
        // load floating dialog animation tile
        final TextureAtlas interactions = asset.getAssets();
        this.dialogAnimation = new Animation<>(.35f,
                interactions.findRegion("dialog", 1),
                interactions.findRegion("dialog", 2),
                interactions.findRegion("dialog", 3));

        this.dialogAnimation.setPlayMode(Animation.PlayMode.LOOP);
        Logging.info(this, "Finished loading world.");
    }

    @Override
    public void handleInteractionKeyPressed() {
        final EntityInteractable interactable = getClosestEntity();

        if (interactable != null
                && !interactable.isSpeakingTo()
                && interactable.isSpeakable()) {
            interactable.setSpeakingTo(true);

            gui.getDialog().setDialogToRender(interactable, interactable.getDialogSection(), interactable.getDisplay());
            gui.getDialog().showGui();

            this.entityInteractingWith = interactable;
            return;
        }

        enterInteriorIfPossible();
        enterDomainIfPossible();
    }

    private void enterInstanceIfPossible() {
    }

    /**
     * Enter an interior if possible
     * TODO: Only iterate interiors in view?
     */
    private void enterInteriorIfPossible() {
        for (AbstractInterior interior : interiors.values()) {
            if (interior.isEnterable(thePlayer)) {
                final boolean result = interior.enterInstance(asset, this, game, renderer, thePlayer);
                if (result) {
                    this.exit();
                } else {
                    Logging.error(this, "Failed to load into interior: " + interior);
                }
            }
        }
    }

    private void enterDomainIfPossible() {
        for (AbstractDomain domain : domains.values()) {
            if (!domain.isLocked()) {
                gui.showGui(98);
                //  final boolean result = domain.enterInstance(asset, this, game, renderer, thePlayer);
                //    if (result) {
                //        this.exit();
                //     } else {
                //         Logging.error(this, "Failed to load into domain: " + domain);
                //    }
            }
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

        // render dialog animations above entities
        dialogAnimationTime += delta;
        final TextureRegion frame = dialogAnimation.getKeyFrame(dialogAnimationTime);

        entities.values()
                .stream()
                .filter(EntityInteractable::isWithinDistance).forEach(entity -> {
                    if (entity.doDrawDialogAnimationTile()) {
                        batch.draw(frame, entity.getPosition().x, entity.getPosition().y + entity.getHeight(worldScale),
                                frame.getRegionWidth() * worldScale, frame.getRegionHeight() * worldScale);
                    }
                });
    }

    @Override
    public void renderUi() {
        gui.render();
    }

    @Override
    public void update(float d) {
        super.update(d);
        updateDialogState();
    }

    private void updateDialogState() {
        if (gui.updateDialogState(entityInteractingWith)) {
            entityInteractingWith = null;
        }
    }

}
