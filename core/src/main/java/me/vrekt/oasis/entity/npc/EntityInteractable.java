package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.drawing.Rotation;
import lunar.shared.entity.player.LunarEntity;
import lunar.shared.entity.player.LunarEntityPlayer;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.dialog.EntityDialog;
import me.vrekt.oasis.entity.dialog.EntityDialogSection;
import me.vrekt.oasis.entity.npc.animation.EntityTextured;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.graphics.Renderable;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable extends EntityTextured implements ResourceLoader, Renderable {

    // describes the view/renderable stuff
    protected final Vector3 view = new Vector3(0, 0, 0);
    protected boolean inView;

    protected final OasisPlayerSP player;
    protected final OasisGame game;

    protected EntityDialog entityDialog;
    protected EntityDialogSection dialog;

    protected boolean speakingTo, speakable;
    protected EntityNPCType type;

    protected final TextureRegion[] dialogFrames = new TextureRegion[3];
    protected float speakableDistance = 6f, dialogAnimationRange = 50f;
    protected final Vector2 interaction = new Vector2();

    public EntityInteractable(String name, Vector2 position, OasisPlayerSP player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(true);
        setPosition(position.x, position.y, true);
        setEntityName(name);
        getInstance().worldIn = worldIn;
        this.player = player;
        this.game = game;
        this.type = type;
        this.rotation = Rotation.FACING_DOWN.ordinal();
    }

    public EntityNPCType getType() {
        return type;
    }

    @Override
    public void update(float v) {
        this.speakable = getDistanceFromPlayer() <= speakableDistance;
        setInView(this.inView);

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
                game.getGui().hideGui(GuiType.DIALOG);
                this.speakingTo = false;
            }
        }

    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        // draw generic texture
        if (currentRegionState != null) {
            batch.draw(currentRegionState, getX(), getY(), getWidth() * getScaling(), getHeight() * getScaling());
        } else if (currentTextureState != null) {
            batch.draw(currentTextureState, getX(), getY(), getWidth() * getScaling(), getHeight() * getScaling());
        }

        // draw animation
        if (drawDialogAnimationTile()) {
            renderCurrentDialogFrame(batch, dialogFrames[getCurrentDialogFrame() - 1]);
        }
    }

    public EntityDialogSection getDialog() {
        return dialog;
    }

    public TextureRegion getDialogFace() {
        return getRegion("face");
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
            player.setRotation(Rotation.getOppositeRotation(rotation).ordinal());
            player.setIdleRegionState();
            interaction.set(player.getPosition());
        }
    }

    /**
     * Check if this entity was clicked on
     *
     * @param clicked the vector3 click
     * @return {@code  true} if so
     */
    public boolean clickedOn(Vector3 clicked) {
        return clicked.x > getX() && clicked.x < (getX() + getWidthScaled()) && clicked.y > getY() && clicked.y < (getY() + getHeightScaled());
    }

    public int getCurrentDialogFrame() {
        return entity.getComponent(EntityDialogComponent.class).currentDialogFrame;
    }

    public void setDrawDialogAnimationTile(boolean drawDialogAnimationTile) {
        entity.getComponent(EntityDialogComponent.class).drawDialogAnimationTile = drawDialogAnimationTile;
    }

    public void setInView(boolean inView) {
        entity.getComponent(EntityDialogComponent.class).isInView = inView;
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public EntityInteractable asInteractable() {
        return this;
    }

    /**
     * @return if the floating dialog animation tile should be drawn.
     */
    public boolean drawDialogAnimationTile() {
        return entity.getComponent(EntityDialogComponent.class).drawDialogAnimationTile;
    }

    /**
     * Render the dialog tile animation
     *
     * @param batch  batch
     * @param region region
     */
    protected void renderCurrentDialogFrame(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, getX() + 0.2f, getY() + getHeightScaled() + 0.1f,
                region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);
    }

    /**
     * Advance to the next dialog
     *
     * @param option the option
     * @return {@code true} if dialog is complete.
     */
    public abstract boolean advanceDialogStage(String option);

    @Override
    public boolean isInView(Camera camera) {
        inView = Renderable.isInViewExtended(view, getX(), getY(), camera.frustum);
        return inView;
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> lunarWorld, float v, float v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> lunarWorld) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void removeEntityInWorld(LunarWorld<P, N, E> lunarWorld) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (currentTextureState != null) currentTextureState.dispose();
        this.entityDialog.dispose();
        dialogFrames[0] = null;
        dialogFrames[1] = null;
        dialogFrames[2] = null;
        this.entityDialog = null;
        this.dialog = null;
    }
}
