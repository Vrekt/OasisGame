package me.vrekt.oasis.world.districts;

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

import java.util.*;

/**
 * A base representation of a domain within the world.
 * <p>
 * A domain is a separate dungeon or instance with specific rewards and monsters.
 */
public abstract class AbstractDistrict extends AbstractInstance {

    // domains are cached for 8 minutes.
    private static final float CACHE_TIME = 500;
    protected final DistrictType type;

    protected boolean locked = false;
    protected Map<String, Integer> levels = new LinkedHashMap<>();
    protected Map<DistrictReward, Integer> rewards = new LinkedHashMap<>();
    protected String description;

    public AbstractDistrict(DistrictType districtType, OasisGame game, Vector2 entrance, AbstractWorld worldIn) {
        super(game, entrance, worldIn);
        this.type = districtType;

        table.add(new Label(districtType.getPrettyName(), game.getAsset().getSkin(), "big", Color.WHITE)).center();
    }

    public DistrictType getType() {
        return type;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void addLevel(String name, int level) {
        levels.put(name, level);
    }

    protected void addReward(DistrictReward reward, int amount) {
        this.rewards.put(reward, amount);
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Integer> getLevels() {
        return levels;
    }

    public Map<DistrictReward, Integer> getRewards() {
        return rewards;
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
