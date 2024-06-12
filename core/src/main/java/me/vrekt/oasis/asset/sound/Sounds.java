package me.vrekt.oasis.asset.sound;

/**
 * All sounds
 */
public enum Sounds {

    BUTTON_HOVER("hover_1.wav"),
    FISH_SPLASHING("fish_splashing.ogg"),
    LOCK_CLICK("lock_click_1.wav"),
    LOCK_SUCCESS("lock_success.wav");

    private final String resource;
    Sounds(String resource) {
        this.resource = "sound/" + resource;
    }

    public String resource() {
        return resource;
    }
}
