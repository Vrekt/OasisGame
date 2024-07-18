package me.vrekt.oasis.entity.component.status;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Handles drawing the dialog dots above the entities head
 */
public final class EntitySpeakableStatus extends EntityStatus {

    public static final int STATUS_ID = 0;

    private static final float ANIMATION_DISTANCE = 40f;
    private static final float SPEAKING_DISTANCE = 5.5f;

    // TODO: Later maybe store these, I don't know if its worth it yet.
    private final TextureRegion[] dialogFrames;
    private boolean drawDialogFrames;

    private float offsetX = 0.15f, offsetY = 0.1f;

    private float dialogAnimationTime;
    private int currentDialogFrame = 1;

    public EntitySpeakableStatus(GameEntity entity, Asset asset) {
        super(entity, STATUS_ID);

        dialogFrames = new TextureRegion[3];
        dialogFrames[0] = asset.get("dialog", 1);
        dialogFrames[1] = asset.get("dialog", 2);
        dialogFrames[2] = asset.get("dialog", 3);
    }

    public void offset(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    @Override
    public void update(float delta) {
        // speakable
        entity.asInteractable().setSpeakable(entity.getDistanceFromPlayer() <= SPEAKING_DISTANCE);
        // rendering
        drawDialogFrames = entity.getDistanceFromPlayer() <= ANIMATION_DISTANCE;
        if (dialogAnimationTime == 0.0f) dialogAnimationTime = GameManager.getTick();

        if (GameManager.hasTimeElapsed(dialogAnimationTime, 0.33f)) {
            dialogAnimationTime = GameManager.getTick();
            currentDialogFrame = currentDialogFrame >= 3 ? 1 : currentDialogFrame + 1;
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (drawDialogFrames) {
            renderCurrentDialogFrame(batch, dialogFrames[currentDialogFrame - 1]);
        }
    }

    /**
     * Render the dialog tile animation
     *
     * @param batch  batch
     * @param region region
     */
    private void renderCurrentDialogFrame(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, entity.getX() + offsetX, entity.getY() + entity.getScaledHeight() + offsetY, region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);
    }

}
