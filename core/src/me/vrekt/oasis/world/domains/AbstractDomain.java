package me.vrekt.oasis.world.domains;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.instance.AbstractInstance;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

/**
 * A base representation of a domain within the world.
 * <p>
 * A domain is a separate dungeon or instance with specific rewards and monsters.
 */
public abstract class AbstractDomain extends AbstractInstance {

    // domains are cached for 8 minutes.
    private static final float CACHE_TIME = 500;
    protected final DomainType type;

    protected boolean locked = false;

    public AbstractDomain(DomainType domainType, OasisGame game, Vector2 entrance, AbstractWorld worldIn) {
        super(game, entrance, worldIn);
        this.type = domainType;

        table.add(new Label(domainType.getPrettyName(), game.getAsset().getSkin(), "big", Color.WHITE)).center();
    }

    public boolean isLocked() {
        return locked;
    }

    public void unlock() {
        this.locked = false;
    }

    @Override
    public boolean enterInstance(Asset asset, AbstractWorld worldIn, OasisGame game, GlobalGameRenderer renderer, Player thePlayer) {
        super.enterInstance(asset, worldIn, game, renderer, thePlayer);

        TiledMap map;
        try {
            map = asset.loadDomain(type, CACHE_TIME);
            loadCollision(map);
            loadActions(map);

            spawnEntities();
        } catch (Exception any) {
            Logging.error(this, "Failed to enter domain instance: \n" + any);
            return this.enterable = false;
        }

        this.ready(map);
        return true;
    }
}
