package me.vrekt.oasis.item.items.weapons;

import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.ItemRarity;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;

public final class BladeOfAverrWeapon extends Item {

    public BladeOfAverrWeapon(Asset asset) {
        super("Blade Of Averr", "blade_of_averr", ItemRarity.EPIC);
        this.texture = asset.getAssets().findRegion(this.textureName);

        this.addAttribute(ItemAttributeType.ATK, 46);
        this.addAttribute(ItemAttributeType.CRITICAL_DAMAGE, 10);
        this.description = "A blade born from Averrs' enchantment.";
    }
}
