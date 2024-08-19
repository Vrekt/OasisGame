package me.vrekt.oasis.world.tutorial.interior.other;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.gui.input.Cursor;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;
import me.vrekt.oasis.world.interior.Interior;

/**
 * Lyra house
 */
public final class LyraHouse extends GameWorldInterior {

    public static final int WORLD_ID = 4;

    public LyraHouse(GameWorld parentWorld, String interiorMap, Interior type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld, interiorMap, type, cursor, entranceBounds);
        this.worldMap = Asset.HOUSE_2;
        this.worldId = WORLD_ID;
    }

    @Override
    public void enterWorld() {
        super.enterWorld();

        // after we enter, bring lyra into this house to confront the player
        if (findEntity(EntityType.LYRA) == null) {
            game.tasks().schedule(() -> {
                parentWorld.findInteractableEntity(EntityType.LYRA)
                        .transfer(this);
            }, 2);
        }
    }

    @Override
    public float tickWorldPhysicsSim(float delta) {
        return super.tickWorldPhysicsSim(delta);
    }
}
