package me.vrekt.oasis.world.farm;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.ui.world.WorldGui;
import me.vrekt.oasis.world.farm.ui.AllotmentInteractionOption;

/**
 * Handles most aspects of the farming system
 */
public final class FarmAllotmentManager {

    private final Array<FarmingAllotment> allotments;

    private final Player thePlayer;
    private final WorldGui gui;

    // current allotment near or interacting with
    private AllotmentInteractionOption interaction;
    private FarmingAllotment allotment;

    public FarmAllotmentManager(Array<FarmingAllotment> allotments, Player thePlayer, WorldGui gui) {
        this.allotments = allotments;
        this.thePlayer = thePlayer;
        this.gui = gui;
    }

    public void update() {
        if (this.allotment != null) {
            // ensure we are still close to this allotment.
            if (thePlayer.getPosition().dst2(allotment.getCenter()) >= 11.5) {
                reset(); // player invalidated the interaction by moving to far
            }
        } else {
            final FarmingAllotment allotment = getClosestAllotment();
            if (allotment != null && !allotment.isInteractingWith()) {
                this.allotment = allotment;
                interaction = allotment.getInteraction();

                if (interaction == AllotmentInteractionOption.RAKE)
                    gui.getInteractions().show("Press [E] to rake allotment.");
            }
        }

        for (FarmingAllotment allotment : allotments) allotment.update(thePlayer);
    }


    /**
     * Render all allotments
     *
     * @param batch      batch
     * @param worldScale scale
     */
    public void render(SpriteBatch batch, float worldScale, Camera camera) {
        for (FarmingAllotment allotment : allotments) {
            if (allotment.isInView(camera)) allotment.render(batch, worldScale);
        }
    }

    /**
     * Interact with the current allotment
     */
    public void interact() {
        if (allotment != null && interaction != null) {
            allotment.interact(interaction, thePlayer);
            reset();
        }
    }

    /**
     * Retrieve the closest allotment to the player
     *
     * @return the allotment or {@code null} if none
     */
    private FarmingAllotment getClosestAllotment() {
        for (FarmingAllotment allotment : allotments) {
            if (thePlayer.getPosition().dst2(allotment.getCenter()) <= 11) {
                return allotment;
            }
        }
        return null;
    }

    /**
     * Reset interaction
     */
    private void reset() {
        gui.getInteractions().hide();
        allotment = null;
        interaction = null;
    }

}
