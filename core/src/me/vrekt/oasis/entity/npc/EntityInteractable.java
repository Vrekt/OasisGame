package me.vrekt.oasis.entity.npc;

import gdx.lunar.server.game.utilities.Disposable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.dialog.EntityDialog;
import me.vrekt.oasis.entity.dialog.EntityDialogSection;
import me.vrekt.oasis.entity.render.EntityAnimationRenderer;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable extends Entity implements Disposable {

    protected EntityDialog dialog;
    protected EntityDialogSection dialogSection;

    protected boolean speakingTo, speakable;
    protected boolean drawDialogAnimationTile;

    protected final QuestManager questManager;
    protected EntityAnimationRenderer renderer;

    protected EntityNPCType type;

    public EntityInteractable(String name, float x, float y, OasisGame game, AbstractWorld worldIn) {
        super(name, x, y, game, worldIn);
        this.questManager = game.getQuestManager();
    }

    public EntityDialogSection getDialogSection() {
        return dialogSection;
    }

    public EntityNPCType getType() {
        return type;
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
        if (speakingTo) {
            this.entityTexture = game.getAsset().getAssets().findRegion(rotations.get(getOppositeRotation(game.getPlayer().getRotation())));
            this.width = entityTexture.getRegionWidth();
            this.height = entityTexture.getRegionHeight();
        }
    }

    public void setDrawDialogAnimationTile(boolean drawDialogAnimationTile) {
        this.drawDialogAnimationTile = drawDialogAnimationTile;
    }

    /**
     * @return if the floating dialog animation tile should be drawn.
     */
    public boolean doDrawDialogAnimationTile() {
        return drawDialogAnimationTile;
    }

    public void invalidate() {
        setDrawDialogAnimationTile(false);
        setSpeakingTo(false);
        setWithinDistance(false);
    }

    /**
     * Advance to the next dialog
     *
     * @param option the option
     * @return {@code true} if dialog is complete.
     */
    public abstract boolean nextOrEnd(String option);

    @Override
    public void dispose() {
        super.dispose();
        this.dialog.dispose();
        this.dialog = null;
        this.dialogSection = null;
    }
}
