package me.vrekt.oasis.entity.player.spells.test;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.LunarEntity;
import lunar.shared.entity.player.LunarEntityPlayer;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.spells.PlayerSpell;

public final class PlayerSpellTest extends PlayerSpell implements ResourceLoader {

    private final OasisGame game;
    private int i = 1;

    private long last;

    private boolean going = false;
    private Vector2 last1 = new Vector2();

    public PlayerSpellTest(OasisGame game) {
        this.game = game;
    }

    @Override
    public void load(Asset asset) {
        for (int i = 1; i < 8; i++) {
            final TextureRegion region = asset.getSpells().findRegion(i + "");
            if (region != null) {
                putRegion(i + "", region);
            }
        }

        currentRegionState = getRegion(1 + "");
        setConfig(64, 64, OasisGameSettings.SCALE);
        setPosition(game.getPlayer().getPosition().add(1, 1), true);
        last = System.currentTimeMillis();
    }

    public void reset() {
        i = 1;
        setPosition(game.getPlayer().getPosition().add(-1.5f, -0.5f), true);
        last1.set(getPosition());
        going = true;
    }

    @Override
    public void update(float v) {

        if (System.currentTimeMillis() - last >= 10) {
            last = System.currentTimeMillis();

            if (last1.dst2(getPosition()) >= .5f) {
                last1.set(getPosition());
                i++;
            }
        }

        if (i >= 8) {
            i = 1;
            going = false;
        }

        setPosition(getPosition().x, getPosition().y + 0.03f, true);
        currentRegionState = getRegion(i + "");

        super.update(v);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (going) {
            // draw generic texture
            if (currentRegionState != null) {
                batch.draw(currentRegionState, getX(), getY(), getWidth() * getScaling(), getHeight() * getScaling());
            }
        }

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> lunarWorld) {
        //    super.spawnEntityInWorld(lunarWorld, getPosition().x, getPosition().y);
    }
}
