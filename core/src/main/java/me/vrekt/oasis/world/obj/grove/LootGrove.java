package me.vrekt.oasis.world.obj.grove;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.world.GameWorld;

public final class LootGrove {
    private final String childKey;
    private final ItemRarity rarity;

    private final LootGroveGenerator generator;
    private final Array<Vector2> points = new Array<>();

    public LootGrove(String childKey, ItemRarity rarity) {
        this.childKey = childKey;
        this.rarity = rarity;
        this.generator = assignGenerator();
    }

    /**
     * Assign the right generator for this loot grove
     *
     * @return the generator
     */
    private LootGroveGenerator assignGenerator() {
        return switch (rarity) {
            case COMMON -> new CommonLootGroveGenerator(rarity);
            case null, default -> throw new UnsupportedOperationException();
        };
    }

    public void addRewardPoint(Vector2 point) {
        points.add(point);
    }

    /**
     * Generate this grove.
     *
     * @param world world in
     * @param asset asset
     */
    public void generate(GameWorld world, Asset asset) {
        generator.generate(world, asset, points);
    }

}
