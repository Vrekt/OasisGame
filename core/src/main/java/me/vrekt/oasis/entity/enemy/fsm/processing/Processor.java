package me.vrekt.oasis.entity.enemy.fsm.processing;

@FunctionalInterface
public interface Processor {

    /**
     * Update this processor
     *
     * @param delta delta
     */
    void update(float delta);

}
