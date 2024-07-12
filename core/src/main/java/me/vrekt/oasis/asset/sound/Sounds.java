package me.vrekt.oasis.asset.sound;

import me.vrekt.oasis.utility.logging.GameLogging;

/**
 * All sounds
 */
public enum Sounds {

    NONE("null"),
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
    TELEPORT("teleport.wav"),
    OPEN_MAGIC_BOOK("magic_book_open.wav"),
    QUEST_ADDED("quest_added.wav"),
    WALK_ON_MUD_LEFT_1("mud_walk_l1.ogg"),
    WALK_ON_MUD_LEFT_2("mud_walk_l2.ogg"),
    WALK_ON_MUD_LEFT_3("mud_walk_l3.ogg"),
    WALK_ON_MUD_RIGHT_1("walk_on_mud_r1.ogg"),
    WALK_ON_MUD_RIGHT_2("walk_on_mud_r2.ogg"),
    WALK_ON_MUD_RIGHT_3("walk_on_mud_r3.ogg"),
    WALK_ON_GRASS_LEFT_1("walk_on_grass_l1.ogg"),
    WALK_ON_GRASS_LEFT_2("walk_on_grass_l2.ogg"),
    WALK_ON_GRASS_LEFT_3("walk_on_grass_l3.ogg"),
    WALK_ON_GRASS_RIGHT_1("walk_on_grass_r1.ogg"),
    WALK_ON_GRASS_RIGHT_2("walk_on_grass_r2.ogg"),
    WALK_ON_GRASS_RIGHT_3("walk_on_grass_r3.ogg");

    private final String resource;

    Sounds(String resource) {
        this.resource = "sound/" + resource;
    }

    public String resource() {
        return resource;
    }

    public static Sounds of(String resource) {
        try {
            return valueOf(resource.toUpperCase());
        } catch (IllegalArgumentException exception) {
            GameLogging.warn("Sounds", "Failed to find the tile sound for %s", resource);
            return NONE;
        }
    }

}
