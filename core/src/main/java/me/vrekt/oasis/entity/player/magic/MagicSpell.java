package me.vrekt.oasis.entity.player.magic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.ResourceLoader;

/**
 * Represents a base magic spell
 */
public abstract class MagicSpell implements ResourceLoader {

    protected final String name;
    protected final String description;
    protected final float cooldown;

    protected TextureRegion icon;
    protected boolean isUnlocked = true;

    public MagicSpell(String name, String description, float cooldown) {
        this.name = name;
        this.description = description;
        this.cooldown = cooldown;
    }

    /**
     * @return name
     */
    public String name() {
        return name;
    }

    /**
     * @return description
     */
    public String description() {
        return description;
    }

    /**
     * @return cooldown time in seconds
     */
    public float cooldown() {
        return cooldown;
    }

    public abstract void cast(PlayerSP player);

    /**
     * @return icon
     */
    public TextureRegion icon() {
        return icon;
    }

    /**
     * @return {@code true} if this spell is unlocked
     */
    public boolean isUnlocked() {
        return isUnlocked;
    }

    public boolean isCastable(PlayerSP player) {
        return true;
    }
}
