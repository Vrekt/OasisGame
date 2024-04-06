package me.vrekt.oasis.item.attribute.attributes;

import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.attribute.AbstractItemAttribute;

/**
 * Grants the player a specific amount of HP.
 */
public final class PlayerHealingAttribute extends AbstractItemAttribute {

    private final float healingAmount;

    public PlayerHealingAttribute(float healingAmount) {
        super("attribute:healing", "Healing", "Implement this later.");
        this.healingAmount = healingAmount;
        setAvailableUses(1);
    }

    @Override
    public void applyToPlayer(OasisPlayer player) {
        if (timesUsed > getAvailableUses()) return;

        timesUsed++;
        player.heal(healingAmount);
    }
}

