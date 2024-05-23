package me.vrekt.oasis.entity.player.sp.attribute.attributes;

import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.entity.player.sp.attribute.AttributeType;

/**
 * Provides a small damage boost to the player.
 */
public final class PlayerSatisfactionAttribute extends Attribute {

    private static final String KEY = "attribute:satisfaction";
    private static final String NAME = "Satisfaction";
    private static final String TEXTURE = "satisfaction_attribute3";
    private static final String DESCRIPTION = "Gives you a small damage boost.";

    public PlayerSatisfactionAttribute(float duration, float strength) {
        super(KEY, NAME, DESCRIPTION, AttributeType.BASE_DAMAGE_MULTIPLIER, duration, strength);
        this.texture = TEXTURE;
    }

}
