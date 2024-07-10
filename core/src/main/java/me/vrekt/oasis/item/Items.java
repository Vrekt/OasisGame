package me.vrekt.oasis.item;

import com.badlogic.gdx.math.MathUtils;
import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.*;
import me.vrekt.oasis.item.misc.LockpickItem;
import me.vrekt.oasis.item.usable.ArcanaCodexItem;
import me.vrekt.oasis.item.usable.RingOfMyceliaItem;
import me.vrekt.oasis.item.misc.WrynnRecipeBookItem;
import me.vrekt.oasis.item.weapons.TemperedBladeItem;
import me.vrekt.oasis.item.weapons.magic.StaffOfEarthItem;
import me.vrekt.oasis.item.weapons.magic.StaffOfObsidian;

/**
 * Map of all items within the game
 */
public enum Items {

    NO_ITEM("oasis:none", ItemRarity.COMMON),
    TEMPERED_BLADE(TemperedBladeItem.KEY, ItemRarity.VOID),
    QUICKSTEP_ARTIFACT(QuickStepItemArtifact.KEY, ItemRarity.COSMIC),
    PIG_HEART(PigHeartConsumable.KEY, ItemRarity.UN_COMMON),
    WRYNN_RECIPE_BOOK(WrynnRecipeBookItem.KEY, ItemRarity.COSMIC),
    LOCK_PICK(LockpickItem.KEY, ItemRarity.COMMON),
    RING_OF_MYCELIA(RingOfMyceliaItem.KEY, ItemRarity.VOID),
    CRIMSON_CAP(CrimsonCapItem.KEY, ItemRarity.UN_COMMON),
    VERDANT_FUNGUS(VerdantFungusItem.KEY, ItemRarity.UN_COMMON),
    SILVER_SPORE(SilverSporeItem.KEY, ItemRarity.VOID),
    STAFF_OF_EARTH(StaffOfEarthItem.KEY, ItemRarity.COSMIC),
    STAFF_OF_OBSIDIAN(StaffOfObsidian.KEY, ItemRarity.VOID),
    ARCANA_CODEX(ArcanaCodexItem.KEY, ItemRarity.UN_COMMON);

    private final String key;
    private final ItemRarity rarity;

    Items(String key, ItemRarity rarity) {
        this.key = key;
        this.rarity = rarity;
    }

    public String getKey() {
        return key;
    }

    public ItemRarity rarity() {
        return rarity;
    }

    public static Items random() {
        return values()[MathUtils.random(1, values().length - 1)];
    }

}
