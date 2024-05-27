package me.vrekt.oasis.entity.enemy;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.enemy.easy.GrungyRoachEnemy;
import me.vrekt.oasis.world.GameWorld;

/**
 * Types of enemies
 */
public enum EntityEnemyType {

    ROACH {
        @Override
        public EntityEnemy create(Vector2 position, OasisGame game, GameWorld world, String variety) {
            return new GrungyRoachEnemy(position, world, game);
        }
    };

    public abstract EntityEnemy create(Vector2 position, OasisGame game, GameWorld world, String variety);

}
