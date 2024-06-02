package me.vrekt.oasis.combat;

/**
 * Stores relevant information about damage
 */
public final class EntityDamage {

    final float damage;
    float offsetX, offsetY, fade;
    final DamageType type;

    public EntityDamage(float damage, DamageType type) {
        this.damage = damage;
        this.type = type;
        this.fade = 1.0f;
    }

}
