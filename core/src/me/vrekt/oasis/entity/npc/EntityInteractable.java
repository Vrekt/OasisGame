package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.server.game.utilities.Disposable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.dialog.EntityDialog;
import me.vrekt.oasis.entity.dialog.EntityDialogSection;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.quest.type.QuestType;
import me.vrekt.oasis.utilities.render.Viewable;
import me.vrekt.oasis.world.AbstractWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable implements Disposable, Viewable {

    protected final int uniqueId;

    protected final Vector2 position = new Vector2();
    private final Vector3 view = new Vector3();
    protected final AbstractWorld worldIn;
    protected final OasisGame game;
    protected final String name;

    // the quest this NPC could be related to.
    protected QuestType questRelatedTo;

    protected EntityDialog dialog;
    protected EntityDialogSection dialogSection;

    protected final Map<Rotation, String> rotations = new HashMap<>();

    // texture for this NPC.
    protected TextureRegion entityTexture;
    protected float width, height;
    protected boolean speakingTo, speakable;
    protected String speakingDialogName;

    // required player rotation to talk to this npc
    protected Rotation speakingRotation;

    // display face
    protected TextureRegion display;
    protected boolean drawDialogAnimationTile, inView;
    protected float distance = 100f;

    protected final QuestManager questManager;

    public EntityInteractable(String name, float x, float y, OasisGame game, AbstractWorld worldIn) {
        this.position.set(x, y);
        this.name = name;
        this.game = game;
        this.worldIn = worldIn;
        this.uniqueId = ThreadLocalRandom.current().nextInt(1567, 28141);
        this.questManager = game.questManager;
    }

    public String getName() {
        return name;
    }

    public int getUniqueId() {
        return uniqueId;
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

        // set rotation to face the player.
        if (speakingTo) {
            this.entityTexture = game.asset.getAssets().findRegion(rotations.get(getOppositeRotation(game.thePlayer.getRotation())));
            this.width = entityTexture.getRegionWidth();
            this.height = entityTexture.getRegionHeight();
        }
    }

    /**
     * @return entity position
     */
    public Vector2 getPosition() {
        return position;
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

    /**
     * @return dst2 to the player
     */
    public float getDistance(Player player) {
        return distance = player.getPosition().dst2(position);
    }

    public float getDistance() {
        return distance;
    }

    /**
     * @return if this entity is in view
     */
    public boolean isInView() {
        return inView;
    }

    /**
     * Get the width of this entity
     *
     * @param scale the world scale
     * @return the width
     */
    public float getWidth(float scale) {
        return entityTexture.getRegionWidth() * scale;
    }

    /**
     * Get the height of this entity
     *
     * @param scale the world scale
     * @return the width
     */
    public float getHeight(float scale) {
        return entityTexture.getRegionHeight() * scale;
    }

    /**
     * Advance to the next dialog
     *
     * @param option the option
     * @return {@code true} if dialog is complete.
     */
    public abstract boolean nextOrEnd(String option);

    /**
     * Update this NPC
     *
     * @param player player
     * @param delta  delta time
     */
    public void update(Player player, float delta) {
        this.distance = player.getPosition().dst2(position.x, position.y);
    }

    /**
     * Load this NPC
     *
     * @param asset assets
     */
    public abstract void load(Asset asset);

    /**
     * Render this entity
     *
     * @param batch batch
     * @param scale world scale
     */
    public void render(SpriteBatch batch, float scale) {
        if (entityTexture != null)
            batch.draw(entityTexture, position.x, position.y, width * scale, height * scale);
    }

    @Override
    public boolean isInView(Camera camera) {
        inView = isInViewExtended(camera.frustum);
        return inView;
    }

    public boolean isInViewExtended(Frustum frustum) {
        view.set(position.x, position.y, 0.0f);
        for (int i = 0; i < frustum.planes.length; i++) {
            Plane.PlaneSide result = testExtendedPoint(frustum.planes[i].normal, view, frustum.planes[i].d);
            if (result == Plane.PlaneSide.Back) return false;
        }
        return true;
    }

    public Plane.PlaneSide testExtendedPoint(Vector3 normal, Vector3 point, float d) {
        float dist = normal.dot(point) + d;

        if (dist > -10 && dist <= 0)
            return Plane.PlaneSide.OnPlane;
        else if (dist < 0)
            return Plane.PlaneSide.Back;
        else
            return Plane.PlaneSide.Front;
    }

    /**
     * Get opposite rotation for interacting entities
     *
     * @param rotation current rotation
     * @return rotation
     */
    public Rotation getOppositeRotation(Rotation rotation) {
        switch (rotation) {
            case FACING_UP:
                return Rotation.FACING_DOWN;
            case FACING_DOWN:
                return Rotation.FACING_UP;
            case FACING_RIGHT:
                return Rotation.FACING_LEFT;
            default:
                return Rotation.FACING_RIGHT;
        }
    }

    @Override
    public void dispose() {
        entityTexture = null;
        this.dialog.dispose();
        this.dialog = null;
        this.dialogSection = null;
    }
}
