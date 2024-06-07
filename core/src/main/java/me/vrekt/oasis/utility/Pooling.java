package me.vrekt.oasis.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.enemy.projectile.Projectile;
import me.vrekt.oasis.world.effects.AreaEffectCloud;
import me.vrekt.oasis.world.effects.Effect;

/**
 * Keeps track of the various pools.
 */
public final class Pooling {

    private static final Pool<Effect> EFFECT_POOL = new Pool<>() {
        @Override
        protected Effect newObject() {
            return new Effect();
        }
    };

    private static final Pool<Projectile> PROJECTILE_POOL = new Pool<>() {
        @Override
        protected Projectile newObject() {
            return new Projectile();
        }
    };

    private static final Pool<AreaEffectCloud> AREA_EFFECT_CLOUD_POOL = new Pool<>() {
        @Override
        protected AreaEffectCloud newObject() {
            return new AreaEffectCloud();
        }
    };

    private static ParticleEffectPool itemDropHintPool;

    /**
     * Init
     *
     * @param asset asset
     */
    public static void init(Asset asset) {
        final ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal("world/asset/particles/pick_up_hint.p"), asset.getAtlasAssets());
        // watch this, 20 may not be enough depending on how complex the game gets. 6-6-2024
        itemDropHintPool = new ParticleEffectPool(effect, 6, 20);
    }

    public static Effect effect() {
        return EFFECT_POOL.obtain();
    }

    public static void freeEffect(Effect effect) {
        EFFECT_POOL.free(effect);
    }

    public static Projectile projectile() {
        return PROJECTILE_POOL.obtain();
    }

    public static void freeProjectile(Projectile projectile) {
        PROJECTILE_POOL.free(projectile);
    }

    public static AreaEffectCloud effectCloud() {
        return AREA_EFFECT_CLOUD_POOL.obtain();
    }

    public static void freeEffectCloud(AreaEffectCloud effectCloud) {
        AREA_EFFECT_CLOUD_POOL.free(effectCloud);
    }

    public static ParticleEffectPool.PooledEffect hint() {
        return itemDropHintPool.obtain();
    }

    public static void freeHint(ParticleEffectPool.PooledEffect effect) {
        itemDropHintPool.free(effect);
    }

}
