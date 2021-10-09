package me.vrekt.oasis.world.farm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.animation.MovingTileAnimation;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.farm.flora.Plant;
import me.vrekt.oasis.world.farm.flora.brush.OvergrownBrushPlant;
import me.vrekt.oasis.world.farm.ui.AllotmentInteractionOption;

/**
 * Represents an allotment for players to farm on.
 */
public final class FarmingAllotment {

    private final Rectangle bounds;

    // all allotments start out as over-grown.
    private AllotmentStatus status = AllotmentStatus.OVERGROWN_3;

    // texture to draw when this allotment is empty.
    private TextureRegion emptyAllotment;

    // current growing plant
    private Plant growingPlant;
    private final Vector2 center = new Vector2();

    private boolean interactingWith;
    // animation used for clearing allotments
    private MovingTileAnimation rakingAnimation;

    public FarmingAllotment(Rectangle bounds) {
        this.bounds = bounds;
    }

    /**
     * Load this allotment and get it ready for in-game use.
     */
    public void loadAllotment(Asset assets) {
        this.growingPlant = new OvergrownBrushPlant(this, assets, bounds.x, bounds.y);
        this.emptyAllotment = assets.getAtlas(Asset.PLANTS).findRegion(AllotmentStatus.EMPTY.getAsset());
        bounds.getCenter(center);

        growingPlant.setParticleEffectAt(center.x, center.y);

        // initialize the animation for this allot.
        // TODO: Is direction based, will probably need to be changed.
        this.rakingAnimation = new MovingTileAnimation(
                new Vector2(0.0f, 0.0f),
                center,
                new Vector2(bounds.x + (bounds.width - 1.5f), center.y),
                assets.get(Asset.RAKE));
    }

    public Vector2 getCenter() {
        return bounds.getCenter(center);
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

        return AllotmentInteractionOption.NONE;
    }

    public boolean isInteractingWith() {
        return interactingWith;
    }

    public void setInteractingWith(boolean interactingWith) {
        this.interactingWith = interactingWith;
    }

    // update plants or animations within the allotment
    public void update(Player player) {
        if (growingPlant != null && growingPlant.getShouldUpdate()) {
            growingPlant.update(player, rakingAnimation);
        }

        // update rake animation
        if (growingPlant instanceof OvergrownBrushPlant && growingPlant.getShouldUpdate()) {
            rakingAnimation.setSpeed(Gdx.graphics.getDeltaTime(), 0.0f);
            rakingAnimation.update();
        }

    }

    // render animations and plants
    public void render(SpriteBatch batch, float scale) {
        for (float x = bounds.x; x < (bounds.x + bounds.width); x++) {
            for (float y = bounds.y; y < (bounds.y + bounds.height); y++) {
                if (growingPlant == null) {
                    batch.draw(emptyAllotment, x, y, emptyAllotment.getRegionWidth() * scale, emptyAllotment.getRegionHeight() * scale);
                } else {
                    growingPlant.render(batch, x, y, scale);
                }
            }
        }

        // check if we are clearing an allotment, if so render the animation
        if (growingPlant instanceof OvergrownBrushPlant
                && !growingPlant.isComplete() && growingPlant.wasInteractedWith()) {
            // player is clearing field.
            rakingAnimation.render(batch, scale);
            growingPlant.renderParticleEffect(batch);
        }

    }

    public void complete() {
        this.interactingWith = false;

        if (growingPlant instanceof OvergrownBrushPlant) {
            // set this plot to empty now.
            status = AllotmentStatus.EMPTY;
            growingPlant = null;
            rakingAnimation.reset();
        }
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
        }
    }

    private void rakeAllotment(Player player) {
        if (!status.isOvergrown()
                || !(growingPlant instanceof OvergrownBrushPlant)
                || growingPlant.wasInteractedWith())
            return;

        growingPlant.interactWith(player);
    }

}
