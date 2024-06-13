package me.vrekt.oasis.utility.hints;

/**
 * Player hints
 */
public enum PlayerHints {
    DIALOG_TUTORIAL_HINT("When speaking to certain people you can type your own responses within the text-box."),
    DOOR_LOCKED_HINT("The door is locked, try a lockpick."),
    NO_MORE_LOCKPICKS("You don't have anymore lockpicks, try crafting some.");

    private final String text;

    PlayerHints(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
