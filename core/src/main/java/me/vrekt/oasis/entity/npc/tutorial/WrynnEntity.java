package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.goals.EntityFollowPathGoal;
import me.vrekt.oasis.ai.utility.AiVectorUtility;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.component.animation.EntityAnimationBuilder;
import me.vrekt.oasis.entity.component.animation.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.dialog.EntityDialogueLoader;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
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

    public static final String ENTITY_KEY = "oasis:wrynn";
    public static final String NAME = "Wrynn";

    private boolean hintShown;
    private EntityAnimationComponent animationComponent;
    private EntityFollowPathGoal pathComponent;

    public WrynnEntity(GameWorld world, Vector2 position, OasisGame game) {
        super(NAME, position, game.getPlayer(), world, game);
        this.key = ENTITY_KEY;
        this.type = EntityType.WRYNN;
    }

    @Override
    public void load(Asset asset) {
        super.load(asset);

        this.parentWorld = ((GameWorldInterior) worldIn).getParentWorld();
        this.isInParentWorld = true;

        addTexturePart("face", asset.get("wrynn_face"));
        addTexturePart(EntityRotation.UP, asset.get("wrynn_facing_up"), false);
        addTexturePart(EntityRotation.DOWN, asset.get("wrynn_facing_down"), true);
        addTexturePart(EntityRotation.RIGHT, asset.get("wrynn_facing_right"), false);
        addTexturePart(EntityRotation.LEFT, asset.get("wrynn_facing_left"), false);
        createBB(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight());

        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        final EntityAnimationBuilder builder = new EntityAnimationBuilder(asset)
                .moving(EntityRotation.LEFT, 0.4f, "wrynn_walking_left", 2)
                .add(animationComponent)
                .moving(EntityRotation.DOWN, 0.4f, "wrynn_walking_down", 2)
                .add(animationComponent)
                .moving(EntityRotation.RIGHT, 0.4f, "wrynn_walking_right", 2)
                .add(animationComponent)
                .moving(EntityRotation.UP, 0.4f, "wrynn_walking_up", 2)
                .add(animationComponent);
        builder.dispose();

        dialogue = EntityDialogueLoader.load("assets/dialog/wrynn_dialog.json");
        dialogue.setOwner(this);

        activeEntry = dialogue.getEntry("wrynn:dialog_stage_0").getEntry();

        // dialog will be set to complete once the player has the items
        dialogue.addEntryCondition("wrynn:dialog_stage_4", this::checkPlayerHasItems);
        // if the player has the tutorial book continue
        dialogue.addEntryCondition("wrynn:dialog_stage_6", this::checkPlayerHasBook);
        // take the book
        dialogue.addTaskHandler("wrynn:take_item", () -> player.getInventory().removeFirst(Items.WRYNN_RECIPE_BOOK));

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

        pathComponent = new EntityFollowPathGoal(this, worldIn.getPaths());
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
            // face the player if we haven't already.
            // maybe check if we haven't instead of running this every tick.
            setRotation(AiVectorUtility.faceEntity(player, this));
            updateRotationTextureState();
            return;
        }

        updateAi(delta);

        if (pathComponent.isWithinTarget(1.0f)) {
            setVelocity(0, 0, true);
            pauseFor(MathUtils.random(4.0f, 10.0f));
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
            game.guiManager.getHudComponent().showPlayerHint(PlayerHints.DIALOG_TUTORIAL_HINT, 10, 0.0f);
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

    public boolean checkPlayerHasBook() {
        return player.getInventory().containsItem(Items.WRYNN_RECIPE_BOOK);
    }

}
