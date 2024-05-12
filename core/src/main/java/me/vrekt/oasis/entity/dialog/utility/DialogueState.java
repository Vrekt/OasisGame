package me.vrekt.oasis.entity.dialog.utility;

/**
 * A dialogue result.
 */
public enum DialogueState {

    /**
     * The dialog continues.
     */
    CONTINUED,
    /**
     * The dialog is finished
     */
    FINISHED,
    /**
     * Entity is waiting for an action to be completed before continuing
     */
    INCOMPLETE,

}
