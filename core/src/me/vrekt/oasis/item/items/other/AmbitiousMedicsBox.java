package me.vrekt.oasis.item.items.other;

import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.ItemRarity;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;

public final class AmbitiousMedicsBox extends Item {

    public AmbitiousMedicsBox(Asset asset) {
        super("Ambitious Medics' Box", "ambitious_medics_box", ItemRarity.RARE);
        this.texture = asset.get(textureName);

        addAttribute(ItemAttributeType.HP, 256);
        addAttribute(ItemAttributeType.DEF, 2);
        this.description = "A kit of useful medic tools.";
    }

}
