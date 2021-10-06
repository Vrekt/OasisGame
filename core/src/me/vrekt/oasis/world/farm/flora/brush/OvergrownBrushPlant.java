package me.vrekt.oasis.world.farm.flora.brush;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.asset.WorldAsset;
import me.vrekt.oasis.world.farm.FarmingAllotment;
import me.vrekt.oasis.world.farm.animation.BasicFloraAnimation;
import me.vrekt.oasis.world.farm.flora.Plant;

/**
 * The overgrown weeds plant that must be cleared first to farm.
 */
public final class OvergrownBrushPlant extends Plant {

    private OvergrownBrushStage stage = OvergrownBrushStage.OVERGROWN_3;

    /**
     * Initial point where player interacted
     */
    private float interactionX, interactionY;
    private boolean reset;

    public OvergrownBrushPlant(FarmingAllotment owner, WorldAsset assets, float x, float y) {
        super(owner, assets, x, y);

        this.plantTexture = assets.getTexture(stage.getAsset());
        this.animation = new BasicFloraAnimation(1000);
    }

    @Override
    public void interactWith(Player player) {
        this.wasInteractedWith = true;

        if (reset && !this.shouldUpdate) {
            this.shouldUpdate = true;
            this.reset = false;
        }
    }

    @Override
    public void update(Player player) {
        if (interactionX == 0 && interactionY == 0) {
            interactionX = player.getPosition().x;
            interactionY = player.getPosition().y;
        }

        // check if the player moved while animation is in progress.
        // if so return and stop progress.
        if ((interactionX != player.getPosition().x || interactionY != player.getPosition().y)
                && player.getPosition().dst2(interactionX, interactionY) >= 0.5) {
            this.shouldUpdate = false;
            this.wasInteractedWith = false;
            this.reset = true;
            this.interactionX = 0;
            this.interactionY = 0;
            this.owner.setInteractingWith(false);
            return;
        }

        // animation state is ready to be updated.
        if (animation.isReady()) {
            if (stage == OvergrownBrushStage.FINISHED) {
                this.isComplete = true;
                this.shouldUpdate = false;
                owner.complete();
            } else {
                updatePlantStatus();
            }

            animation.updateAnimation();
        }
    }

    @Override
    public void render(SpriteBatch batch, float x, float y, float scale) {
        batch.draw(plantTexture, x, y, plantTexture.getWidth() * scale, plantTexture.getHeight() * scale);
    }

    /**
     * Update plant status
     */
    private void updatePlantStatus() {
        switch (stage) {
            case OVERGROWN_3:
                stage = OvergrownBrushStage.OVERGROWN_2;
                break;
            case OVERGROWN_2:
                this.plantTexture = assets.getTexture(stage.getAsset());
                stage = OvergrownBrushStage.OVERGROWN_1;
                break;
            case OVERGROWN_1:
                this.plantTexture = assets.getTexture(stage.getAsset());
                stage = OvergrownBrushStage.FINISHED;
                break;
        }
    }


}
