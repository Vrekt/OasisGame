package me.vrekt.oasis.world.farm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.asset.WorldAsset;
import me.vrekt.oasis.world.farm.flora.Plant;
import me.vrekt.oasis.world.farm.flora.brush.OvergrownBrushPlant;
import me.vrekt.oasis.world.farm.ui.AllotmentInteractionOption;

/**
 * Represents an allotment for players to farm on.
 */
public final class FarmingAllotment {

    private final Rectangle bounds;

    private WorldAsset assets;
    // all allotments start out as over-grown.
    private AllotmentStatus status = AllotmentStatus.OVERGROWN_3;

    // texture to draw when this allotment is empty.
    private Texture emptyAllotment;

    // current growing plant
    private Plant growingPlant;
    private Vector2 center = new Vector2();

    private boolean interactingWith;

    public FarmingAllotment(Rectangle bounds) {
        this.bounds = bounds;
    }

    /**
     * Load this allotment and get it ready for in-game use.
     */
    public void loadAllotment(WorldAsset assets) {
        this.assets = assets;

        this.growingPlant = new OvergrownBrushPlant(this, assets, bounds.x, bounds.y);
        this.emptyAllotment = assets.getTexture(AllotmentStatus.EMPTY.getAsset());
    }

    public Vector2 getCenter() {
        return bounds.getCenter(center);
    }

    public boolean isInteractingWith() {
        return interactingWith;
    }

    public void setInteractingWith(boolean interactingWith) {
        this.interactingWith = interactingWith;
    }

    /**
     * Update the allotment
     */
    public void update(Player player) {
        if (growingPlant != null && growingPlant.getShouldUpdate()) {
            growingPlant.update(player);
        }
    }

    public void render(SpriteBatch batch, float scale) {
        for (float x = bounds.x; x < (bounds.x + bounds.width); x++) {
            for (float y = bounds.y; y < (bounds.y + bounds.height); y++) {
                if (growingPlant == null) {
                    // plot is empty.
                    batch.draw(emptyAllotment, x, y, emptyAllotment.getWidth() * scale, emptyAllotment.getHeight() * scale);
                } else {
                    growingPlant.render(batch, x, y, scale);
                }
            }
        }
    }

    public void complete() {
        this.interactingWith = false;

        if (growingPlant instanceof OvergrownBrushPlant) {
            // set this plot to empty now.
            status = AllotmentStatus.EMPTY;
            growingPlant = null;
        }
    }

    /**
     * Retrieve the current valid interaction
     *
     * @return the interaction
     */
    public AllotmentInteractionOption getInteraction() {
        if (status.isOvergrown()) {
            return AllotmentInteractionOption.RAKE;
        }

        return AllotmentInteractionOption.PLANT;
    }

    /**
     * Interact with this allotment
     *
     * @param option the option chosen
     * @param player local player
     */
    public void interact(AllotmentInteractionOption option, Player player) {
        this.interactingWith = true;

        switch (option) {
            case RAKE:
                rakeAllotment(player);
                break;
            case PLANT:
                break;
        }
    }

    private void rakeAllotment(Player player) {
        if (!status.isOvergrown()
                || !(growingPlant instanceof OvergrownBrushPlant)
                || growingPlant.wasInteractedWith())
            return;

        final OvergrownBrushPlant plant = (OvergrownBrushPlant) growingPlant;
        plant.interactWith(player);
    }

}
