package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.ai.utility.AiVectorUtility;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.status.EntitySpeakableStatus;
import me.vrekt.oasis.entity.dialog.Dialogue;
import me.vrekt.oasis.entity.dialog.DialogueEntry;
import me.vrekt.oasis.entity.dialog.utility.DialogueResult;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiType;

/**
 * Represents an entity that can be spoken to.
 */
public abstract class EntitySpeakable extends GameEntity {

    protected Dialogue dialogue;
    protected DialogueEntry activeEntry;

    protected boolean speakingTo, speakable;
    protected final Vector2 interactionPoint = new Vector2();

    private boolean showingSpeaking;

    public EntitySpeakable(PlayerSP player) {
        this.player = player;
    }

    /**
     * @return dialogue
     */
    public Dialogue dialogue() {
        return dialogue;
    }

    /**
     * Only to be used for game saving
     *
     * @param entry entry
     */
    public void setActiveEntry(DialogueEntry entry) {
        this.activeEntry = entry;
    }

    public abstract TextureRegion getDialogFace();

    @Override
    public void load(Asset asset) {
        addStatus(new EntitySpeakableStatus(this, asset));
    }

    @Override
    public void update(float v) {
        super.update(v);

        if (getDistanceFromPlayer() <= 4.0f) {
            if (!showingSpeaking) {
                showingSpeaking = true;
                //  getWorldState().interactionManager().showSpeakingInteraction(this);
            }
        } else {
            if (showingSpeaking) {
                showingSpeaking = false;
                //  getWorldState().interactionManager().hideInteractions();
            }
        }


        // stop speaking to this entity if the player moves away
        if (speakingTo) {
            if (player.getPosition().dst(interactionPoint) >= 0.1f) {
                GameManager.gui().hideGui(GuiType.DIALOG);
                GameManager.gui().resetCursor();
                speakingTo = false;

                stoppedSpeaking();
            }
        }
    }

    /**
     * When the player stops speaking to this entity
     */
    protected void stoppedSpeaking() {

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

    public void setSpeakable(boolean speakable) {
        this.speakable = speakable;
    }

    public boolean isSpeakingTo() {
        return speakingTo;
    }

    public void speak() {
        if (!speakingTo && speakable) {
            speak(true);
        }
    }

    /**
     * Set speaking to this entity
     *
     * @param speakingTo state
     */
    public void speak(boolean speakingTo) {
        if (!speakingTo && this.speakingTo) {
            stoppedSpeaking();
        }

        if (speakingTo) {
            worldIn.getGame().guiManager.showGui(GuiType.DIALOG, true);
            worldIn.getGame().guiManager.getDialogComponent().showEntityDialog(this);
            player.setRotation(AiVectorUtility.faceEntity(this, player));
        }

        this.speakingTo = speakingTo;
        player.speak(this, speakingTo);
        interactionPoint.set(player.getPosition());
    }

    public void endSpeak() {
        this.speakingTo = false;
        stoppedSpeaking();
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
    }
}
