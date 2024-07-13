package me.vrekt.oasis.entity.component.status;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Mira grabs the book for the player
 */
public final class MiraObjectiveStatus extends EntityStatus {

    private final TextureRegion region;

    private final Color oldColor = new Color();
    private boolean direction;
    private float alpha = 1.0f;

    public MiraObjectiveStatus(GameEntity entity, Asset asset) {
        super(entity);

        this.region = asset.get("mira_get_book");
    }

    @Override
    public void update(float delta) {
        if (direction) {
            alpha -= delta;
            if (alpha <= 0.25f) {
                direction = false;
            }
        } else {
            alpha += delta;
            if (alpha >= 1.0f) {
                direction = true;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        oldColor.set(batch.getColor());
        final float oldAlpha = oldColor.a;

        batch.setColor(1, 1, 1, alpha);
        batch.draw(region, entity.getX() + 0.15f, entity.getY() + entity.getScaledHeight() + 0.1f, region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);

        oldColor.a = oldAlpha;
        batch.setColor(oldColor);
    }

}
