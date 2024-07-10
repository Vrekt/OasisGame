package me.vrekt.oasis.asset.sound;

/**
 * All sounds
 */
public enum Sounds {

    BUTTON_HOVER("hover_1.wav"),
    FISH_SPLASHING("fish_splashing.ogg"),
    LOCK_CLICK("lock_click_1.wav"),
    LOCK_SUCCESS("lock_success.wav"),
    LOCKPICK_UNLOCK("lockpick_unlocked.wav"),
    DOOR_LOCKED("door_locked.wav"),
    LOCKPICK_BREAK("lockpick_break.wav"),
    QUEST_COMPLETED("quest_completed.wav"),
    POT_BREAKING("pot_breaking.ogg"),
    BARREL_BREAKING("barrel_breaking.wav"),
    GLASS_BREAKING("glass_breaking.ogg"),
    LEVER_CLICK("click_sound_1.mp3"),
    LOCKPICK_FAIL("lockpick_fail.wav"),
    OPEN_INVENTORY("open_inventory.wav"),
    TURN_PAGE("turn_page.wav"),
    TELEPORT("teleport.wav");

    private final String resource;
    Sounds(String resource) {
        this.resource = "sound/" + resource;
    }

    public String resource() {
        return resource;
    }
}
