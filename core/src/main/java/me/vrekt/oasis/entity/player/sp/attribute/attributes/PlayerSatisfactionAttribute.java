package me.vrekt.oasis.entity.player.sp.attribute.attributes;

import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.entity.player.sp.attribute.AttributeType;

/**
 * Provides a small damage boost to the player.
 */
public final class PlayerSatisfactionAttribute extends Attribute {

    private static final String KEY = "attribute:satisfaction";
    private static final String NAME = "Satisfaction";

    public PlayerSatisfactionAttribute(float duration, float strength) {
        super(KEY, NAME, AttributeType.BASE_DAMAGE_MULTIPLIER, duration, strength);
    }

}
