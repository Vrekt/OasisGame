package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.goals.EntityWalkPathGoal;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.component.animation.EntityAnimationBuilder;
import me.vrekt.oasis.entity.component.animation.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.dialog.EntityDialogueLoader;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.world.GameWorld;

/**
 * Misc entity seen wandering around the tutorial world.
 */
public final class LyraEntity extends EntityInteractable {

    public static final String ENTITY_KEY = "oasis:lyra";
    public static final String NAME = "Lyra";

    private EntityAnimationComponent animationComponent;
    private EntityWalkPathGoal pathComponent;

    public LyraEntity(GameWorld world, Vector2 position, OasisGame game) {
        super(NAME, position, game.getPlayer(), world, game);
        this.key = ENTITY_KEY;
        this.type = EntityType.LYRA;
    }

    @Override
    public void load(Asset asset) {
        super.load(asset);
        this.parentWorld = worldIn;

        addTexturePart("face", asset.get("lyra_face"));
        addTexturePart(EntityRotation.UP, asset.get("lyra_walking_up_idle"), false);
        addTexturePart(EntityRotation.DOWN, asset.get("lyra_walking_down_idle"), false);
        addTexturePart(EntityRotation.LEFT, asset.get("lyra_walking_left_idle"), true);
        addTexturePart(EntityRotation.RIGHT, asset.get("lyra_walking_right_idle"), false);
        createBB(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight());

        setDrawDialogAnimationTile(false);
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        dialogue = EntityDialogueLoader.load("assets/dialog/lyra_dialog.json");
        dialogue.setOwner(this);

        activeEntry = dialogue.getEntry("lyra:dialog_stage_0").getEntry();

        // FIXME: add other walking animations
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

        createBoxBody(worldIn.boxWorld());
        pathComponent = new EntityWalkPathGoal(this, worldIn.getPaths());
        pathComponent.setMaxLinearSpeed(1.25f);
        pathComponent.setMaxLinearAcceleration(1.25f);
        pathComponent.setFinalRotation(EntityRotation.UP);
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
            if (isMoving()) setVelocity(0, 0, true);
            return;
        }

        if (!pathComponent.isFinished()) {
            updateAi(delta);
            rotation = pathComponent.getFacingDirection();
        } else {
            // TODO:
        }

        updateRotationTextureState();
    }

}
