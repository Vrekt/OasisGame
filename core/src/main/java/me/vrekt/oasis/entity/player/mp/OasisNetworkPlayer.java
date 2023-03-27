package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import lunar.shared.drawing.Rotation;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.NetworkEntityPlayer;

/**
 * Represents any player over the network
 */
public final class OasisNetworkPlayer extends NetworkEntityPlayer implements ResourceLoader {

    private EntityAnimationComponent animationComponent;
    private float oldRotation = 0;

    private float nametagRenderWidth;
    private final Vector3 worldPosition = new Vector3();
    private final Vector3 screenPosition = new Vector3();

    private boolean renderNametag;

    public OasisNetworkPlayer(boolean initializeComponents) {
        super(initializeComponents);

        setDoPositionInterpolation(true);
        setSnapToPositionIfDesync(true);
        setInterpolateDesyncDistance(2.5f);
        setInterpolateAlpha(1.0f);
        setIgnorePlayerCollision(true);
        setHasMoved(true);
    }

    public void setRenderNametag(boolean renderNametag) {
        this.renderNametag = renderNametag;
    }

    public boolean shouldRenderNametag() {
        return renderNametag;
    }

    @Override
    public void setEntityName(String name) {
        super.setEntityName(name);

        final GlyphLayout fontLayout = new GlyphLayout(GameManager.getGui().getSmall(), getName());
        this.nametagRenderWidth = (fontLayout.width / 6f) * OasisGameSettings.SCALE;
        fontLayout.reset();

    }

    @Override
    public void load(Asset asset) {
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        putRegion("healer_walking_up_idle", asset.get("healer_walking_up_idle"));
        putRegion("healer_walking_down_idle", asset.get("healer_walking_down_idle"));
        putRegion("healer_walking_left_idle", asset.get("healer_walking_left_idle"));
        putRegion("healer_walking_right_idle", asset.get("healer_walking_right_idle"));
        currentRegionState = getRegion("healer_walking_up_idle");

        // up, down, left, right
        animationComponent.registerWalkingAnimation(0, 0.25f, asset.get("healer_walking_up", 1), asset.get("healer_walking_up", 2));
        animationComponent.registerWalkingAnimation(1, 0.25f, asset.get("healer_walking_down", 1), asset.get("healer_walking_down", 2));
        animationComponent.registerWalkingAnimation(2, 0.25f, asset.get("healer_walking_left", 1), asset.get("healer_walking_left", 2));
        animationComponent.registerWalkingAnimation(3, 0.25f, asset.get("healer_walking_right", 1), asset.get("healer_walking_right", 2));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setHasMoved(!getVelocity().isZero());

        if (oldRotation != rotation) {
            setIdleRegionState();
        }

        oldRotation = rotation;
    }

    @Override
    public void updatePosition(float x, float y, float angle) {
        super.updatePosition(x, y, angle);
    }

    @Override
    public void updateVelocity(float x, float y, float angle) {
        super.updateVelocity(x, y, angle);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.playWalkingAnimation(rotation, delta));
        } else {
            if (currentRegionState != null) {
                draw(batch, currentRegionState);
            }
        }
    }

    /**
     * Render the name tag of this player
     *
     * @param font        the font to use
     * @param batch       the batch to draw with
     * @param worldCamera the game world camera
     * @param guiCamera   the gui camera
     */
    public void renderNametag(BitmapFont font, Batch batch, Camera worldCamera, Camera guiCamera) {
        worldPosition.set(worldCamera.project(worldPosition.set(getInterpolated().x - nametagRenderWidth, getInterpolated().y + 2.25f, 0.0f)));
        screenPosition.set(guiCamera.project(worldPosition));
        font.draw(batch, getName(), screenPosition.x, screenPosition.y);
    }

    private void setIdleRegionState() {
        switch (Rotation.of(getRotation())) {
            case FACING_UP:
                currentRegionState = getRegion("healer_walking_up_idle");
                break;
            case FACING_DOWN:
                currentRegionState = getRegion("healer_walking_down_idle");
                break;
            case FACING_LEFT:
                currentRegionState = getRegion("healer_walking_left_idle");
                break;
            case FACING_RIGHT:
                currentRegionState = getRegion("healer_walking_right_idle");
                break;
        }
    }

    @Override
    public void defineEntity(World world, float x, float y) {
        super.defineEntity(world, x, y);
        this.body.setUserData(this);
    }
}
