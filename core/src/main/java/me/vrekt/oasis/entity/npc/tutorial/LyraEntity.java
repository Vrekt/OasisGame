package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiHostilePursueComponent;
import me.vrekt.oasis.ai.goals.EntityWalkPathGoal;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.component.animation.EntityAnimationBuilder;
import me.vrekt.oasis.entity.component.animation.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.component.status.EntityAlertedStatus;
import me.vrekt.oasis.entity.component.status.EntitySpeakableStatus;
import me.vrekt.oasis.entity.dialog.EntityDialogueLoader;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.utility.collision.CollisionType;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;

/**
 * Misc entity seen wandering around the tutorial world.
 */
public final class LyraEntity extends EntityInteractable {

    public static final String ENTITY_KEY = "oasis:lyra";
    public static final String NAME = "Lyra";

    private EntitySpeakableStatus speakingStatus;

    private EntityAnimationComponent animationComponent;
    private EntityWalkPathGoal pathComponent;
    private AiHostilePursueComponent component;

    private boolean transferred;
    private boolean isFinished;

    public LyraEntity(GameWorld world, Vector2 position, OasisGame game) {
        super(NAME, position, game.getPlayer(), world, game);
        this.key = ENTITY_KEY;
        this.type = EntityType.LYRA;
    }

    @Override
    public void load(Asset asset) {
        super.load(asset);
        this.parentWorld = worldIn;

        disableCollisionFor(CollisionType.PLAYER);
        disableCollisionFor(CollisionType.MAP_BOUNDS);

        addTexturePart("face", asset.get("lyra_face"));
        addTexturePart(EntityRotation.UP, asset.get("lyra_walking_up_idle"), false);
        addTexturePart(EntityRotation.DOWN, asset.get("lyra_walking_down_idle"), false);
        addTexturePart(EntityRotation.LEFT, asset.get("lyra_walking_left_idle"), true);
        addTexturePart(EntityRotation.RIGHT, asset.get("lyra_walking_right_idle"), false);
        createBB(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight());

        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        this.dialogue = EntityDialogueLoader.loadSync("assets/dialog/lyra_dialog.json");
        this.dialogue.setOwner(this);
        activeEntry = dialogue.getEntry("lyra:dialog_stage_0").getEntry();

        final EntityAnimationBuilder builder = new EntityAnimationBuilder(asset)
                .moving(EntityRotation.LEFT, 0.4f, "lyra_walking_left", 2)
                .add(animationComponent)
                .moving(EntityRotation.DOWN, 0.4f, "lyra_walking_down", 2)
                .add(animationComponent)
                .moving(EntityRotation.RIGHT, 0.4f, "lyra_walking_right", 2)
                .add(animationComponent)
                .moving(EntityRotation.UP, 0.4f, "lyra_walking_up", 2)
                .add(animationComponent);
        builder.dispose();

        createRectangleBody(worldIn.boxWorld(), new Vector2(0, 1));
        if (!isNetworked) loadAi();
        this.speakingStatus = getStatus(EntitySpeakableStatus.STATUS_ID);
    }

    @Override
    public void loadAi() {
        pathComponent = new EntityWalkPathGoal(this, worldIn.getPaths(), true);
        pathComponent.setMaxLinearSpeed(1.25f);
        pathComponent.setMaxLinearAcceleration(1.25f);
        pathComponent.setFinalRotation(EntityRotation.UP);
        addAiComponent(pathComponent);
    }

    @Override
    public void speak(boolean speakingTo) {
        super.speak(speakingTo);
        if (speakingTo) {
            // stop the player moving since this a critical dialog
            if (!isFinished) player.disableMovement();
        }
    }

    @Override
    public void endSpeak() {
        super.endSpeak();
        player.enableMovement();
    }

    @Override
    public void transfer(GameWorldInterior interior) {
        super.transfer(interior);
        resetPathingPursuePlayer(interior);
        setRotation(EntityRotation.UP);
        // lyra is alerted because we are in her house
        addStatus(new EntityAlertedStatus(this, game.getAsset()));

        activeEntry = dialogue.getEntry("lyra:dialog2_stage_1").getEntry();
        transferred = true;
    }

    /**
     * Reset the pathing for the interior house
     *
     * @param interior interior
     */
    private void resetPathingPursuePlayer(GameWorldInterior interior) {
        pathComponent = null;
        aiComponents.clear();

        component = new AiHostilePursueComponent(this, player);
        component.setMaxLinearSpeed(1.8f);
        component.setMaxLinearAcceleration(2.0f);
        component.setHostileAttackRange(1.0f);

        setPosition(interior.worldOrigin().x, interior.worldOrigin().y - 1.0f);
        addAiComponent(component);
    }

    private void resetPathingWalkPath() {
        // do not teleport to the first waypoint, walk there instead
        pathComponent = new EntityWalkPathGoal(this, worldIn.getPaths(), false);
        pathComponent.setMaxLinearSpeed(1.25f);
        pathComponent.setMaxLinearAcceleration(1.25f);
        pathComponent.setFinalRotation(EntityRotation.UP);
        addAiComponent(pathComponent);
    }

    /**
     * Reset the state of this entity after walking through the dialog
     */
    private void resetState() {
        isFinished = true;
        addStatus(speakingStatus);

        aiComponents.clear();
        resetPathingWalkPath();

        // give the player the spell book
        player.getInventory().add(Items.ARCANA_CODEX, 1);

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

        if (isMoving()) rotation = rotationFromVelocity();

        // check for end of dialog
        if (!isFinished && activeEntry.getKey().equalsIgnoreCase("lyra:dialog2_stage_9")) {
            resetState();
            return;
        }

        if (isSpeakingTo()) {
            if (isMoving()) setVelocity(0, 0);
            return;
        }

        if (transferred) {
            if (component != null && component.isWithinPlayer(0.88f) && !isFinished) {
                this.speak(true);
                return;
            }
        }

        if ((transferred || (pathComponent != null && !pathComponent.isFinished()))) {
            updateAi(delta);
        }

        updateRotationTextureState();
    }

}
