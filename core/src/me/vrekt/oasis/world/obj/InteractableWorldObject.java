package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.utilities.collision.CollisionShapeCreator;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * Represents a single world object that you could probably interact with
 */
public final class InteractableWorldObject {

    private final Rectangle bounds;
    private final Vector2 center = new Vector2();
    private Body body;

    // textures of this object
    private final TextureRegion[] textures;
    // properties
    private final boolean isBreakable, isSolid;

    private final int maxTextureIndex;
    private int textureIndex;
    private boolean render, finished;

    // the entity this object could be related to.
    private final String relatedTo;

    public InteractableWorldObject(MapObject object, Rectangle regionBounds, Asset asset, World world, float scale) {
        this.bounds = regionBounds;
        this.render = true;
        bounds.getCenter(center);

        // load properties
        this.isBreakable = object.getProperties().get("breakable", Boolean.class);
        this.isSolid = object.getProperties().get("solid", Boolean.class);
        this.relatedTo = object.getProperties().get("relatedTo", String.class);

        // load collision info
        if (isSolid) {
            final Shape shape = CollisionShapeCreator.createPolygonShape((RectangleMapObject) object, scale, false);
            this.body = world.createBody(AbstractWorld.STATIC_BODY);
            this.body.createFixture(shape, 1.0f);
            shape.dispose();
        }

        // load texture info
        final int count = object.getProperties().get("count", Integer.class);
        final String texture = object.getProperties().get("texture", String.class);
        textures = new TextureRegion[count];
        maxTextureIndex = count;

        // load all textures for this object
        for (int i = 0; i < count; i++) textures[i] = asset.getAssets().findRegion(texture, i + 1);
    }

    public boolean isSolid() {
        return isSolid;
    }

    public boolean isBreakable() {
        return isBreakable;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isRender() {
        return render;
    }

    public String getRelatedTo() {
        return relatedTo;
    }

    public Body getBody() {
        return body;
    }

    public boolean isNear(Player player) {
        return player.getPosition().dst2(center.x, center.y) <= 8f;
    }

    public void interact() {
        if (isBreakable) {
            textureIndex = Math.min((textureIndex + 1), maxTextureIndex);

            // check if object interaction is finished
            if (textureIndex == maxTextureIndex) {
                this.render = false;
                this.finished = true;
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(textures[textureIndex], bounds.x, bounds.y, bounds.width, bounds.height);
    }

}
