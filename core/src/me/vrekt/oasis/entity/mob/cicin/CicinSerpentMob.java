package me.vrekt.oasis.entity.mob.cicin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.mob.EntityMob;
import me.vrekt.oasis.world.AbstractWorld;

public final class CicinSerpentMob extends EntityMob {


    public CicinSerpentMob(String name, float x, float y, OasisGame game, AbstractWorld worldIn) {
        super(name, x, y, game, worldIn);
    }

    @Override
    public void render(SpriteBatch batch, float scale) {
        game.getAsset().getTest().setPosition(getPosition().x + (((width * scale)) / 2f), getPosition().y + ((height * scale) / 2f));
        game.getAsset().getTest().update(Gdx.graphics.getDeltaTime());
        game.getAsset().getTest().draw(batch);

        super.render(batch, scale);
    }

    @Override
    public void loadEntity(Asset asset) {
        this.entityTexture = asset.getAssets().findRegion("cicin_serpent");
        this.width = entityTexture.getRegionWidth();
        this.height = entityTexture.getRegionHeight();

        game.getAsset().getTest().start();
    }
}
