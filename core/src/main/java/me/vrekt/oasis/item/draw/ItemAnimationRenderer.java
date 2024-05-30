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

        if (!isSwinging && !isResettingSwing) {
            applyIdleRotation(rotation);
        } else {
            if (!isResettingSwing) {
                activeItemRotation += 3.0f;
                System.err.println(activeItemRotation);
                if (activeItemRotation >= 120) {
                    isSwinging = false;
                    isResettingSwing = true;
                }
            } else {
                activeItemRotation -= 3.0f;
                if (activeItemRotation <= 0.0f) {
                    isResettingSwing = false;
                    activeItemRotation = 0.0f;
                }
            }
        }
    }

    @Override
    protected void applyIdleRotation(EntityRotation rotation) {
        super.applyIdleRotation(rotation);
        config.manipulate(rotation, position);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(region,
                position.x + 1.0f,
                position.y + 0.5f,
                0.0f,
                0.0f,
                region.getRegionWidth(),
                region.getRegionHeight(),
                OasisGameSettings.SCALE,
                OasisGameSettings.SCALE,
                activeItemRotation);
    }
}
