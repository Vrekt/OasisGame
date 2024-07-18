package me.vrekt.oasis.asset.sound;

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
    LEVER_CLICK("click_sound_1.mp3"),
    LOCKPICK_FAIL("lockpick_fail.wav"),
    OPEN_INVENTORY("open_inventory.wav"),
    TURN_PAGE("turn_page.wav"),
    TELEPORT("teleport.wav"),
    OPEN_MAGIC_BOOK("magic_book_open.wav"),
    QUEST_ADDED("quest_added.wav"),
    WALK_ON_MUD_LEFT_1("material/mud_walk_l1.ogg"),
    WALK_ON_MUD_LEFT_2("material/mud_walk_l2.ogg"),
    WALK_ON_MUD_LEFT_3("material/mud_walk_l3.ogg"),
    WALK_ON_MUD_RIGHT_1("material/walk_on_mud_r1.ogg"),
    WALK_ON_MUD_RIGHT_2("material/walk_on_mud_r2.ogg"),
    WALK_ON_MUD_RIGHT_3("material/walk_on_mud_r3.ogg"),
    WALK_ON_GRASS_LEFT_1("material/walk_on_grass_l1.ogg"),
    WALK_ON_GRASS_LEFT_2("material/walk_on_grass_l2.ogg"),
    WALK_ON_GRASS_LEFT_3("material/walk_on_grass_l3.ogg"),
    WALK_ON_GRASS_RIGHT_1("material/walk_on_grass_r1.ogg"),
    WALK_ON_GRASS_RIGHT_2("material/walk_on_grass_r2.ogg"),
    WALK_ON_GRASS_RIGHT_3("material/walk_on_grass_r3.ogg"),
    WALK_ON_DIRT_LEFT_1("material/stepdirt_1.wav"),
    WALK_ON_DIRT_LEFT_2("material/stepdirt_2.wav"),
    WALK_ON_DIRT_LEFT_3("material/stepdirt_3.wav"),
    WALK_ON_DIRT_LEFT_4("material/stepdirt_4.wav"),
    WALK_ON_DIRT_RIGHT_1("material/stepdirt_5.wav"),
    WALK_ON_DIRT_RIGHT_2("material/stepdirt_6.wav"),
    WALK_ON_DIRT_RIGHT_3("material/stepdirt_7.wav"),
    WALK_ON_DIRT_RIGHT_4("material/stepdirt_8.wav"),
    GRASSY_STEP_LEFT_1("material/grassy_step_left_1.wav"),
    GRASSY_STEP_LEFT_2("material/grassy_step_left_2.wav"),
    GRASSY_STEP_RIGHT_1("material/grassy_step_right_1.wav"),
    GRASSY_STEP_RIGHT_2("material/grassy_step_right_2.wav"),
    GLASS_BREAKING("glass_breaking.wav"),
    WALK_ON_STONE_LEFT_1("material/stonewalk_l_1.wav"),
    WALK_ON_STONE_LEFT_2("material/stonewalk_l_2.wav"),
    WALK_ON_STONE_RIGHT_1("material/stonewalk_r_1.wav"),
    WALK_ON_STONE_RIGHT_2("material/stonewalk_r_2.wav");


    private final String resource;

    Sounds(String resource) {
        this.resource = "sound/" + resource;
    }

    public String resource() {
        return resource;
    }

}
