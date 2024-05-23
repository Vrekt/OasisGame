package me.vrekt.oasis.entity.npc.wrynn;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiFollowPathComponent;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.dialog.EntityDialogLoader;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.instance.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * Wrynn.
 */
public final class WrynnEntity extends EntityInteractable {

    private boolean hintShown;
    private EntityAnimationComponent animationComponent;
    private AiFollowPathComponent pathComponent;

    public WrynnEntity(String name, Vector2 position, PlayerSP player, GameWorld worldIn, OasisGame game, EntityNPCType type) {
        super(name, position, player, worldIn, game, type);
    }

    @Override
    public void load(Asset asset) {
        super.load(asset);

        this.parentWorld = ((GameWorldInterior) worldIn).getParentWorld();

        addTexturePart("face", asset.get("wrynn_face"));
        addTexturePart(EntityRotation.UP, asset.get("wrynn_facing_up"), true);
        addTexturePart(EntityRotation.DOWN, asset.get("wrynn_facing_down"), false);
        addTexturePart(EntityRotation.LEFT, asset.get("wrynn_facing_left"), false);
        createBB(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight());

        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        animationComponent.createMoveAnimation(EntityRotation.LEFT, 0.4f,
                asset.get("wrynn_walking_left", 1),
                asset.get("wrynn_walking_left", 2));
        animationComponent.createMoveAnimation(EntityRotation.DOWN, 0.4f,
                asset.get("wrynn_walking_down", 1),
                asset.get("wrynn_walking_down", 2));

        // FIXME, add other walking animations
        animationComponent.createMoveAnimation(EntityRotation.RIGHT, 0.4f,
                asset.get("wrynn_walking_down", 1),
                asset.get("wrynn_walking_down", 2));
        animationComponent.createMoveAnimation(EntityRotation.UP, 0.4f,
                asset.get("wrynn_walking_down", 1),
                asset.get("wrynn_walking_down", 2));

        dialogue = EntityDialogLoader.load("assets/dialog/wrynn_dialog.json");
        dialogue.setOwner(this);

        activeEntry = dialogue.getEntry("wrynn:dialog_stage_0").getEntry();

        // dialog will be set to complete once the player has the items
        dialogue.addEntryCondition("wrynn:dialog_stage_4", this::checkPlayerHasItems);
        dialogue.addTaskHandler("wrynn:unlock_container", () -> worldIn.enableWorldInteraction(WorldInteractionType.CONTAINER, "wrynn:container"));
        dialogue.addTaskHandler("wrynn:unlock_basement", () -> {
            parentWorld.findInteriorByType(InteriorWorldType.WRYNN_BASEMENT).setEnterable(true);
            parentWorld.removeSimpleObject("oasis:basement_gate");
        });

        createBoxBody(worldIn.getEntityWorld());

        pathComponent = new AiFollowPathComponent(this, worldIn.getPaths());
        pathComponent.setMaxLinearSpeed(2.0f);
        pathComponent.setMaxLinearAcceleration(2.1f);
        addAiComponent(pathComponent);
    }

    @Override
    public TextureRegion getDialogFace() {
        return getTexturePart("face");
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        super.render(batch, delta);

        if (!isMoving()) {
            drawCurrentPosition(batch, activeEntityTexture);
        } else {
            drawCurrentPosition(batch, animationComponent.animate(rotation, delta));
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updateAi(delta);

        if (pathComponent.isWithinTarget()) {
            setBodyVelocity(0, 0, true);
            pauseFor(MathUtils.random(4.0f, 10.0f));
        } else if (isPaused) {
            setBodyVelocity(0, 0, true);
        }

        // only update rotation if we are moving
        if (isMoving()) rotation = pathComponent.getFacingDirection();

        updateRotationTextureState();

        setMoving(!getVelocity().isZero());
        dialogue.update();
    }

    @Override
    public void speak(boolean speakingTo) {
        super.speak(speakingTo);

        if (speakingTo && !hintShown) {
            // show player hint about how to interact with the dialog system
            game.guiManager.getHudComponent().showPlayerHint(PlayerHints.DIALOG_TUTORIAL_HINT, GameManager.secondsToTicks(10));
            hintShown = true;
        }
    }

    /**
     * Check if the player has the items from the container
     *
     * @return the result
     */
    public boolean checkPlayerHasItems() {
        return player.getInventory().hasItem(Items.PIG_HEART);
    }

}
