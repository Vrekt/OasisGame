package me.vrekt.oasis.entity.enemy;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.world.GameWorld;

/**
 * A blue beetle variety.
 */
public final class BlueBasementBeetle extends BasementBeetleEnemy {

    private static final float ATTACK_SPEED = 1f;
    private static final float HOSTILE_RANGE = 2.85f;
    private static final float INACCURACY = 0.1f;
    private static final float STRENGTH = 0.5f;

    public BlueBasementBeetle(Vector2 position, OasisGame game, GameWorld world, BeetleVariety variety) {
        super(position, game, world, variety);
        setName("Blue Basement Beetle");

        attackSpeed = ATTACK_SPEED;
        hostileRange = HOSTILE_RANGE;
        inaccuracy = INACCURACY;
        attackStrength = STRENGTH;
    }

}
