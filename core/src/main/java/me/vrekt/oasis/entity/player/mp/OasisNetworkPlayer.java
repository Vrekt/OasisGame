package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import lunar.shared.drawing.Rotation;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.NetworkEntityPlayer;

/**
 * Represents any player over the network
 */
public final class OasisNetworkPlayer extends NetworkEntityPlayer implements ResourceLoader {

    private EntityAnimationComponent animationComponent;
    private float oldRotation = 0;

    public OasisNetworkPlayer(boolean initializeComponents) {
        super(initializeComponents);

        setDoPositionInterpolation(true);
        setSnapToPositionIfDesync(true);
        setInterpolateDesyncDistance(2.5f);
        setInterpolateAlpha(1.0f);
        setIgnorePlayerCollision(true);
        setHasMoved(true);
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
