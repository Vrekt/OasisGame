package me.vrekt.oasis.world.interior.boss;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.wrynn.WrynnEntity;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instance;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.container.WrynnOfficeContainerInteraction;

import java.util.Random;

public final class WrynnHouseInterior extends Instance {

    private WrynnEntity wrynn;
    private final Random random;

    public WrynnHouseInterior(OasisGame game, OasisPlayer player, OasisWorld world, String name, Cursor cursor, Rectangle bounds) {
        super(game, player, world, name, cursor, bounds);
        this.enterable = true;
        random = new Random();
    }

    @Override
    protected void preLoad() {
        interactionManager.registerInteraction(WorldInteractionType.CONTAINER, "wrynn:container", WrynnOfficeContainerInteraction::new);
    }

    @Override
    public void enter(boolean setScreen) {
        super.enter(setScreen);
        wrynn = (WrynnEntity) getEntityByType(EntityNPCType.WRYNN);
        paths.forEach(vector2 -> wrynn.getArrivalComponent().addArrivalPoint(vector2));
    }

}