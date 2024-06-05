package me.vrekt.oasis.item.weapons;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.utility.ItemDescriptor;

public final class TemperedBladeItem extends ItemWeapon {

    public static final String KEY = "oasis:enchanted_violet_sword";
    public static final String NAME = "Tempered Blade";
    public static final String TEXTURE = "enchanted_violet";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public TemperedBladeItem() {
        super(Items.TEMPERED_BLADE, KEY, NAME, "A blade with a razor sharp edge.");

        this.baseDamage = 1.5f;
        this.swingCooldown = 0.1f;
        this.criticalHitChance = 15.0f;
        this.criticalHitDamage = 6.5f;
        this.knockbackMultiplier = 1.6f;
        this.range = 0.8f;
        this.rarity = ItemRarity.VOID;
        this.isStackable = false;
    }

    @Override
    public void load(Asset asset) {
        sprite = asset.get(TEXTURE);
        this.width = sprite.getRegionWidth() * OasisGameSettings.SCALE;
        this.height = sprite.getRegionHeight() * OasisGameSettings.SCALE;
        bounds.setSize(width, height);
    }
}
