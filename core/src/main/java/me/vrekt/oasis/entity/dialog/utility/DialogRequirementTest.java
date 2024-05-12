package me.vrekt.oasis.entity.dialog.utility;

import java.util.function.Supplier;

/**
 * Used for listeners when needing to update game tests
 * Such as player having an item before continuing the dialog.
 */
public final class DialogRequirementTest {

    private final Supplier<Boolean> test;
    private final Runnable success;
    private boolean wasSuccessful;

    public DialogRequirementTest(Supplier<Boolean> test, Runnable success) {
        this.test = test;
        this.success = success;
    }

    /**
     * Only test the condition if we haven't before.
     */
    public void test() {
        if (test.get() && !wasSuccessful) {
            wasSuccessful = true;
            success.run();
        }
    }

}
