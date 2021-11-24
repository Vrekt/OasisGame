package me.vrekt.oasis.entity.mob;

import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * Represents an enemy.
 */
public abstract class EntityMob extends Entity {

    protected int level;

    public EntityMob(String name, float x, float y, OasisGame game, AbstractWorld worldIn) {
        super(name, x, y, game, worldIn);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
