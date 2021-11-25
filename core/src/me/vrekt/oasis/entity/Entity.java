package me.vrekt.oasis.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.entity.drawing.Rotation;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.utilities.render.Viewable;
import me.vrekt.oasis.world.AbstractWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Entity implements Disposable, Viewable {

    protected final int entityId;

    protected final Vector2 position = new Vector2();
    protected final Vector3 view = new Vector3();
    protected final AbstractWorld worldIn;
    protected final OasisGame game;
    protected final String name;

    // entity rotations based on direction looking
    protected final Map<Rotation, String> rotations = new HashMap<>();

    // texture(s) for this entity
    protected TextureRegion entityTexture, displayTexture;
    protected float width, height;
    protected float distance = 100f;
    protected boolean inView;

    // if this entity is within update distance
    protected boolean isMoving, withinDistance;
    protected float health;

    // the effect this entity could express
    protected ParticleEffect effect;

    public Entity(String name, float x, float y, OasisGame game, AbstractWorld worldIn) {
        this.position.set(x, y);
        this.name = name;
        this.game = game;
        this.worldIn = worldIn;
        this.entityId = ThreadLocalRandom.current().nextInt(1567, 28141);
    }

    public String getName() {
        return name;
    }

    public int getEntityId() {
        return entityId;
    }

    public TextureRegion getDisplay() {
        return displayTexture;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setWithinDistance(boolean withinDistance) {
        this.withinDistance = withinDistance;
    }

    public boolean isWithinDistance() {
        return withinDistance;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
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
     * Update this entity
     *
     * @param player player
     * @param delta  delta time
     */
    public void update(Player player, float delta) {

    }

    public void updateWithDistance(Player player, float delta) {
        getDistance(player);
        this.update(player, delta);
    }

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

    /**
     * Load this entity
     *
     * @param asset assets
     */
    public abstract void loadEntity(Asset asset);

    @Override
    public void dispose() {
        entityTexture = null;
    }
}
