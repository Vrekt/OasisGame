package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.world.interior.Interior;

/**
 * Represents a single world object that you could probably interact with
 */
public final class InteractableWorldObject {

    private final Rectangle bounds;
    private final Vector2 center = new Vector2();
    private Body body;

    // textures of this object
    // private final TextureRegion[] textures;
    // properties
    private final boolean isBreakable, isSolid, enterable;

    // private final int maxTextureIndex;
    private int textureIndex;
    private boolean render, finished;

    // the entity this object could be related to.
    private final String relatedTo;
    private final Interior interior;

    public InteractableWorldObject(MapObject object, Rectangle regionBounds, Asset asset, World world, float scale) {
        this.bounds = regionBounds;
        this.render = true;
        bounds.getCenter(center);

        // load properties
        this.isBreakable = object.getProperties().get("breakable", false, Boolean.class);
        this.isSolid = object.getProperties().get("solid", false, Boolean.class);
        this.relatedTo = object.getProperties().get("relatedTo", null, String.class);
        this.enterable = object.getProperties().get("enterable", false, Boolean.class);

        final String name = object.getProperties().get("interior", null, String.class);
        if (name != null) {
            this.interior = Interior.valueOf(object.getProperties().get("interior", String.class));
            Logging.info("Objects", "Loaded interior: " + interior);
        } else {
            this.interior = Interior.OUTSIDE;
        }

        // load collision info
        if (isSolid) {
            //    final Shape shape = CollisionShapeCreator.createPolygonShape((RectangleMapObject) object, scale, false);
            //    this.body = world.createBody(AbstractWorld.STATIC_BODY);
            //     this.body.createFixture(shape, 1.0f);
            //     shape.dispose();
        }


    }

    public boolean isSolid() {
        return isSolid;
    }

    public boolean isBreakable() {
        return isBreakable;
    }

    public boolean isEnterable() {
        return enterable;
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

    public Interior getInterior() {
        return interior;
    }

    public Body getBody() {
        return body;
    }

    public boolean isNear(Player player) {
        return player.getPosition().dst2(center.x, center.y) <= 10f;
    }

    public void interact() {

    }

    public void render(SpriteBatch batch) {
        // batch.draw(textures[textureIndex], bounds.x, bounds.y, bounds.width, bounds.height);
    }

}
