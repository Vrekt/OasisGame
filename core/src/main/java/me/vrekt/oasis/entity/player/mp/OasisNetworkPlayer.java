package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private boolean rotationChanged;
    private float oldRotation = 0;

    public OasisNetworkPlayer(boolean initializeComponents) {
        super(initializeComponents);

        doPositionInterpolation = true;
        interpolateAlpha = 1.0f;
    }

    @Override
    public void load(Asset asset) {
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        putRegion("walking_up_idle", asset.get("walking_up_idle"));
        putRegion("walking_down_idle", asset.get("walking_down_idle"));
        putRegion("walking_left_idle", asset.get("walking_left_idle"));
        putRegion("walking_right_idle", asset.get("walking_right_idle"));
        currentRegionState = getRegion("walking_up_idle");

        // up, down, left, right
        animationComponent.registerWalkingAnimation(0, 0.25f, asset.get("walking_up", 1), asset.get("walking_up", 2));
        animationComponent.registerWalkingAnimation(1, 0.25f, asset.get("walking_down", 1), asset.get("walking_down", 2));
        animationComponent.registerWalkingAnimation(2, 0.25f, asset.get("walking_left", 1), asset.get("walking_left", 2));
        animationComponent.registerWalkingAnimation(3, 0.25f, asset.get("walking_right", 1), asset.get("walking_right", 2));
    }

    @Override
    public void update(float delta) {
        super.update(delta);

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
            batch.draw(animationComponent.playWalkingAnimation(rotation, delta), getInterpolated().x, getInterpolated().y, getWidthScaled(), getHeightScaled());
        } else {
            if (currentRegionState != null) {
                batch.draw(currentRegionState, getInterpolated().x, getInterpolated().y, getWidthScaled(), getHeightScaled());
            }
        }
    }

    private void setIdleRegionState() {
        switch (Rotation.of(getRotation())) {
            case FACING_UP:
                currentRegionState = getRegion("walking_up_idle");
                break;
            case FACING_DOWN:
                currentRegionState = getRegion("walking_down_idle");
                break;
            case FACING_LEFT:
                currentRegionState = getRegion("walking_left_idle");
                break;
            case FACING_RIGHT:
                currentRegionState = getRegion("walking_right_idle");
                break;
        }
    }

}
