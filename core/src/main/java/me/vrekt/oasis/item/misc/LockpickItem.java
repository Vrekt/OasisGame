package me.vrekt.oasis.item.misc;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.utility.ItemDescriptor;

/**
 * Lockpick item
 * Common lockpicks have a high % chance to randomly break if failed
 */
public final class LockpickItem extends BreakableItem {

    public static final String KEY = "oasis:lock_pick";
    public static final String NAME = "Lockpick";
    public static final String DESCRIPTION = "A flimsy lockpick molded from silver.";
    public static final String TEXTURE = "lockpick";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public LockpickItem() {
        super(Items.LOCK_PICK, KEY, NAME, DESCRIPTION);

        this.isStackable = true;
        this.rarity = ItemRarity.COMMON;

        this.breakSound = Sounds.LOCKPICK_BREAK;
        this.breakChance = 0.25f;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }

}
