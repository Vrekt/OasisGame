package me.vrekt.oasis.entity.enemy.projectile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Manages projectiles per entity
 */
public final class ProjectileManager {

    private final Array<Projectile> activeProjectiles = new Array<>();
    private final Pool<Projectile> pool = new Pool<>() {
        @Override
        protected Projectile newObject() {
            return new Projectile();
        }
    };


    /**
     * Spawn a projectile and shoot it
     *
     * @param type   type
     * @param origin origin
     * @param target target
     * @param result result
     */
    public void spawnProjectile(ProjectileType type,
                                Vector2 origin,
                                Vector2 target,
                                ProjectileResult result) {
        final Projectile obtained = pool.obtain();
        obtained.load(type.data(), result);

        obtained.shoot(origin, target);
        activeProjectiles.add(obtained);
    }

    /**
     * Update all activate projectiles
     *
     * @param delta delta
     */
    public void update(float delta) {
        Projectile projectile;
        for (int i = activeProjectiles.size; --i >= 0; ) {
            projectile = activeProjectiles.get(i);
            if (projectile.isExpired()) {
                projectile.pop();

                activeProjectiles.removeIndex(i);
                pool.free(projectile);
            } else {
                projectile.update(delta);
            }
        }
    }

    /**
     * Render projectiles
     *
     * @param batch batch
     * @param delta delta
     */
    public void render(SpriteBatch batch, float delta) {
        for (Projectile projectile : activeProjectiles) {
            projectile.render(batch, delta);
        }
    }

}
