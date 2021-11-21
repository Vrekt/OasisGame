package me.vrekt.oasis.world.interior.mavia;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.mavia.EntityMavia;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.world.interior.AbstractInterior;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

/**
 * Mavia's house instance.
 */
public final class MaviaHouseInterior extends AbstractInterior {

    // her spawn for entering this building.
    private final Vector2 maviaSpawn = new Vector2(0, 0);
    private EntityInteractable mavia;

    public MaviaHouseInterior(Vector2 entrance, AbstractWorld worldIn) {
        super(Interior.MAVIA_HOUSE, entrance, worldIn);

        ((EntityMavia) worldIn.getEntity(EntityNPCType.MAVIA)).setInteriorEntrance(entrance.y);
    }

    @Override
    protected void loadInteriorActions(TiledMap map) {
        super.loadInteriorActions(map);

        final MapLayer layer = map.getLayers().get("Actions");
        final RectangleMapObject object = (RectangleMapObject) layer.getObjects().get("MaviaSpawn");
        if (object == null) {
            Logging.error(this, "Failed to load spawn point for Mavia!");
            this.enterable = false;
        } else {
            maviaSpawn.set(object.getRectangle().x * GlobalGameRenderer.SCALE, object.getRectangle().y * GlobalGameRenderer.SCALE);
        }
    }

    @Override
    protected void spawnEntities(AbstractWorld worldIn) {
        mavia = worldIn.getEntity(EntityNPCType.MAVIA);
        mavia.setPosition(maviaSpawn.x, maviaSpawn.y);
        addEntityInInterior(mavia);
    }

    @Override
    public void handleInteractionKeyPressed() {
        // let this player exit if close enough to origin spawn
        if (player.getPosition().dst2(spawn) <= 1) {
            game.getGui().getDialog().hideGui();
            mavia.setSpeakingTo(false);
            this.exit();
        } else if (player.getPosition().dst2(maviaSpawn) <= 2) {
            mavia.setSpeakingTo(true);
            game.getGui().getDialog().setDialogToRender(mavia, mavia.getDialogSection(), mavia.getDisplay());
            game.getGui().getDialog().showGui();
        }
    }

    @Override
    protected void update() {
        game.getGui().updateDialogState(mavia);
    }
}
