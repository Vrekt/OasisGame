package me.vrekt.oasis.entity.component.status;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Entity displays "!" above their head
 */
public final class EntityAlertedStatus extends EntityStatus {

    public static final int STATUS_ID = 3;

    private final TextureRegion region;

    public EntityAlertedStatus(GameEntity entity, Asset asset) {
        super(entity, STATUS_ID);

        this.region = asset.get("entity_alerted");
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(region, entity.getX() + 0.15f, entity.getY() + entity.getScaledHeight() + 0.1f, region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);
    }
}
