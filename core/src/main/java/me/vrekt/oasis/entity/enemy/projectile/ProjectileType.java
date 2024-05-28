package me.vrekt.oasis.entity.enemy.projectile;

/**
 * All projectile types within the game
 */
public enum ProjectileType {

    ROACH_ACID(new ProjectileData("acid_projectile", 5.0f, 7.5f));

    private final ProjectileData data;

    ProjectileType(ProjectileData data) {
        this.data = data;
    }

    public ProjectileData data() {
        return data;
    }
}
