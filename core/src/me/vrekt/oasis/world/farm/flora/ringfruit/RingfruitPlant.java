package me.vrekt.oasis.world.farm.flora.ringfruit;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.animation.Anim;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.farm.FarmingAllotment;
import me.vrekt.oasis.world.farm.flora.Plant;
import me.vrekt.oasis.world.farm.flora.effect.PlantEffect;

/**
 * A ringfruit is a plant with protective properties...
 */
public final class RingfruitPlant extends Plant {

    public RingfruitPlant(FarmingAllotment owner, Asset assets, float x, float y) {
        super(owner, assets, x, y);

        this.effect = PlantEffect.RESISTANCE;
    }

    @Override
    public void interactWith(Player player) {

    }

    @Override
    public void update(Player player, Anim anim) {

    }

    @Override
    public void render(SpriteBatch batch, float x, float y, float scale) {

    }
}
