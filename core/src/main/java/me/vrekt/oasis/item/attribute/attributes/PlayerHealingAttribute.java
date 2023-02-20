package me.vrekt.oasis.item.attribute.attributes;

import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.attribute.ItemAttribute;

/**
 * Grants the player a specific amount of HP.
 */
public final class PlayerHealingAttribute extends ItemAttribute {

    // ID
    public static final int ID = 1;

    // amount to heal by.
    private final float amount;

    public PlayerHealingAttribute(float amount) {
        super("Healing", "[/][BLACK]Applies [GREEN]+" + amount + " [BLACK]HP.", PlayerHealingAttribute.ID);
        this.amount = amount;
        setUses(1);
    }

    @Override
    public String getAttributeName() {
        return "[BLACK][/]" + super.getAttributeName();
    }

    @Override
    public void applyToPlayer(OasisPlayerSP player) {
        timesUsed++;
        if (timesUsed > getUses()) return;
        player.modifyHealth(amount);
    }
}

