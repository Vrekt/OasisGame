package me.vrekt.oasis.item.items.other;

import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.ItemRarity;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;

public final class EnchantedBandOfPower extends Item {

    public EnchantedBandOfPower(Asset asset) {
        super("Enchanted Band of Power", "enchanted_band_of_power", ItemRarity.EPIC);
        this.texture = asset.get(textureName);

        addAttribute(ItemAttributeType.ATK, 56);
        addAttribute(ItemAttributeType.CRITICAL_DAMAGE, 4);
        addAttribute(ItemAttributeType.LUCK, 1);
        addAttribute(ItemAttributeType.BANISHING, 1);
        this.description = "A enchanted, diamond studded, band of power.";
    }

}
