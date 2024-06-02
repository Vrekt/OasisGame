package me.vrekt.oasis.entity.npc.wrynn;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiFollowPathComponent;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.component.animation.EntityAnimationBuilder;
import me.vrekt.oasis.entity.component.animation.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.dialog.EntityDialogLoader;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
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
        this.isInParentWorld = true;

        addTexturePart("face", asset.get("wrynn_face"));
        addTexturePart(EntityRotation.UP, asset.get("wrynn_facing_up"), false);
        addTexturePart(EntityRotation.DOWN, asset.get("wrynn_facing_down"), true);
        addTexturePart(EntityRotation.LEFT, asset.get("wrynn_facing_left"), false);
        createBB(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight());

        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        // FIXME, add other walking animations
        final EntityAnimationBuilder builder = new EntityAnimationBuilder(asset)
                .moving(EntityRotation.LEFT, 0.4f, "wrynn_walking_left", 2)
                .add(animationComponent)
                .moving(EntityRotation.DOWN, 0.4f, "wrynn_walking_down", 2)
                .add(animationComponent)
                .moving(EntityRotation.RIGHT, 0.4f, "wrynn_walking_down", 2)
                .add(animationComponent)
                .moving(EntityRotation.UP, 0.4f, "wrynn_walking_down", 2)
                .add(animationComponent);
        builder.dispose();

        dialogue = EntityDialogLoader.load("assets/dialog/wrynn_dialog.json");
        dialogue.setOwner(this);

        activeEntry = dialogue.getEntry("wrynn:dialog_stage_0").getEntry();

        // dialog will be set to complete once the player has the items
        dialogue.addEntryCondition("wrynn:dialog_stage_4", this::checkPlayerHasItems);

        dialogue.addTaskHandler("wrynn:unlock_container", () -> {
            worldIn.enableWorldInteraction(WorldInteractionType.CONTAINER, "wrynn:container");
            player.getQuestManager().advanceQuest(QuestType.A_NEW_HORIZON);
        });

        dialogue.addTaskHandler("wrynn:unlock_basement", () -> {
            parentWorld.findInteriorByType(InteriorWorldType.WRYNN_BASEMENT).setEnterable(true);
            parentWorld.removeSimpleObject("oasis:basement_gate");

            player.getQuestManager().advanceQuest(QuestType.A_NEW_HORIZON);
        });

        createBoxBody(worldIn.boxWorld());

        pathComponent = new AiFollowPathComponent(this, worldIn.getPaths());
        pathComponent.setMaxLinearSpeed(1.25f);
        pathComponent.setMaxLinearAcceleration(1.25f);
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
            drawCurrentPosition(batch, animationComponent.animateMoving(rotation, delta));
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (isSpeakingTo()) {
            if (isMoving()) setVelocity(0.0f, 0.0f, true);
            return;
        }

        updateAi(delta);

        if (pathComponent.isWithinTarget()) {
            setVelocity(0, 0, true);
            pauseFor(MathUtils.random(4.0f, 10.0f));
            pathComponent.pickRandomPoint();
        } else if (isPaused) {
            setVelocity(0, 0, true);
        }

        // only update rotation if we are moving
        if (isMoving()) rotation = pathComponent.getFacingDirection();

        updateRotationTextureState();

        dialogue.update();
    }

    @Override
    public void speak(boolean speakingTo) {
        if (this.speakingTo && !speakingTo) {
            // stopped talking, pause AI for awhile so they don't
            // instantly walk away.
            pauseFor(4f);
        }

        if (speakingTo && !hintShown) {
            // show player hint about how to interact with the dialog system
            game.guiManager.getHudComponent().showPlayerHint(PlayerHints.DIALOG_TUTORIAL_HINT, GameManager.secondsToTicks(10));
            hintShown = true;
        }

        super.speak(speakingTo);
    }

    /**
     * Check if the player has the items from the container
     *
     * @return the result
     */
    public boolean checkPlayerHasItems() {
        return player.getInventory().containsItem(Items.PIG_HEART);
    }

}
