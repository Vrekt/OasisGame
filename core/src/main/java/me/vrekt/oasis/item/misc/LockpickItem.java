package me.vrekt.oasis.item.misc;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.utility.ItemDescriptor;

import java.util.SplittableRandom;

/**
 * Lockpick item
 */
public final class LockpickItem extends Item {

    private static final SplittableRandom RANDOM = new SplittableRandom();

    public static final String KEY = "oasis:lock_pick";
    public static final String NAME = "Lockpick";
    public static final String DESCRIPTION = "A flimsy lockpick molded from silver.";
    public static final String TEXTURE = "lockpick";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    // the chance that this lockpick will break if the player fails
    private final float breakChance;

    public LockpickItem() {
        super(Items.LOCK_PICK, KEY, NAME, DESCRIPTION);

        this.isStackable = true;
        this.rarity = ItemRarity.COMMON;
        this.breakChance = 5.0f;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }

    /**
     * If this lockpick should break
     * TODO: Fix EM-94
     * @return {@code true} if so
     */
    public boolean shouldBreak() {
        return false;
    }

    /**
     * Destroy this item
     *
     * @param player player
     */
    public void destroy(PlayerSP player) {
        GameManager.playSound(Sounds.LOCKPICK_BREAK, 0.5f, 1.0f, 1.0f);
        player.getInventory().remove(this);
    }

}
