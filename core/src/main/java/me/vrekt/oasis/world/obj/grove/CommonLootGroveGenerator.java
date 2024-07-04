package me.vrekt.oasis.world.obj.grove;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;

/**
 * Generate common loot
 */
public final class CommonLootGroveGenerator extends LootGroveGenerator {

    private static final float CONTAINER_CHANCE = 0.3f;
    private static final float CONTAINER_MULTIPLE_ITEMS_CHANCE = 0.1f;
    private static final float MULTIPLE_ITEMS_CHANCE = 0.1f;
    private static final float STEP_ABOVE_RARITY_CHANCE = 0.25f;

    public CommonLootGroveGenerator(ItemRarity rarity) {
        super(rarity);
    }

    @Override
    public void generate(GameWorld world, Asset asset, Array<Vector2> points) {
        points.forEach(p -> handlePoint(world, p));
    }

    private void handlePoint(GameWorld world, Vector2 point) {
        if (generateContainer(CONTAINER_CHANCE)) {
            generateContainer(world, point);
        } else {
            generateRandomItem(world, point);
        }
    }

    private void generateContainer(GameWorld world, Vector2 point) {
        final ContainerInventory inventory = new ContainerInventory(16);
        final OpenableContainerInteraction interaction = new OpenableContainerInteraction(inventory);

        if (MathUtils.randomBoolean(CONTAINER_MULTIPLE_ITEMS_CHANCE)) {
            for (int i = 0; i < MathUtils.random(1, 3); i++) {
                inventory.add(generateRandomItem(STEP_ABOVE_RARITY_CHANCE));
            }
        } else {
            inventory.add(generateRandomItem(CONTAINER_MULTIPLE_ITEMS_CHANCE));
        }

        world.spawnWorldObject(interaction, "container_crate", point);
    }

    /**
     * Generate a random item matching the rarity of this grove
     *
     * @param world world
     * @param point where
     */
    private void generateRandomItem(GameWorld world, Vector2 point) {
        world.spawnWorldDrop(generateRandomItem(MULTIPLE_ITEMS_CHANCE, STEP_ABOVE_RARITY_CHANCE), point);
    }

}
