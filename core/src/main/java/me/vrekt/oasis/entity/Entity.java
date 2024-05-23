package me.vrekt.oasis.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import lunar.shared.components.drawing.EntityTextureComponent;
import lunar.shared.entity.AbstractLunarEntity;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;
import me.vrekt.oasis.utility.ResourceLoader;
import me.vrekt.oasis.world.GameWorld;

/**
 * Represents a basic entity within Oasis
 */
public abstract class Entity extends AbstractLunarEntity implements Viewable, Drawable, ResourceLoader {

    protected PlayerSP player;
    protected boolean isNearby, inView;
    protected NinePatch gradient;
    protected boolean dynamicSize;
    protected Rectangle bb;

    // the active texture this entity is using to draw itself
    protected TextureRegion activeEntityTexture;
    protected EntityRotation rotation = EntityRotation.DOWN, previousRotation = EntityRotation.LEFT;

    // the world this entity is in
    protected GameWorld worldIn;
    // if entity is in interior
    protected GameWorld parentWorld;

    public Entity(boolean initializeComponents) {
        super(initializeComponents);

        entity.add(new EntityTextureComponent());
        setHealth(100.0f);
    }

    public Rectangle bb() {
        return bb;
    }

    protected void createBB(float w, float h) {
        bb = new Rectangle(getX(), getY(), w * OasisGameSettings.SCALE, h * OasisGameSettings.SCALE);
        setSize(w, h, OasisGameSettings.SCALE);
    }

    /**
     * Add a texture part
     *
     * @param rotation rotation key
     * @param texture  the texture
     */
    protected TextureRegion addTexturePart(EntityRotation rotation, TextureRegion texture, boolean initial) {
        entity.getComponent(EntityTextureComponent.class).textureRegions.put(rotation.name(), texture);
        if (initial) activeEntityTexture = texture;
        return texture;
    }

    /**
     * Add a texture part
     *
     * @param name    the name
     * @param texture the texture
     */
    protected TextureRegion addTexturePart(String name, TextureRegion texture) {
        entity.getComponent(EntityTextureComponent.class).textureRegions.put(name, texture);
        return texture;
    }

    /**
     * Get a texture part
     *
     * @param name the name of the texture
     * @return the texture
     */
    protected TextureRegion getTexturePart(String name) {
        return entity.getComponent(EntityTextureComponent.class).textureRegions.get(name);
    }

    /**
     * Get a texture part
     *
     * @param rotation rotation key
     * @return the texture
     */
    protected TextureRegion getTexturePart(EntityRotation rotation) {
        return entity.getComponent(EntityTextureComponent.class).textureRegions.get(rotation.name());
    }

    /**
     * Render the health bar of this entity
     * Usually only used for enemies.
     *
     * @param batch the batch
     */
    public void renderHealthBar(SpriteBatch batch) {
        if (getHealth() <= 0 || gradient == null) return;

        final float width = (getHealth() / 100.0f * getWidth()) * OasisGameSettings.SCALE;
        gradient.draw(batch, getX(), getY() + (getScaledHeight() + 0.1f), width, 3.0f * OasisGameSettings.SCALE);
    }

    /**
     * Check if this entity was clicked on
     *
     * @param clicked the vector3 click
     * @return {@code  true} if so
     */
    public boolean isMouseInEntityBounds(Vector3 clicked) {
        return clicked.x > getX() && clicked.x < (getX() + getScaledWidth()) && clicked.y > getY() && clicked.y < (getY() + getScaledHeight());
    }

    /**
     * @return {@code true} if this entity is interactable
     */
    public boolean isInteractable() {
        return false;
    }

    /**
     * @return the interactable entity
     */
    public EntityInteractable asInteractable() {
        return null;
    }

    /**
     * @return {@code true} if this entity is nearby
     */
    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }

    public void setDistanceToPlayer(float distance) {
        if (this instanceof EntityInteractable) {
            entity.getComponent(EntityDialogComponent.class).distanceFromPlayer = distance;
        }
    }

    public float getDistanceFromPlayer() {
        return entity.getComponent(EntityDialogComponent.class).distanceFromPlayer;
    }

    public EntityRotation getRotation() {
        return rotation;
    }

    public GameWorld getWorldState() {
        throw new UnsupportedOperationException("Implement this ha");
    }

    public GameWorld getWorldIn() {
        return worldIn;
    }

    /**
     * Damage this entity
     *
     * @param tick       the current world tick
     * @param amount     the amount of damage
     * @param knockback  the knockback multiplier
     * @param isCritical if this damage was a critical
     */
    public void damage(float tick, float amount, float knockback, boolean isCritical) {

    }

    /**
     * Draw the current position
     * If dynamic sizing is true, will automatically resize the bb.
     *
     * @param batch  batch
     * @param region texture
     */
    protected void drawCurrentPosition(SpriteBatch batch, TextureRegion region) {
        if (dynamicSize && previousRotation != rotation) {
            setSize(region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE, OasisGameSettings.SCALE);
        }

        batch.draw(region, body.getPosition().x, body.getPosition().y, region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);
    }

    public void createBoxBody(World world) {
        final BodyDef definition = new BodyDef();
        final FixtureDef fixture = new FixtureDef();

        definition.type = BodyDef.BodyType.DynamicBody;
        definition.fixedRotation = false;
        definition.position.set(getPosition());

        body = world.createBody(definition);
        PolygonShape shape;

        shape = new PolygonShape();
        shape.setAsBox(getScaledWidth() / 2.0F, getScaledHeight() / 2.0F);
        fixture.shape = shape;
        fixture.density = 1.0f;

        body.createFixture(fixture);
        body.setUserData(this);
        shape.dispose();
    }

    @Override
    public boolean isInView(Camera camera) {
        return inView = camera.frustum.pointInFrustum(getX(), getY(), 0.0f);
    }
}
