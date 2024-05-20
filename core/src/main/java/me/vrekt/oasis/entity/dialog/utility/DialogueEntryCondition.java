package me.vrekt.oasis.entity.dialog.utility;

/**
 * Tests a condition for a specific dialog entry.
 */
@FunctionalInterface
public interface DialogueEntryCondition {

    /**
     * Test the condition.
     *
     * @return the result
     */
    boolean test();

}
