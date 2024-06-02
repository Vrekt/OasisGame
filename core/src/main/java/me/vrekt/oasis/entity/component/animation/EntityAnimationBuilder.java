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
        this.animation = new EntityAnimation();
        this.rotation = rotation;
        final TextureRegion[] assets = new TextureRegion[frames];

        for (int i = 0; i < frames; i++) {
            assets[i] = asset.get(template, i + 1);
        }

        animation.moving(new Animation<>(ft, assets));
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

        animation.setHurting(assets);
        return this;
    }

    /**
     * Add to the component
     *
     * @param component component
     * @return this
     */
    public EntityAnimationBuilder add(EntityAnimationComponent component) {
        component.add(animation, rotation);
        return this;
    }

    @Override
    public void dispose() {
        asset = null;
        animation = null;
    }
}
