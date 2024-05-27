package me.vrekt.oasis.item.draw;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.item.Item;

/**
 * Renders animations for an item
 */
public final class ItemAnimationRenderer extends ItemRenderer {

    private final AnimationRendererConfig config;

    public ItemAnimationRenderer(TextureRegion region,
                                 Item item,
                                 AnimationRendererConfig config) {
        super(region, item);
        this.config = config;
    }

    @Override
    public void updateItemRotation(Vector2 position, EntityRotation rotation) {
        this.position.set(position);
        applyStaticRotation(rotation);
    }

    @Override
    protected void applyStaticRotation(EntityRotation rotation) {
        super.applyStaticRotation(rotation);
        config.manipulate(rotation, position);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(region,
                position.x,
                position.y,
                0.0f,
                0.0f,
                region.getRegionWidth(),
                region.getRegionHeight(),
                OasisGameSettings.SCALE,
                OasisGameSettings.SCALE,
                rotation);
    }
}
