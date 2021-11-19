package me.vrekt.oasis.entity.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.server.game.utilities.Disposable;

import java.util.HashMap;
import java.util.Map;

public final class EntityAnimationRenderer implements Disposable {

    /**
     * Animations by rotation
     */
    protected final Map<Rotation, AnimationPair> animations = new HashMap<>();

    protected final TextureAtlas atlas;
    protected final Map<Rotation, String> walkingAnimations;
    protected final Map<Rotation, String> idleAnimations;

    protected final float width, height;
    protected final boolean offsetBox2d;

    /**
     * Current rotation
     */
    protected Rotation rotation;

    /**
     * Active and idle
     */
    protected Animation<TextureRegion> animation;
    protected TextureRegion idle;

    protected float animationTime;
    protected boolean animate;

    /**
     * Track last known rotations
     */
    protected Rotation lastAnimationRotation, lastIdleRotation;

    /**
     * Initializes a new renderer.
     *
     * @param atlas                 the atlas of player textures and animations.
     * @param rotation              default starting rotation
     * @param walkingAnimationNames the names of the walking animations keyed by rotation.
     * @param idleAnimationNames    the names of the idle animations keyed by rotation.
     * @param width                 the width of the player
     * @param height                the height of the player
     * @param offsetBox2d           if player position should be offset to fit inside box2d bounds.
     */
    public EntityAnimationRenderer(TextureAtlas atlas, Rotation rotation,
                                   Map<Rotation, String> walkingAnimationNames,
                                   Map<Rotation, String> idleAnimationNames,
                                   float width, float height, boolean offsetBox2d) {
        this.atlas = atlas;
        this.rotation = rotation;
        this.walkingAnimations = walkingAnimationNames;
        this.idleAnimations = idleAnimationNames;
        this.width = width;
        this.height = height;
        this.offsetBox2d = offsetBox2d;
    }

    /**
     * Initialize a new renderer.
     * This will use default settings for naming conventions
     *
     * @param atlas       the atlas
     * @param rotation    default starting rotation
     * @param width       the width of the player
     * @param height      the height of the player
     * @param offsetBox2d if player position should be offset to fit inside box2d bounds.
     */
    public EntityAnimationRenderer(TextureAtlas atlas, Rotation rotation, float width, float height, boolean offsetBox2d) {
        this.atlas = atlas;
        this.rotation = rotation;
        this.width = width;
        this.height = height;
        this.offsetBox2d = offsetBox2d;

        this.walkingAnimations = new HashMap<>();
        this.idleAnimations = new HashMap<>();
    }


    /**
     * Load this renderer.
     */
    public void load() {
        final Animation<TextureRegion> walkingAnimation = createAnimation("mavia_walking_up", atlas, true);
        final TextureRegion idleTexture = atlas.findRegion("mavia_walking_up_idle");
        this.animations.put(rotation, new AnimationPair(walkingAnimation, idleTexture));

        this.idle = idleTexture;
        this.animation = walkingAnimation;

        // free some memory
        this.walkingAnimations.clear();
        this.idleAnimations.clear();
    }

    /**
     * Update this renderer
     *
     * @param rotation    rotation of the player.
     * @param hasVelocity if the player has velocity.
     */
    public void update(Rotation rotation, boolean hasVelocity) {
        animate = hasVelocity;

        if (!animate) animationTime = 0f;

        // ensure we are not constantly getting the texture over and over.
        if (!animate && rotation != lastIdleRotation) {
            lastIdleRotation = rotation;
            idle = animations.get(rotation).idle;
        }

        // ensure we are not constantly getting the animation over and over.
        if (animate && rotation != lastAnimationRotation) {
            lastAnimationRotation = rotation;
            animation = animations.get(rotation).animation;
        }
    }

    /**
     * Draw
     *
     * @param delta the delta time
     * @param x     x location to draw at
     * @param y     y location to draw at
     * @param batch the batch
     */
    public void render(float delta, float x, float y, SpriteBatch batch) {
        if (!animate) {
            drawIdleState(x, y, batch);
        } else {
            drawAnimationFrame(x, y, batch);
            animationTime += delta;
        }
    }

    /**
     * offset the player position to fit within the box2d bounds
     *
     * @param x     x location to draw at
     * @param y     y location to draw at
     * @param batch batch
     */
    protected void drawIdleState(float x, float y, SpriteBatch batch) {
        batch.draw(idle,
                x,
                y,
                width,
                height);
    }

    /**
     * offset the player position to fit within the box2d bounds
     *
     * @param x     x location to draw at
     * @param y     y location to draw at
     * @param batch batch
     */
    protected void drawAnimationFrame(float x, float y, SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(animationTime),
                x,
                y,
                width,
                height);
    }

    /**
     * Create a new animation
     *
     * @param region the region name
     * @param atlas  the textures
     * @param loop   if the animation should be looping.
     * @return a new {@link Animation}
     */
    protected Animation<TextureRegion> createAnimation(String region, TextureAtlas atlas, boolean loop) {
        final Animation<TextureRegion> animation = new Animation<>(0.25f, atlas.findRegion(region, 1), atlas.findRegion(region, 2));
        if (loop) animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }

    @Override
    public void dispose() {
        this.animations.clear();
        this.idle = null;
        this.animation = null;
        this.walkingAnimations.clear();
        this.idleAnimations.clear();
        this.atlas.dispose();
    }

    /**
     * Represents a complete pair of a movement stage.
     * The animation and idle state of the direction player is moving.
     */
    protected static class AnimationPair {
        protected final Animation<TextureRegion> animation;
        protected final TextureRegion idle;

        public AnimationPair(Animation<TextureRegion> animation, TextureRegion idle) {
            this.animation = animation;
            this.idle = idle;
        }
    }

}
