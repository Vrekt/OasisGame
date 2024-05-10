package me.vrekt.oasis.entity.player.sp.attribute.attributes;

import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;

/**
 * Applies health to the player.
 */
public final class PlayerHealingAttribute extends Attribute {

    private static final String KEY = "attribute:healing";
    private static final String NAME = "Healing";
    private final float amount;

    public PlayerHealingAttribute(float amount) {
        super(KEY, NAME);
        this.amount = amount;
    }

    @Override
    public void apply(OasisPlayer player) {
        player.heal(amount);
    }
}
