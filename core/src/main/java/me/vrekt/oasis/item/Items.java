package me.vrekt.oasis.item;

import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.*;
import me.vrekt.oasis.item.misc.LockpickItem;
import me.vrekt.oasis.item.usable.RingOfMyceliaItem;
import me.vrekt.oasis.item.misc.WrynnRecipeBookItem;
import me.vrekt.oasis.item.weapons.TemperedBladeItem;
import me.vrekt.oasis.item.weapons.magic.StaffOfEarthItem;
import me.vrekt.oasis.item.weapons.magic.StaffOfObsidian;

/**
 * Map of all items within the game
 */
public enum Items {

    NO_ITEM("oasis:none"),

    LUCID_FRUIT_TREE_ITEM(LucidTreeFruitItem.KEY),
    TEMPERED_BLADE(TemperedBladeItem.KEY),
    QUICKSTEP_ARTIFACT(QuickStepItemArtifact.KEY),
    PIG_HEART(PigHeartConsumable.KEY),
    WRYNN_RECIPE_BOOK(WrynnRecipeBookItem.KEY),
    LOCK_PICK(LockpickItem.KEY),
    RING_OF_MYCELIA(RingOfMyceliaItem.KEY),
    CRIMSON_CAP(CrimsonCapItem.KEY),
    VERDANT_FUNGUS(VerdantFungusItem.KEY),
    SILVER_SPORE(SilverSporeItem.KEY),
    STAFF_OF_EARTH(StaffOfEarthItem.KEY),
    STAFF_OF_OBSIDIAN(StaffOfObsidian.KEY);

    private final String key;

    Items(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
