package me.vrekt.oasis.world.tutorial.interior.other;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;

/**
 * Lyra house
 */
public final class LyraHouse extends GameWorldInterior {

    public static final int WORLD_ID = 4;

    public LyraHouse(GameWorld parentWorld, String interiorMap, InteriorWorldType type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld, interiorMap, type, cursor, entranceBounds);
        this.worldMap = Asset.HOUSE_2;
        this.worldId = WORLD_ID;
    }

    @Override
    public void enter() {
        super.enter();

        // after we enter, bring lyra into this house to confront the player
        GameManager.executeTaskLater(() -> parentWorld.findInteractableEntity(EntityType.LYRA).transfer(this), 2);
    }

    @Override
    public float update(float delta) {
        return super.update(delta);
    }
}
