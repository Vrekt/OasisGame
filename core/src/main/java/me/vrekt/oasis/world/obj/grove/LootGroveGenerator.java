package me.vrekt.oasis.world.obj.grove;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.world.GameWorld;

/**
 * Base of a loot grove generator
 */
public abstract class LootGroveGenerator {

    protected final ItemRarity rarity;
    protected final String childKey;

    public LootGroveGenerator(ItemRarity rarity, String childKey) {
        this.rarity = rarity;
        this.childKey = childKey;
    }

    public abstract void generate(GameWorld world, Asset asset, Array<Vector2> points);

    protected boolean generateContainer(float chance) {
        return MathUtils.randomBoolean(chance);
    }

    protected boolean chance(float chance) {
        return MathUtils.randomBoolean(chance);
    }

    /**
     * Generate a random item matching the rarity of this grove
     */
    protected Item generateRandomItem(float stepAboveRarityChance) {
        // NOTE-TODO: this will cause issues with the ceil of rarity (currently void)
        final ItemRarity r = MathUtils.randomBoolean(stepAboveRarityChance) ? ItemRarity.values()[rarity.ordinal() + 1] : rarity;
        return ItemRegistry.createRandomItemWithRarity(r, 1);
    }

    /**
     * Generate a random item matching the rarity of this grove
     *
     * @param multipleItemsChance chance for it to be multiple items
     */
    protected Item generateRandomItem(float multipleItemsChance, float stepAboveRarityChance) {
        // NOTE-TODO: this will cause issues with the ceil of rarity (currently void)
        final ItemRarity r = MathUtils.randomBoolean(stepAboveRarityChance) ? ItemRarity.values()[rarity.ordinal() + 1] : rarity;
        final int amount = chance(multipleItemsChance) ? MathUtils.random(1, 3) : 1;
        return ItemRegistry.createRandomItemWithRarity(r, amount);
    }


}
