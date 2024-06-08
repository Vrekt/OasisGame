package me.vrekt.oasis.asset.sound;

/**
 * All sounds
 */
public enum Sounds {

    BUTTON_HOVER("hover_1.wav");

    private final String resource;
    Sounds(String resource) {
        this.resource = "sound/" + resource;
    }

    public String resource() {
        return resource;
    }
}
