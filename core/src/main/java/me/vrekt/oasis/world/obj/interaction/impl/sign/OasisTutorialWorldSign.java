package me.vrekt.oasis.world.obj.interaction.impl.sign;

/**
 * The tutorial world sign next to the dungeon entrance.
 * <p>
 * This ties in with wrynn, she lost her recipe book and decided to put a sign in "memoriam"
 * Of course, we come along and save the day.
 */
public final class OasisTutorialWorldSign extends ReadableSignInteraction {

    public static final String KEY = "oasis:tutorial_sign";

    public OasisTutorialWorldSign() {
        super(KEY, "Here lies my dignity after losing my recipe book.");
    }

}
