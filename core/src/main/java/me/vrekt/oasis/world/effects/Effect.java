package me.vrekt.oasis.world.effects;

import com.badlogic.gdx.utils.Pool;
import com.google.gson.*;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.save.Savable;
import me.vrekt.oasis.utility.Pooling;

import java.lang.reflect.Type;

/**
 * Represents an effect
 * Poison, etc
 */
public final class Effect implements Pool.Poolable, Savable<Effect>, JsonDeserializer<Effect> {

    private EffectType type;
    private float strength, duration, interval, applied;

    public Effect() {
    }

    /**
     * Create a new effect
     *
     * @param type     the type
     * @param interval interval to apply
     * @param strength the strength
     * @param duration the duration
     * @return the effect
     */
    public static Effect create(EffectType type, float interval, float strength, float duration) {
        final Effect effect = Pooling.effect();
        effect.type = type;
        effect.interval = interval;
        effect.strength = strength;
        effect.duration = duration;
        return effect;
    }

    public void apply(GameEntity entity, float tick) {
        type.applyEffect(entity, strength);
        this.applied = tick;
    }

    public void apply(PlayerSP player, float tick) {
        type.applyToPlayer(player, strength);
        this.applied = tick;
    }

    /**
     * Apply without setting the applied tick
     * Useful for saves
     *
     * @param player player
     */
    public void applyPreviously(PlayerSP player) {
        type.applyToPlayer(player, strength);
    }

    public EffectType type() {
        return type;
    }

    public float strength() {
        return strength;
    }

    public float interval() {
        return interval;
    }

    public float applied() {
        return applied;
    }

    public void setApplied(float applied) {
        this.applied = applied;
    }

    /**
     * @return {@code true} if this effect is ready to be applied
     */
    public boolean ready(float lastApplied) {
        return GameManager.hasTimeElapsed(lastApplied, interval);
    }

    /**
     * @return duration in seconds
     */
    public float duration() {
        return duration;
    }

    /**
     * Free
     */
    public void free() {
        Pooling.freeEffect(this);
    }

    @Override
    public Effect save(JsonObject to, Gson gson) {
        to.addProperty("type", type.name());
        to.addProperty("strength", strength);
        to.addProperty("duration", duration);
        to.addProperty("interval", interval);
        return this;
    }

    @Override
    public Effect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();

        final EffectType type = EffectType.valueOf(object.get("type").getAsString());
        final float strength = object.get("strength").getAsFloat();
        final float duration = object.get("duration").getAsFloat();
        final float interval = object.get("interval").getAsFloat();
        final Effect effect = Effect.create(type, interval, strength, duration);
        effect.applied = applied;
        return effect;
    }

    @Override
    public void reset() {
        type = null;
        strength = 1.0f;
        interval = 1.0f;
        duration = 0.0f;
    }

}
