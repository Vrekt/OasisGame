package me.vrekt.oasis.entity.enemy.fsm.processing;

@FunctionalInterface
public interface ProcessingRequirement {

    /**
     * @return {@code true} if the processor should process.
     */
    boolean shouldProcess();
}
