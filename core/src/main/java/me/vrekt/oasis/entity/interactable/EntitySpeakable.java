package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.dialog.InteractableEntityDialog;
import me.vrekt.oasis.entity.dialog.InteractableEntityDialogSection;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.GuiType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an entity that can be spoken to.
 */
public abstract class EntitySpeakable extends Entity {

    protected OasisPlayer player;

    protected InteractableEntityDialog entityDialog;
    protected InteractableEntityDialogSection dialog;

    protected boolean speakingTo, speakable;
    protected final TextureRegion[] dialogFrames = new TextureRegion[3];
    protected float speakableDistance = 6f, dialogAnimationRange = 50f;
    protected final Vector2 interactionPoint = new Vector2();

    protected final Map<String, Runnable> dialogActions = new HashMap<>();

    public EntitySpeakable(OasisPlayer player) {
        super(true);
        this.player = player;

        entity.add(new EntityDialogComponent());
    }

    public abstract TextureRegion getDialogFace();

    @Override
    public void update(float v) {
        this.speakable = getDistanceFromPlayer() <= speakableDistance;
        entity.getComponent(EntityDialogComponent.class).isInView = inView;

        // update dialog animation state
        if (getDistanceFromPlayer() <= dialogAnimationRange) {
            setDrawDialogAnimationTile(true);
        } else if (getDistanceFromPlayer() > dialogAnimationRange && drawDialogAnimationTile()) {
            setDrawDialogAnimationTile(false);
        }

        // stop speaking to this entity if the player moves away
        if (speakingTo) {
            if (player.getPosition().dst(interactionPoint) >= 0.1f) {
                GameManager.getGuiManager().hideGui(GuiType.DIALOG);
                speakingTo = false;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (drawDialogAnimationTile()) renderCurrentDialogFrame(batch, dialogFrames[getCurrentDialogFrame() - 1]);
    }

    /**
     * Render the dialog tile animation
     *
     * @param batch  batch
     * @param region region
     */
    protected void renderCurrentDialogFrame(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, getX() + 0.2f, getY() + getScaledHeight() + 0.1f, getScaledWidth(), getScaledHeight());
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

        if (speakingTo) {
            // TODO: Face the player when speaking to this entity
            player.setIdleRegionState();
            player.setSpeakingToEntity(true);
            player.setEntitySpeakingTo(this);
            interactionPoint.set(player.getPosition());
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
