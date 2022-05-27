package me.vrekt.oasis.world.environment;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import me.vrekt.oasis.asset.settings.OasisGameSettings;

/**
 * Represents an environment object not put on the Tiled map.
 */
public final class EnvironmentObject {

    private final TextureRegion texture;
    private final Vector2 location;
    private Body collisionBody;

    public EnvironmentObject(TextureRegion texture, float x, float y) {
        this.texture = texture;
        this.location = new Vector2(x - ((texture.getRegionWidth() / 2f) * OasisGameSettings.SCALE), y - ((texture.getRegionHeight() / 3f) * OasisGameSettings.SCALE));
    }

    public void setCollisionBody(Body collisionBody) {
        this.collisionBody = collisionBody;
    }

    public Body getCollisionBody() {
        return collisionBody;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, location.x, location.y, texture.getRegionWidth() * OasisGameSettings.SCALE, texture.getRegionHeight() * OasisGameSettings.SCALE);
    }

}
