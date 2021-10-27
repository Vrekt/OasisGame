package me.vrekt.oasis.item.items.tools;

import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.ItemRarity;

/**
 * Basic starter pickaxe
 */
public final class CommonPickaxeItem extends Item {

    public CommonPickaxeItem() {
        super("Pickaxe", "pickaxe", ItemRarity.COMMON);
        this.description = "A basic pickaxe.";
        this.amount = 1;
    }
}
