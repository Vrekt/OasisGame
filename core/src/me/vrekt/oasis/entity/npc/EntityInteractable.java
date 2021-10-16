package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.server.game.utilities.Disposable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.dialog.entity.EntityDialog;
import me.vrekt.oasis.dialog.entity.EntityDialogSection;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.quest.type.QuestType;
import me.vrekt.oasis.utilities.render.Viewable;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable implements Disposable, Viewable {

    protected final Vector2 position = new Vector2();
    protected final AbstractWorld worldIn;
    protected final OasisGame game;
    protected final String name;

    // the quest this NPC could be related to.
    protected QuestType questRelatedTo;

    protected EntityDialog dialog;
    protected EntityDialogSection dialogSection;

    // texture for this NPC.
    protected TextureRegion entityTexture;
    protected float width, height;
    protected boolean speakingTo, speakable;
    // required player rotation to talk to this npc
    protected Rotation speakingRotation;

    // display face
    protected TextureRegion display;

    public EntityInteractable(String name, float x, float y, OasisGame game, AbstractWorld worldIn) {
        this.position.set(x, y);
        this.name = name;
        this.game = game;
        this.worldIn = worldIn;
    }

    public String getName() {
        return name;
    }

    public Rotation getSpeakingRotation() {
        return speakingRotation;
    }

    public EntityDialogSection getDialogSection() {
        return dialogSection;
    }

    public TextureRegion getDisplay() {
        return display;
    }

    public boolean isSpeakable() {
        return speakable;
    }

    public boolean isSpeakingTo() {
        return speakingTo;
    }

    public void setSpeakingTo(boolean speakingTo) {
        this.speakingTo = speakingTo;
    }

    /**
     * Advance to the next dialog
     *
     * @param option the option
     */
    public abstract void nextDialog(String option);

    /**
     * Update this NPC
     *
     * @param player player
     * @param delta  delta time
     */
    public abstract void update(Player player, float delta);

    /**
     * Load this NPC
     *
     * @param asset assets
     */
    public abstract void load(Asset asset);

    public float dst2(Player player) {
        return player.getPosition().dst2(position.x, position.y);
    }

    public void render(SpriteBatch batch, float scale) {
        if (entityTexture != null)
            batch.draw(entityTexture, position.x, position.y, width * scale, height * scale);
    }

    @Override
    public boolean isInView(Camera camera) {
        return camera.frustum.pointInFrustum(position.x, position.y, 0.0f);
    }

    @Override
    public void dispose() {
        entityTexture = null;
        this.dialog.dispose();
        this.dialog = null;
        this.dialogSection = null;
    }
}
