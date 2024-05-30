package me.vrekt.oasis.world.interior.wrynn;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.npc.wrynn.WrynnEntity;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.container.WrynnOfficeContainerInteraction;

/**
 * Wrynn tutorial house.
 */
public final class WrynnHouseInterior extends GameWorldInterior {

    private WrynnEntity wrynn;

    public WrynnHouseInterior(GameWorld parentWorld, String interiorMap, InteriorWorldType type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld, interiorMap, type, cursor, entranceBounds);

        getConfiguration().worldScale = OasisGameSettings.SCALE;
        getConfiguration().handlePhysics = true;
        getConfiguration().updateEntityEngine = true;
        getConfiguration().updateEntities = false;
        getConfiguration().updateNetworkPlayers = true;
    }

    @Override
    protected void preLoad() {
        interactionManager.registerInteraction(WorldInteractionType.CONTAINER, "wrynn:container", WrynnOfficeContainerInteraction::new);
    }

    @Override
    public void enter() {
        super.enter();
        player.getQuestManager().advanceQuest(QuestType.A_NEW_HORIZON);
        guiManager.getHudComponent().clearHints();
    }

}