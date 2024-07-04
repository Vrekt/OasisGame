package me.vrekt.oasis.item.consumables.food;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.consumables.ItemConsumable;

/**
 * Crimson cap mushroom
 */
public final class CrimsonCapItem extends ItemConsumable {

    public static final String NAME = "Crimson Cap";
    public static final String KEY = "oasis:crimson_cap";
    public static final String TEXTURE = "crimson_cap_item";
    public static final String DESCRIPTION = "A cap from the crimson mushroom, temporarily grants you poison resistance.";

    public CrimsonCapItem() {
        super(Items.CRIMSON_CAP, KEY, NAME, DESCRIPTION);

        this.rarity = ItemRarity.UN_COMMON;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }
}
