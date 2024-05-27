package me.vrekt.oasis.item.draw;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.item.Item;

/**
 * A static item renderer
 */
public final class StaticItemRenderer extends ItemRenderer {

    public StaticItemRenderer(TextureRegion region, Item item) {
        super(region, item);
    }

    @Override
    public void updateItemRotation(Vector2 position, EntityRotation rotation) {
        applyStaticRotation(rotation);
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
