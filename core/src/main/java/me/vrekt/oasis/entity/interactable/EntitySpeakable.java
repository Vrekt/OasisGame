package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.dialog.DialogEntry;
import me.vrekt.oasis.entity.dialog.utility.DialogRequirementTest;
import me.vrekt.oasis.entity.dialog.DialogResult;
import me.vrekt.oasis.entity.dialog.EntityDialog;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.GuiType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents an entity that can be spoken to.
 */
public abstract class EntitySpeakable extends Entity {

    protected OasisPlayer player;

    protected EntityDialog dialog;
    protected DialogEntry activeEntry;

    protected boolean speakingTo, speakable;
    protected final TextureRegion[] dialogFrames = new TextureRegion[3];
    protected float speakableDistance = 6f, dialogAnimationRange = 50f;
    protected final Vector2 interactionPoint = new Vector2();

    protected final Map<String, Runnable> dialogActions = new HashMap<>();
    protected final Map<String, Runnable> entryListeners = new HashMap<>();
    protected final Map<String, DialogRequirementTest> updateListeners = new HashMap<>();

    protected float lastDialogUpdate;

    // the active requirement that needs to be completed
    protected boolean hasActiveRequirement;
    protected String activeRequirement;

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

        // update dialog listeners
        if ((worldIn.getCurrentWorldTick() - lastDialogUpdate) >= 3f) {
            updateListeners.values().forEach(DialogRequirementTest::test);
            lastDialogUpdate = worldIn.getCurrentWorldTick();
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

    public DialogEntry getEntry() {
        return activeEntry;
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

        // also check here so if we go back and speak
        // we want to update the waiting state
        // because the entry has to be visited before
        // activating the wait content
        if (entryListeners.containsKey(activeEntry.getKey()))
            entryListeners.get(activeEntry.getKey()).run();

    }

    public DialogResult next(String optionPicked, Consumer<DialogEntry> entry) {
        if (optionPicked != null && !dialog.hasEntryKey(optionPicked)) return DialogResult.FINISHED;

        if (activeEntry.isWaiting()) {
            // entity is waiting for an action to be completed
            return DialogResult.WAIT;
        }

        // TODO DOESNT UPDATE IF JUST PRESS F
        activeEntry = optionPicked == null ? dialog.next() : dialog.getEntry(optionPicked);

        // invoke any valid listeners
        if (entryListeners.containsKey(activeEntry.getKey()))
            entryListeners.get(activeEntry.getKey()).run();

        activeEntry.setVisited();

        //    if (activeEntry.hasRequirement()) {
        ///       hasActiveRequirement = true;
        //       activeRequirement = activeEntry.getRequirement();
        //   } else {
        //        hasActiveRequirement = false;
        //        activeRequirement = null;
        //   }

        if (activeEntry.hasAction()) executeAction(activeEntry.getAction());
        entry.accept(activeEntry);
        return DialogResult.CONTINUED;
    }

    public DialogResult next(Consumer<DialogEntry> entry) {
        return next(null, entry);
    }

    /**
     * Advance the dialog ignoring any safety checks.
     */
    public void nextUnsafe() {
        activeEntry = dialog.next();
    }

    public void addAction(String key, Runnable action) {
        this.dialogActions.put(key, action);
    }

    public void executeAction(String key) {
        if (dialogActions.containsKey(key)) dialogActions.get(key).run();
    }

    /**
     * Add a listener for when a specific dialog stage is reached
     */
    protected void addEntryListener(String key, Runnable listener) {
        entryListeners.put(key, listener);
    }

    protected void addUpdateListener(String key, DialogRequirementTest requirementTest) {
        updateListeners.put(key, requirementTest);
    }

    @Override
    public void dispose() {
        super.dispose();
        dialog.dispose();
        dialog = null;
        activeEntry = null;
        dialogFrames[0] = null;
        dialogFrames[1] = null;
        dialogFrames[2] = null;
    }
}
