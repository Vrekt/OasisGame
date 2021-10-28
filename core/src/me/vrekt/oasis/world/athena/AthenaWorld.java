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
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.player.animation.PlayerAnimations;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.item.items.tools.CommonPickaxeItem;
import me.vrekt.oasis.ui.gui.GameGui;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.obj.InteractableWorldObject;
import me.vrekt.oasis.world.region.WorldRegion;
import me.vrekt.oasis.world.renderer.WorldRenderer;

/**
 * The world of Athena.
 */
public final class AthenaWorld extends AbstractWorld {

    // regions
    private long regionTimer, lastRegionEnter;
    private boolean updateRegionTimer;

    // dialog animation above entities heads.
    private float dialogAnimationTime;
    private Animation<TextureRegion> dialogAnimation;

    public AthenaWorld(OasisGame game, Player player, World world, SpriteBatch batch) {
        super(player, world, batch, game.asset);

        this.gui = new GameGui(game, game.asset, this, multiplexer);
        multiplexer.addProcessor(player.getInventory());

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
        // load floating dialog animation tile
        final TextureAtlas interactions = asset.getAssets();
        this.dialogAnimation = new Animation<>(.35f,
                interactions.findRegion("dialog", 1),
                interactions.findRegion("dialog", 2),
                interactions.findRegion("dialog", 3));

        this.dialogAnimation.setPlayMode(Animation.PlayMode.LOOP);
        this.thePlayer.getInventory().giveItem(new CommonPickaxeItem());
        Gdx.app.log(ATHENA, "Finished loading World: Athena");
    }

    @Override
    protected void handleInteractionKeyPressed() {
        final EntityInteractable interactable = getClosestEntity();
        if (interactable != null && !interactable.isSpeakingTo()) {
            interactable.setSpeakingTo(true);
            thePlayer.setRotation(interactable.getSpeakingRotation());

            gui.getDialog().setDialogToRender(interactable, interactable.getDialogSection(), interactable.getDisplay());
            gui.getDialog().showGui();

            this.entityInteractingWith = interactable;
        }
    }

    @Override
    protected void clicked() {
        if (!gui.getDialog().isVisible()) {

            for (InteractableWorldObject object : objects) {
                if (object.isNear(thePlayer)
                        && object.isBreakable()
                        && !thePlayer.isPickaxeLocked()
                        && thePlayer.getInventory().getEquippedItem() instanceof CommonPickaxeItem) {
                    thePlayer.getAnimations().tickAnimation(PlayerAnimations.MINING, true, 1f);
                    object.interact();

                    if (object.isFinished()) thePlayer.getAnimations().stopAnimation(PlayerAnimations.MINING);
                }
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
        for (EntityInteractable entity : entitiesInVicinity) {
            if (entity.doDrawDialogAnimationTile()) {
                batch.draw(frame, entity.getPosition().x, entity.getPosition().y + entity.getHeight(worldScale),
                        frame.getRegionWidth() * worldScale, frame.getRegionHeight() * worldScale);
            }
        }
    }

    @Override
    public void renderUi() {
        gui.render();
    }

    @Override
    public void update(float d) {
        super.update(d);

        updateDialogState();
        updateRegions();
    }

    private void updateRegions() {
        final long now = System.currentTimeMillis();

        // update entering new regions within the world
        boolean hasNewRegion = false;
        for (WorldRegion region : regions) {
            if (regionIn != region && region.isIn(thePlayer.getX(), thePlayer.getY())) {
                this.regionIn = region;
                hasNewRegion = true;

                // prevent subtitle spam basically
                if (now - lastRegionEnter >= 15000) {
                    this.updateRegionTimer = true;
                    this.lastRegionEnter = now;
                    this.regionTimer = now;

                    //    gui.getRegion().enterRegion("Entering " + region.getRegionName());
                    //     gui.getRegion().show();
                }
            }
        }

        if (!hasNewRegion && regionIn != null && !regionIn.isIn(thePlayer.getX(), thePlayer.getY())) {
            regionIn = null;
        }

        if (updateRegionTimer && (now - regionTimer) >= 2500) {
            updateRegionTimer = false;
            //   gui.getRegion().hide();
        }
    }

    private void updateDialogState() {
        if (gui.getDialog().isVisible()
                && entityInteractingWith != null
                && entityInteractingWith.isSpeakingTo()
                && !entityInteractingWith.isSpeakable()) {

            gui.getDialog().hideGui();
            entityInteractingWith.setSpeakingTo(false);
            entityInteractingWith = null;
        }
    }

}
