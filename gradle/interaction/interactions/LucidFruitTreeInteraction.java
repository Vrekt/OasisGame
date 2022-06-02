package me.vrekt.oasis.world.interaction.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.interaction.WorldInteraction;

/**
 * WorldInteraction for lucid fruit tree
 */
public final class LucidFruitTreeInteraction extends WorldInteraction {

    private ParticleEffect harvestingEffect;

    public LucidFruitTreeInteraction() {
        this.interactionDistance = 6.9f;
        this.interactionTime = 3f;
        this.isInteractable = false;
    }

    @Override
    public void load(Asset asset) {
        harvestingEffect = new ParticleEffect();
        harvestingEffect.load(Gdx.files.internal("world/asset/lucid_fruit_tree_harvest_particle"), asset.getAtlasAssets());
        harvestingEffect.setPosition(location.x, location.y);
    }

    @Override
    public void interact(OasisPlayerSP player) {
        super.interact(player);
        world.removeEffect(environmentObject.getEffect());
        environmentObject.setPlayEffect(false);
        harvestingEffect.start();
    }

    @Override
    public void interactionFinished() {
        this.isInteractable = false;
        world.getWorld().destroyBody(environmentObject.getCollisionBody());
        world.removeEffect(environmentObject.getEffect());
        environmentObject.removeEffect();
        environmentObject.setPlayEffect(false);
        world.removeEnvironmentObject(id);
    }

    @Override
    public void update(OasisPlayerSP player) {
        super.update(player);

        harvestingEffect.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void invalidate() {
        super.invalidate();
        world.addEffect(environmentObject.getEffect());
        environmentObject.getEffect().reset();
        environmentObject.setPlayEffect(true);
    }

    @Override
    public void render(SpriteBatch batch) {
        harvestingEffect.draw(batch);
    }
}
