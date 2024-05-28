package me.vrekt.oasis.entity.enemy.projectile;

/**
 * Data related to a projectile
 *
 * @param texture   their texture
 * @param moveSpeed their speed
 * @param expires   when it expires
 */
public record ProjectileData(String texture, float moveSpeed, float expires) {

}
