package me.vrekt.oasis.entity.component.status;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Handles drawing the dialog dots above the entities head
 */
public final class EntitySpeakableStatus extends EntityStatus {

    private static final float ANIMATION_DISTANCE = 40f;
    private static final float SPEAKING_DISTANCE = 5.5f;

    // TODO: Later maybe store these, I don't know if its worth it yet.
    private final TextureRegion[] dialogFrames;
    private boolean drawDialogFrames;

    public EntitySpeakableStatus(GameEntity entity, Asset asset) {
        super(entity);

        dialogFrames = new TextureRegion[3];
        dialogFrames[0] = asset.get("dialog", 1);
        dialogFrames[1] = asset.get("dialog", 2);
        dialogFrames[2] = asset.get("dialog", 3);
    }

    @Override
    public void update(float delta) {
        // speakable
        entity.asInteractable().setSpeakable(entity.getDistanceFromPlayer() <= SPEAKING_DISTANCE);
        // rendering
        drawDialogFrames = entity.getDistanceFromPlayer() <= ANIMATION_DISTANCE;
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (drawDialogFrames) {
            renderCurrentDialogFrame(batch, dialogFrames[getCurrentDialogFrame() - 1]);
        }
    }

    /**
     * Render the dialog tile animation
     *
     * @param batch  batch
     * @param region region
     */
    private void renderCurrentDialogFrame(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, entity.getX() + 0.15f, entity.getY() + entity.getScaledHeight() + 0.1f, region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);
    }

    /**
     * @return active dialog frame
     */
    public int getCurrentDialogFrame() {
        return entity.getDialogComponent().currentDialogFrame;
    }

}
