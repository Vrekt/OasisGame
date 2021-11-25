package me.vrekt.oasis.item.items.other;

import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.ItemRarity;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;

public final class WandOfEmbracing extends Item {

    public WandOfEmbracing(Asset asset) {
        super("Wand of Embracing", "wand_of_embracing", ItemRarity.EPIC);
        this.texture = asset.get(textureName);

        addAttribute(ItemAttributeType.HP, 50);
        addAttribute(ItemAttributeType.LUCK, 1);
        addAttribute(ItemAttributeType.PACIFY, 1);
        this.description = "An enchanted wand from Athena providing great companionship.";
    }

}
