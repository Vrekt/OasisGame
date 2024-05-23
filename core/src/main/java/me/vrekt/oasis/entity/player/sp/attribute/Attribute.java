package me.vrekt.oasis.entity.player.sp.attribute;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.PlayerSP;

/**
 * Base implementation of an attribute that is applied from an item to the player.
 */
public abstract class Attribute {

    protected final String key, name, description;
    protected String texture;

    protected final AttributeType type;

    protected final float duration;
    protected final float strength;

    protected boolean instant;
    protected float tickApplied;

    public Attribute(String key, String name, String description) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.type = AttributeType.NONE;
        this.duration = 0.0f;
        this.strength = 1.0f;
    }

    public Attribute(String key, String name, String description, AttributeType type, float duration, float strength) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.description = description;
        this.duration = duration;
        this.strength = strength;
    }

    /**
     * @return the key of this attribute
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the name of this attribute
     */
    public String getName() {
        return name;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the texture of this attribute for the inventory GUI
     */
    public String getTexture() {
        return texture;
    }

    /**
     * @return type this attribute will modify
     */
    public AttributeType getType() {
        return type;
    }

    /**
     * @return {@code true} if this attribute is instant
     */
    public boolean isInstant() {
        return instant;
    }

    /**
     * @return {@code true} if this attribute has expired
     */
    public boolean isExpired() {
        return (GameManager.getTick() - tickApplied >= duration);
    }

    /**
     * @return the strength of this attribute
     */
    public float getStrength() {
        return strength;
    }

    /**
     * Apply this attribute
     *
     * @param player the player
     */
    public void apply(PlayerSP player) {
        tickApplied = GameManager.getTick();
    }

}
