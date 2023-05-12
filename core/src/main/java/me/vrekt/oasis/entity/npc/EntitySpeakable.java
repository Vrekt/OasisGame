package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lunar.shared.entity.texture.Rotation;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.dialog.InteractableEntityDialog;
import me.vrekt.oasis.entity.dialog.InteractableEntityDialogSection;
import me.vrekt.oasis.entity.npc.animation.EntityTextured;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.gui.GuiType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an entity that can be spoken to.
 */
public abstract class EntitySpeakable extends EntityTextured {

    protected OasisPlayerSP player;

    protected InteractableEntityDialog entityDialog;
    protected InteractableEntityDialogSection dialog;

    protected String dialogFaceAsset;
    protected boolean speakingTo, speakable;
    protected final TextureRegion[] dialogFrames = new TextureRegion[3];
    protected float speakableDistance = 6f, dialogAnimationRange = 50f;
    protected final Vector2 interaction = new Vector2();

    protected final Map<String, Runnable> dialogActions = new HashMap<>();

    public EntitySpeakable(OasisPlayerSP player) {
        super(true);
        this.player = player;
        entity.add(new EntityDialogComponent());
    }

    @Override
    public void update(float v) {
        this.speakable = getDistanceFromPlayer() <= speakableDistance;

        // update dialog animation state
        if (getDistanceFromPlayer() <= dialogAnimationRange) {
            setDrawDialogAnimationTile(true);
        } else if (getDistanceFromPlayer() > dialogAnimationRange && drawDialogAnimationTile()) {
            setDrawDialogAnimationTile(false);
        }

        // update dialog state
        if (speakingTo) {
            // player moved, cancel
            if (player.getPosition().dst(interaction) >= 0.1f) {
                GameManager.getGui().hideGui(GuiType.DIALOG);
                this.speakingTo = false;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (drawDialogAnimationTile()) {
            renderCurrentDialogFrame(batch, dialogFrames[getCurrentDialogFrame() - 1]);
        }
    }

    /**
     * Render the dialog tile animation
     *
     * @param batch  batch
     * @param region region
     */
    protected void renderCurrentDialogFrame(SpriteBatch batch, TextureRegion region) {
        if (region == null) return;
        batch.draw(region, getX() + 0.2f, getY() + getHeightScaled() + 0.1f,
                region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);
    }

    public int getCurrentDialogFrame() {
        return entity.getComponent(EntityDialogComponent.class).currentDialogFrame;
    }

    public void setDrawDialogAnimationTile(boolean drawDialogAnimationTile) {
        entity.getComponent(EntityDialogComponent.class).drawDialogAnimationTile = drawDialogAnimationTile;
    }

    /**
     * @return if the floating dialog animation tile should be drawn.
     */
    public boolean drawDialogAnimationTile() {
        return entity.getComponent(EntityDialogComponent.class).drawDialogAnimationTile;
    }

    public TextureRegion getDialogFace() {
        return getRegion(dialogFaceAsset);
    }

    public InteractableEntityDialogSection getDialog() {
        return dialog;
    }

    public boolean isSpeakable() {
        return speakable;
    }

    public boolean isSpeakingTo() {
        return speakingTo;
    }

    public void setSpeakingTo(boolean speakingTo) {
        this.speakingTo = speakingTo;

        // set rotation to face the player.
        // TODO: Fix this
        if (speakingTo) {
            player.setRotation(Rotation.getOppositeRotation(rotation).ordinal());
            player.setIdleRegionState();
            player.setSpeakingToEntity(true);
            player.setEntitySpeakingTo(this);
            interaction.set(player.getPosition());
        } else {
            player.setEntitySpeakingTo(null);
            player.setSpeakingToEntity(false);
        }
    }

    /**
     * Advance to the next dialog
     *
     * @param option the option
     * @return {@code true} if dialog is complete.
     */
    public boolean advanceDialogStage(String option) {
        return false;
    }

    public boolean advanceDialogStage() {
        return false;
    }

    /**
     * Add a dialog option
     *
     * @param key    the key
     * @param action the action to run
     */
    protected void addDialogAction(String key, Runnable action) {
        this.dialogActions.put(key, action);
    }

    /**
     * Execute a dialog action
     *
     * @param key the key entry
     * @return {@code  true} if executed
     */
    protected boolean executeDialogAction(String key) {
        if (dialogActions.containsKey(key)) {
            dialogActions.get(key).run();
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        this.entityDialog.dispose();
        dialogFrames[0] = null;
        dialogFrames[1] = null;
        dialogFrames[2] = null;
        this.entityDialog = null;
        this.dialog = null;
    }
}
