package me.vrekt.oasis.world.farm.flora.ringfruit;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.animation.Anim;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.farm.FarmingAllotment;
import me.vrekt.oasis.world.farm.flora.Plant;
import me.vrekt.oasis.world.farm.flora.effect.PlantEffect;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A ringfruit is a plant with protective properties...
 */
public final class RingfruitPlant extends Plant {

    private long growthStageDelay, lastGrow;

    public RingfruitPlant(FarmingAllotment owner, Asset assets, float x, float y) {
        super(owner, assets, x, y);

        this.effect = PlantEffect.RESISTANCE;
        this.shouldUpdate = true;

        this.growthStage = 0;
        this.growthStages = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            growthStages[i] = assets.getAtlas(Asset.PLANTS).findRegion("ringfruit_stage", i + 1);
        }

        this.plantTexture = growthStages[0];
        growthStageDelay = ThreadLocalRandom.current().nextLong(1000, 4000);
        lastGrow = System.currentTimeMillis();
    }

    @Override
    public void interactWith(Player player) {

    }

    @Override
    public void update(Player player, Anim anim) {
        if (System.currentTimeMillis() - lastGrow >= growthStageDelay) {
            // grow this plant
            growthStage++;
            if (growthStage >= 5) {
                // growing is complete for this plant
                this.shouldUpdate = false;
                this.owner.complete();
            } else {
                this.plantTexture = growthStages[growthStage];
            }

            // re-assign random growth value
            growthStageDelay = ThreadLocalRandom.current().nextLong(1000, 4000);
            lastGrow = System.currentTimeMillis();
        }
    }

    @Override
    public void render(SpriteBatch batch, float x, float y, float scale) {
        batch.draw(plantTexture, x, y, plantTexture.getRegionWidth() * scale, plantTexture.getRegionHeight() * scale);
    }
}
