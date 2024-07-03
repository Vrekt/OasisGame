package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.ai.utility.AiVectorUtility;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.component.status.EntitySpeakableStatus;
import me.vrekt.oasis.entity.dialog.Dialogue;
import me.vrekt.oasis.entity.dialog.DialogueEntry;
import me.vrekt.oasis.entity.dialog.utility.DialogueResult;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.cursor.Cursor;

/**
 * Represents an entity that can be spoken to.
 */
public abstract class EntitySpeakable extends GameEntity {

    protected Dialogue dialogue;
    protected DialogueEntry activeEntry;

    protected boolean speakingTo, speakable;
    protected final Vector2 interactionPoint = new Vector2();

    public EntitySpeakable(PlayerSP player) {
        this.player = player;

        entity.add(new EntityDialogComponent());
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
    public Cursor enter(Vector3 mouse) {
        return Cursor.DIALOG;
    }

    @Override
    public boolean clicked(Vector3 mouse) {
        if (!speakingTo && speakable) {
            speak(true);

            worldIn.getGame().guiManager.showGui(GuiType.DIALOG, true);
            worldIn.getGame().guiManager.getDialogComponent().showEntityDialog(this);
            player.setRotation(AiVectorUtility.faceEntity(this, player));
        }
        return true;
    }

    @Override
    public void load(Asset asset) {
        setStatus(new EntitySpeakableStatus(this, asset));
    }

    @Override
    public void update(float v) {
        super.update(v);

        if (status != null) status.update(v);

        entity.getComponent(EntityDialogComponent.class).isInView = inView;

        // stop speaking to this entity if the player moves away
        if (speakingTo) {
            if (player.getPosition().dst(interactionPoint) >= 0.1f) {
                GameManager.getGuiManager().hideGui(GuiType.DIALOG);
                GameManager.getGuiManager().resetCursor();
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

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (status != null) status.render(batch, delta);
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

    /**
     * @return {@code true} if this entity can be spoken to, (mainly distance related)
     */
    public boolean isSpeakable() {
        return speakable;
    }

    public void setSpeakable(boolean speakable) {
        this.speakable = speakable;
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
        if (!speakingTo && this.speakingTo) {
            stoppedSpeaking();
        }

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
    }
}
