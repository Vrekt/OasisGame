package me.vrekt.oasis.asset.sound;

/**
 * All sounds
 */
public enum Sounds {

    BUTTON_HOVER("hover_1.wav"),
    FISH_SPLASHING("fish_splashing.ogg");

    private final String resource;
    Sounds(String resource) {
        this.resource = "sound/" + resource;
    }

    public String resource() {
        return resource;
    }
}
