package me.vrekt.oasis.world.obj.interaction.impl.sign;

/**
 * The tutorial world sign next to the dungeon entrance.
 */
public final class OasisTutorialWorldSign extends ReadableSignInteraction {

    public static final String KEY = "oasis:tutorial_sign";

    public OasisTutorialWorldSign() {
        super(KEY, "Don't worry, nothing scary going on down here.");
    }

}
