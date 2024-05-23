package me.vrekt.oasis.entity.npc.wrynn;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiArrivalComponent;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
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
    private AiArrivalComponent arrivalComponent;

    public WrynnEntity(String name, Vector2 position, PlayerSP player, GameWorld worldIn, OasisGame game, EntityNPCType type) {
        super(name, position, player, worldIn, game, type);
    }

    public AiArrivalComponent getArrivalComponent() {
        return arrivalComponent;
    }

    @Override
    public void load(Asset asset) {
        super.load(asset);

        this.parentWorld = ((GameWorldInterior) worldIn).getParentWorld();

        addTexturePart("face", asset.get("wrynn_face"));
        addTexturePart(EntityRotation.DOWN.name(), asset.get("wrynn_facing_down"));
        activeEntityTexture = addTexturePart(EntityRotation.UP.name(), asset.get("wrynn_facing_up"));
        addTexturePart(EntityRotation.LEFT.name(), asset.get("wrynn_facing_left"));

        setSize(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight(), OasisGameSettings.SCALE);
        this.bounds = new Rectangle(getPosition().x, getPosition().y, getScaledWidth(), getScaledHeight());

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

        arrivalComponent = new AiArrivalComponent(this);
        arrivalComponent.setPathingInterval(GameManager.secondsToTicks(15));
        arrivalComponent.setMaxLinearSpeed(2.0f);
        arrivalComponent.setMaxLinearAcceleration(2.85f);
        arrivalComponent.setTargetArrivalTolerance(1.55f);
        addAiComponent(arrivalComponent);
    }

    @Override
    public TextureRegion getDialogFace() {
        return getTexturePart("face");
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (activeEntityTexture != null) {
            if (!isMoving()) {
                drawCurrentPosition(batch, activeEntityTexture);
            } else {
                drawCurrentPosition(batch, animationComponent.animate(rotation, delta));
            }
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        updateRotationTextureState();

        // don't do AI updates if the player is speaking to this entity
        if (!isSpeakingTo()) {
            // only update the steering if we are not within the target tolerance
            // avoids the steering constantly making small corrections
            // TODO: Maybe just fine tune later but for now it doesn't even work.
            if (!arrivalComponent.isWithinArrivalTarget()) {
                arrivalComponent.update(delta);
                rotation = arrivalComponent.getFacingDirection();
            } else {
                // stop moving, cancels all pending velocity.
                setBodyVelocity(0, 0, true);
            }
        } else {
            // stop moving entirely
            setBodyVelocity(0, 0, true);
            // don't continue going towards the same path after the player is done speaking to us
            // that would look weird.
            arrivalComponent.ignoreLastPath();
        }

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
