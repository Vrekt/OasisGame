package me.vrekt.oasis.save.player;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.world.effects.Effect;
import me.vrekt.oasis.world.effects.EffectType;

/**
 * Single effect
 */
public final class EffectSave {

    @Expose
    private EffectType type;
    @Expose
    private float strength;
    @Expose
    private float duration;
    @Expose
    private float interval;
    @Expose
    private float applied;

    public EffectSave(Effect effect) {
        this.type = effect.type();
        this.strength = effect.strength();
        this.duration = effect.duration();
        this.interval = effect.interval();
        this.applied = effect.applied();
    }

    public EffectType type() {
        return type;
    }

    public float strength() {
        return strength;
    }

    public float duration() {
        return duration;
    }

    public float interval() {
        return interval;
    }

    public float applied() {
        return applied;
    }
}
