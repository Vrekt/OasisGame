package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiFollowPathComponent;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.component.animation.EntityAnimationBuilder;
import me.vrekt.oasis.entity.component.animation.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.world.GameWorld;

/**
 * Misc entity seen wandering around the tutorial world.
 */
public final class LyraEntity extends EntityInteractable {

    public static final String ENTITY_KEY = "oasis:lyra";
    public static final String NAME = "Lyra";

    private EntityAnimationComponent animationComponent;
    private AiFollowPathComponent pathComponent;

    public LyraEntity(GameWorld world, Vector2 position, OasisGame game) {
        super(NAME, position, game.getPlayer(), world, game);
        this.key = ENTITY_KEY;
        this.type = EntityType.LYRA;
    }

    @Override
    public void load(Asset asset) {
        super.load(asset);
        this.parentWorld = worldIn;

        addTexturePart("face", asset.get("wrynn_face"));
        addTexturePart(EntityRotation.UP, asset.get("lyra_walking_up_idle"), false);
        addTexturePart(EntityRotation.DOWN, asset.get("lyra_walking_down_idle"), false);
        addTexturePart(EntityRotation.LEFT, asset.get("lyra_walking_left_idle"), true);
        addTexturePart(EntityRotation.RIGHT, asset.get("lyra_walking_right_idle"), false);
        createBB(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight());

        setDrawDialogAnimationTile(false);
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

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
        pathComponent = new AiFollowPathComponent(this, worldIn.getPaths());
        pathComponent.setMaxLinearSpeed(1.25f);
        pathComponent.setMaxLinearAcceleration(1.25f);
        pathComponent.setRotationLocked(false);
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

        if (pathComponent.isWithinTarget(0.76f)) {
            setVelocity(0, 0, true);
            pauseFor(MathUtils.random(4.0f, 10.0f));
            pathComponent.pickRandomPoint();
        } else if (isPaused) {
            setVelocity(0, 0, true);
        }

        // only update rotation if we are moving
        if (isMoving()) rotation = pathComponent.getFacingDirection();

        updateRotationTextureState();
    }

    @Override
    public void speak(boolean speakingTo) {

    }

}
