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
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.district.DistrictEntranceGui;
import me.vrekt.oasis.item.items.weapons.PrototypeTimepiercerWeapon;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.districts.AbstractDistrict;
import me.vrekt.oasis.world.interior.AbstractInterior;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The world of Athena.
 */
public final class AthenaWorld extends AbstractWorld {

    // dialog animation above entities heads.
    private float dialogAnimationTime;
    private Animation<TextureRegion> dialogAnimation, test;
    private boolean anim, reset;
    private long tim2e;
    private int index;

    private float y, c;
    private boolean swing;

    private TextureRegion[] regions = new TextureRegion[3];

    private TextureRegion region;

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

        thePlayer.getInventory().giveItem(new PrototypeTimepiercerWeapon(game.getAsset()));
        thePlayer.getInventory().equipItem(0);

        Logging.info(this, "Finished loading world.");
    }

    @Override
    public void handleInteractionKeyPressed() {
        final EntityInteractable interactable = getClosestEntity();

        if (interactable != null
                && !interactable.isSpeakingTo()
                && interactable.isSpeakable()) {
            interactable.setSpeakingTo(true);

            gui.showEntityDialog(interactable);
            gui.showGui(GuiType.DIALOG);

            this.entityInteractingWith = interactable;
            return;
        }

        enterInteriorIfPossible();
        showDomainEntranceGui();
    }

    /**
     * Enter an interior if possible
     * TODO: Only iterate interiors in view?
     */
    private void enterInteriorIfPossible() {
        for (AbstractInterior interior : interiors.values()) {
            if (interior.isInView(renderer.getCamera())
                    && interior.isEnterable(thePlayer)) {
                final boolean result = interior.enterInstance(asset, this, game, renderer, thePlayer);
                if (result) {
                    this.exit();
                } else {
                    Logging.error(this, "Failed to load into interior: " + interior);
                }
            }
        }
    }

    private void showDomainEntranceGui() {
        for (AbstractDistrict district : domains.values()) {
            if (district.isInView(renderer.getCamera()) && district.isEnterable(thePlayer)) {
                ((DistrictEntranceGui) gui.getGui(GuiType.DISTRICT)).setDistrictTryingToEnter(district);
                gui.showGui(GuiType.DISTRICT);
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tim2e = System.currentTimeMillis();
        anim = true;
        swing = true;
        region = regions[ThreadLocalRandom.current().nextInt(0, 2)];
        y = 0;
        // index = 0;

        return false;
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
