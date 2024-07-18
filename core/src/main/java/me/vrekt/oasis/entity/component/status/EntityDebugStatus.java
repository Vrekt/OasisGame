package me.vrekt.oasis.entity.component.status;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Provides origin and current position
 */
public final class EntityDebugStatus extends EntityStatus {

    public static final int STATUS_ID = 1;

    private final Vector3 worldPosition = new Vector3();
    private final Vector3 screenPosition = new Vector3();

    private final BitmapFont font;

    public EntityDebugStatus(GameEntity entity) {
        super(entity, STATUS_ID);

        this.font = GameManager.asset().getMediumMipMapped();
        this.isPostRender = true;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void postRender(SpriteBatch batch, Camera worldCamera, Camera guiCamera) {
        worldPosition.set(worldCamera.project(worldPosition.set(entity.getPosition().x + 0.25f, entity.getPosition().y + 2.5f, 0.0f)));
        screenPosition.set(guiCamera.project(worldPosition));

        final String position = Math.round(entity.getPosition().x) + ", " + Math.round(entity.getPosition().y);
        font.draw(batch, position, screenPosition.x, screenPosition.y);
    }
}
