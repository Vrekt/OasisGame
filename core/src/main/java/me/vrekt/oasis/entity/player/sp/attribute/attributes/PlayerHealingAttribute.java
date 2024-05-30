package me.vrekt.oasis.entity.player.sp.attribute.attributes;

import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;

/**
 * Applies health to the player.
 */
public final class PlayerHealingAttribute extends Attribute {

    private static final String KEY = "attribute:healing";
    private static final String NAME = "Healing";
    private static final String TEXTURE = "healing_attribute2";
    private static final String DESCRIPTION = "Heals you.";

    private final float amount;

    public PlayerHealingAttribute(float amount) {
        super(KEY, NAME, DESCRIPTION);
        this.amount = amount;
        this.texture = TEXTURE;
        this.instant = true;
        this.subType = AttributeSubType.HEALING;
    }

    @Override
    public void apply(PlayerSP player) {
        player.heal(amount);
    }
}
