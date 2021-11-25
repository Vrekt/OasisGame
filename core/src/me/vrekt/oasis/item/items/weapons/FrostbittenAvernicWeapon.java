package me.vrekt.oasis.item.items.weapons;

import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.ItemRarity;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;

public final class FrostbittenAvernicWeapon extends Item {

    public FrostbittenAvernicWeapon(Asset asset) {
        super("Frostbitten Avernic", "frostbitten_avernic", ItemRarity.EPIC);
        this.texture = asset.getAssets().findRegion(this.textureName);
        this.addAttribute(ItemAttributeType.ATK, 36);
        this.addAttribute(ItemAttributeType.BANISHING, 1);
        this.description = "A blade crafted from the frozen depths of Athena.";
    }
}
