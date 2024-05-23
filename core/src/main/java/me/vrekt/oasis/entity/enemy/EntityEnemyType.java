package me.vrekt.oasis.entity.enemy;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.world.GameWorld;

/**
 * Types of enemies
 */
public enum EntityEnemyType {

    BEETLE {
        @Override
        public EntityEnemy create(Vector2 position, OasisGame game, GameWorld world, String variety) {
            return new BlueBasementBeetle(position, game, world, BasementBeetleEnemy.BeetleVariety.valueOf(variety));
        }
    };

    public abstract EntityEnemy create(Vector2 position, OasisGame game, GameWorld world, String variety);

}
