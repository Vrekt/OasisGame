package me.vrekt.oasis.entity.enemy.projectile;

/**
 * A projectile result (if it hit anything)
 */
public interface ProjectileResult {

    /**
     * Process the result
     *
     * @param hitTarget {@code true} if the target was hit
     */
    void result(boolean hitTarget);

}
