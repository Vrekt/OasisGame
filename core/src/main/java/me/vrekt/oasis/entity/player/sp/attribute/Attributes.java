package me.vrekt.oasis.entity.player.sp.attribute;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class Attributes {

    private final Array<Attribute> attributes = new Array<>();

    /**
     * Add an attribute
     *
     * @param attribute the attribute
     */
    public void add(Attribute attribute) {
        attributes.add(attribute);
    }

    /**
     * Get an attribute strength
     *
     * @return the strength;
     */
    public float getAttributeStrength() {
        float strength = 1.0f;
        for (Attribute attribute : attributes) {
            strength += attribute.getStrength();
        }
        return strength;
    }

    /**
     * Update all active attributes
     */
    public void update() {
        for (Iterator<Attribute> iterator = attributes.iterator(); iterator.hasNext(); ) {
            final Attribute attribute = iterator.next();
            if (attribute.isExpired()) {
                iterator.remove();
            }
        }
    }

}
