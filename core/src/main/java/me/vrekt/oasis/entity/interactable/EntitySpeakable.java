package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.dialog.Dialogue;
import me.vrekt.oasis.entity.dialog.DialogueEntry;
import me.vrekt.oasis.entity.dialog.utility.DialogueResult;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.GuiType;

/**
 * Represents an entity that can be spoken to.
 */
public abstract class EntitySpeakable extends Entity {

    protected OasisPlayer player;

    protected Dialogue dialogue;
    protected DialogueEntry activeEntry;

    protected boolean speakingTo, speakable;
    protected final TextureRegion[] dialogFrames = new TextureRegion[3];
    protected float speakableDistance = 6f, dialogAnimationRange = 50f;
    protected final Vector2 interactionPoint = new Vector2();

    protected float lastDialogUpdate;

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
