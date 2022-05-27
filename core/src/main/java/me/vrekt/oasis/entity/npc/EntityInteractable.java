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
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.dialog.EntityDialog;
import me.vrekt.oasis.entity.dialog.EntityDialogSection;
import me.vrekt.oasis.entity.npc.animation.EntityTextured;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.graphics.Renderable;
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

    public EntityInteractable(String name, Vector2 position, OasisPlayerSP player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(true);
        setPosition(position.x, position.y, false);
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
        this.speakable = getDistanceFromPlayer() <= 6f;
        setDistanceToPlayer(player.getPosition().dst2(getPosition()));
        setInView(this.inView);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (currentRegionState != null) {
            batch.draw(currentRegionState, getX(), getY(), getWidth() * getScaling(), getHeight() * getScaling());
        } else if (currentTextureState != null) {
            batch.draw(currentTextureState, getX(), getY(), getWidth() * getScaling(), getHeight() * getScaling());
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
        }
    }

    /**
     * Face the player
     * Must be implemented by default
     */
    public void facePlayer() {

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

    public void invalidateDialogState() {
        setDrawDialogAnimationTile(false);
        setSpeakingTo(false);
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
        //  removeEntityInWorld(getWorldIn());
        //   lunarWorld.spawnEntityInWorld(this);
        //   getInstance().setWorldIn(lunarWorld);
        throw new UnsupportedOperationException();
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> lunarWorld) {
        //  removeEntityInWorld(getWorldIn());
        //  lunarWorld.spawnEntityInWorld(this);
        //   getInstance().setWorldIn(lunarWorld);
        throw new UnsupportedOperationException();
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void removeEntityInWorld(LunarWorld<P, N, E> lunarWorld) {
        //    getInstance().worldIn.removeEntityInWorld(this);
        throw new UnsupportedOperationException();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (currentTextureState != null) currentTextureState.dispose();
        this.entityDialog.dispose();
        this.entityDialog = null;
        this.dialog = null;
    }
}
