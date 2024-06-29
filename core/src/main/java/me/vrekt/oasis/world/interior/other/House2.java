package me.vrekt.oasis.world.interior.other;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.items.MapItemInteraction;

public final class House2 extends GameWorldInterior {

    public House2(GameWorld parentWorld, String interiorMap, InteriorWorldType type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld, interiorMap, type, cursor, entranceBounds);
        this.worldMap = Asset.HOUSE_2;
    }

    @Override
    protected void init() {
        interactionManager.registerInteraction(WorldInteractionType.MAP_ITEM, "oasis:map_item", MapItemInteraction::new);
    }
}
