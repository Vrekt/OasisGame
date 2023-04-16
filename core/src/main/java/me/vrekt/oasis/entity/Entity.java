package me.vrekt.oasis.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import lunar.shared.entity.LunarEntity;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;

/**
 * Represents a basic entity within Oasis
 */
public abstract class Entity extends LunarEntity implements Viewable, Drawable {

    // describes the view/renderable stuff
    protected boolean withinUpdateDistance, isNearby;

    protected float health = 100.0f;

    public Entity(com.badlogic.ashley.core.Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public Entity(boolean initializeComponents) {
        super(initializeComponents);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    /**
     * Render the health bar of this entity
     * Usually only used for enemies.
     *
     * @param batch    the batch
     * @param gradient the gradient
     */
    public void renderHealthBar(SpriteBatch batch, NinePatch gradient) {
        if (health <= 0) return;

        final float width = (health / 100.0f * getWidth()) * OasisGameSettings.SCALE;
        gradient.draw(batch, getX(), getY() + (getHeightScaled() + 0.1f), width, 3.0f * OasisGameSettings.SCALE);
    }

    /**
     * Check if this entity was clicked on
     *
     * @param clicked the vector3 click
     * @return {@code  true} if so
     */
    public boolean isMouseInEntityBounds(Vector3 clicked) {
        return clicked.x > getX() && clicked.x < (getX() + getWidthScaled()) && clicked.y > getY() && clicked.y < (getY() + getHeightScaled());
    }

    public boolean isInteractable() {
        return false;
    }

    public EntityInteractable asInteractable() {
        return null;
    }

    public boolean isWithinUpdateDistance() {
        return withinUpdateDistance;
    }

    public void setWithinUpdateDistance(boolean withinUpdateDistance) {
        this.withinUpdateDistance = withinUpdateDistance;
    }

    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }

    public void setDistanceToPlayer(float distance) {
        entity.getComponent(EntityDialogComponent.class).distanceFromPlayer = distance;
    }

    public float getDistanceFromPlayer() {
        return entity.getComponent(EntityDialogComponent.class).distanceFromPlayer;
    }

    @Override
    public boolean isInView(Camera camera) {
        return camera.frustum.pointInFrustum(getX(), getY(), 0.0f);
    }
}
