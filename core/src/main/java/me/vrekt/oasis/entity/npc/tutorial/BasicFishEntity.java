package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiWanderComponent;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.world.GameWorld;

/**
 * Tutorial world fish that {@link ThaliaEntity} is attempting to catch
 */
public final class BasicFishEntity extends GameEntity {

    public static final String ENTITY_KEY = "oasis:fish";

    // keep within the bounds of the water
    private static final float WANDERING_MAX_Y = 60f;
    private static final float WANDERING_MIN_Y = 57;

    private AiWanderComponent wanderComponent;

    private float animationTime;
    private Animation<TextureRegion> fishAnimation;
    private TextureRegion lastFrame;

    private boolean playingSplashing;
    private float lastPlayedSplashing;

    public BasicFishEntity(GameWorld world, Vector2 position, OasisGame game) {
        this.worldIn = world;
        this.key = ENTITY_KEY;
        this.type = EntityType.FISH;
        this.player = game.getPlayer();

        setName("Fish");
        setPosition(position.x, position.y, false);
    }

    @Override
    public void load(Asset asset) {
        fishAnimation = new Animation<>(0.5f,
                asset.get("fish", 1),
                asset.get("fish", 2),
                asset.get("fish", 3),
                asset.get("fish", 4));
        fishAnimation.setPlayMode(Animation.PlayMode.LOOP);

        createBB(16 * OasisGameSettings.SCALE, 16 * OasisGameSettings.SCALE);
        createBoxBody(worldIn.boxWorld());

        wanderComponent = new AiWanderComponent(this, WANDERING_MIN_Y, WANDERING_MAX_Y);
        wanderComponent.setMaxLinearSpeed(1f);
        wanderComponent.setMaxLinearAcceleration(1f);
        addAiComponent(wanderComponent);
    }

    @Override
    public void update(float delta) {
        wanderComponent.update(delta);

        // play a splashing noise if the player is nearby
        if (!playingSplashing
                && body.getPosition().dst2(player.getPosition()) < 10
                && GameManager.hasTimeElapsed(lastPlayedSplashing, 6.0f)) {
            GameManager.playSound(Sounds.FISH_SPLASHING, 0.1f, -1.0f, 0.1f);

            lastPlayedSplashing = GameManager.getTick();
            playingSplashing = true;
        } else if (playingSplashing) {
            playingSplashing = GameManager.hasTimeElapsed(lastPlayedSplashing, 1.0f);
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        boolean skipFrame = MathUtils.randomBoolean(0.25f);

        // doesn't seem to work correctly, keep for now.
        if (lastFrame == null) lastFrame = fishAnimation.getKeyFrame(animationTime);
        if (!skipFrame) {
            lastFrame = fishAnimation.getKeyFrame(animationTime);
            animationTime += delta;
        }

        batch.draw(lastFrame, body.getPosition().x, body.getPosition().y, lastFrame.getRegionWidth() * OasisGameSettings.SCALE, lastFrame.getRegionHeight() * OasisGameSettings.SCALE);
    }

}
