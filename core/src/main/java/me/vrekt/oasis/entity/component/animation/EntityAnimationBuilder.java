package me.vrekt.oasis.entity.component.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * Builds entity animations
 */
public final class EntityAnimationBuilder implements Disposable {

    private EntityAnimation animation;
    private EntityRotation rotation;
    private Asset asset;

    public EntityAnimationBuilder(Asset asset) {
        this.asset = asset;
    }

    /**
     * Add a moving animation
     *
     * @param rotation the rotation
     * @param ft       the animation time
     * @param template the asset template
     * @param frames   how many frames
     * @return this
     */
    public EntityAnimationBuilder moving(EntityRotation rotation, float ft, String template, int frames) {
        this.rotation = rotation;
        final TextureRegion[] assets = new TextureRegion[frames];

        for (int i = 0; i < frames; i++) {
            assets[i] = asset.get(template, i + 1);
        }

        final Animation<TextureRegion> a = new Animation<>(ft, assets);
        a.setPlayMode(Animation.PlayMode.LOOP);

        this.animation = new EntityMovingAnimation(a);
        return this;
    }

    /**
     * Add a moving animation
     *
     * @param rotation  the rotation
     * @param ft        the animation time
     * @param template  the asset template
     * @param frames    how many frames
     * @param component the component to automatically add to.
     * @return this
     */
    public EntityAnimationBuilder moving(EntityRotation rotation, float ft, String template, int frames, EntityAnimationComponent component) {
        this.rotation = rotation;
        final TextureRegion[] assets = new TextureRegion[frames];

        for (int i = 0; i < frames; i++) {
            assets[i] = asset.get(template, i + 1);
        }

        final Animation<TextureRegion> a = new Animation<>(ft, assets);
        a.setPlayMode(Animation.PlayMode.LOOP);

        component.add((EntityMovingAnimation) animation, rotation);
        return this;
    }

    /**
     * Attach a hurting animation
     *
     * @param template the asset template
     * @param frames   how many frames
     * @return this
     */
    public EntityAnimationBuilder hurting(String template, int frames) {
        final TextureRegion[] assets = new TextureRegion[frames];

        for (int i = 0; i < frames; i++) {
            assets[i] = asset.get(template, i + 1);
        }

        this.animation = new EntityHurtingAnimation(assets);
        return this;
    }

    /**
     * General animation
     *
     * @param type     type
     * @param ft       frame time
     * @param template template
     * @param frames   how many frames
     * @return this
     */
    public EntityAnimationBuilder animation(AnimationType type, float ft, String template, int frames) {
        final TextureRegion[] assets = new TextureRegion[frames];

        for (int i = 0; i < frames; i++) {
            assets[i] = asset.get(template, i + 1);
        }

        this.animation = new EntityAnimation(type, new Animation<>(ft, assets));
        return this;
    }

    /**
     * Add to the component
     *
     * @param component component
     * @return this
     */
    public EntityAnimationBuilder add(EntityAnimationComponent component) {
        if (rotation != null) {
            component.add((EntityMovingAnimation) animation, rotation);
            // reset rotation here because add is usually called last
            rotation = null;
        } else {
            component.add(animation);
        }
        return this;
    }

    /**
     * Get the active building state
     *
     * @return the state
     */
    public EntityAnimation get() {
        return animation;
    }

    @Override
    public void dispose() {
        asset = null;
        animation = null;
    }
}
