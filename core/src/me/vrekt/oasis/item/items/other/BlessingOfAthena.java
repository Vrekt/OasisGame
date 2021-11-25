package me.vrekt.oasis.item.items.other;

import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.ItemRarity;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;

public final class BlessingOfAthena extends Item {

    public BlessingOfAthena(Asset asset) {
        super("Blessing of Athena", "blessing_of_athena", ItemRarity.RARE);
        this.texture = asset.get(textureName);

        addAttribute(ItemAttributeType.HP, 136);
        addAttribute(ItemAttributeType.DEF, 12);
        this.description = "A blessing artifact of protection from Athena.";
    }
}
