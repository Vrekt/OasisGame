package me.vrekt.oasis.item.misc;

import com.badlogic.gdx.math.MathUtils;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.Items;

/**
 * Represents an item that has a chance to break
 */
public abstract class BreakableItem extends Item {

    protected Sounds breakSound;
    protected float breakChance;

    public BreakableItem(Items itemType, String key, String name, String description) {
        super(itemType, key, name, description);
    }

    /**
     * @return {@code true} if this item should break
     */
    public boolean shouldBreak() {
        return MathUtils.random() <= breakChance;
    }

    /**
     * Destroy this item
     *
     * @param player player
     */
    public void destroy(PlayerSP player) {
        GameManager.playSound(breakSound, 0.33f, 1.0f, 0.0f);
        player.getInventory().remove(this);
    }

}
