package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.dialog.Dialogue;
import me.vrekt.oasis.entity.dialog.DialogueEntry;
import me.vrekt.oasis.entity.dialog.utility.DialogueResult;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiType;

/**
 * Represents an entity that can be spoken to.
 */
public abstract class EntitySpeakable extends Entity {

    protected Dialogue dialogue;
    protected DialogueEntry activeEntry;

    protected boolean speakingTo, speakable;
    protected final TextureRegion[] dialogFrames = new TextureRegion[3];
    protected float speakableDistance = 6f, dialogAnimationRange = 50f;
    protected final Vector2 interactionPoint = new Vector2();

    protected float lastDialogUpdate;

    public EntitySpeakable(PlayerSP player) {
        super(true);
        this.player = player;

        entity.add(new EntityDialogComponent());
    }

    public abstract TextureRegion getDialogFace();

    @Override
    public void load(Asset asset) {
        dialogFrames[0] = asset.get("dialog", 1);
        dialogFrames[1] = asset.get("dialog", 2);
        dialogFrames[2] = asset.get("dialog", 3);
    }

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
        if (drawDialogAnimationTile() && !isSpeakingTo())
            renderCurrentDialogFrame(batch, dialogFrames[getCurrentDialogFrame() - 1]);
    }

    /**
     * Render the dialog tile animation
     *
     * @param batch  batch
     * @param region region
     */
    protected void renderCurrentDialogFrame(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, getX() + 0.15f, getY() + getScaledHeight() + 0.1f, region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);
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

    /**
     * @return the active entry of the dialogue.
     */
    public DialogueEntry getEntry() {
        return activeEntry;
    }

    public boolean advance() {
        return dialogue.advance();
    }

    public boolean isSpeakable() {
        return speakable;
    }

    public boolean isSpeakingTo() {
        return speakingTo;
    }

    /**
     * Set speaking to this entity
     *
     * @param speakingTo state
     */
    public void speak(boolean speakingTo) {
        this.speakingTo = speakingTo;

        player.speak(this, speakingTo);
        interactionPoint.set(player.getPosition());
    }

    /**
     * Get the next entry
     *
     * @param key the key
     * @return the result
     */
    public DialogueResult next(String key) {
        final DialogueResult result = dialogue.getEntry(key);
        if (result.getEntry() != null) this.activeEntry = result.getEntry();
        return result;
    }

    /**
     * Get the next entry
     *
     * @return the result
     */
    public DialogueResult next() {
        final DialogueResult result = dialogue.getEntry(activeEntry.getNextKey());
        if (result.getEntry() != null) this.activeEntry = result.getEntry();
        return result;
    }

    @Override
    public void dispose() {
        super.dispose();
        dialogue.dispose();
        dialogue = null;
        activeEntry = null;
        dialogFrames[0] = null;
        dialogFrames[1] = null;
        dialogFrames[2] = null;
    }
}
