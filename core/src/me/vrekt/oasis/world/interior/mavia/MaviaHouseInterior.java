package me.vrekt.oasis.world.interior.mavia;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.mavia.EntityMavia;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.inventory.container.ChestInventory;
import me.vrekt.oasis.item.items.weapons.PrototypeTimepiercerWeapon;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.interior.AbstractInterior;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

/**
 * Mavia's house instance.
 */
public final class MaviaHouseInterior extends AbstractInterior {

    // her spawn for entering this building.
    private final Vector2 maviaSpawn = new Vector2(0, 0);
    private final Vector2 chestLocation = new Vector2(0, 0);
    private final Vector2 hintLocation = new Vector2(0, 0);
    private EntityInteractable mavia;

    private final ChestInventory inventory;
    private boolean chestUnlocked, hint;

    private TextureRegion hintTexture;
    private float hintWidth = 64, hintHeight = 64;

    public MaviaHouseInterior(Vector2 entrance, AbstractWorld worldIn) {
        super(Interior.MAVIA_HOUSE, entrance, worldIn);

        ((EntityMavia) worldIn.getEntity(EntityNPCType.MAVIA)).setInteriorEntrance(entrance.y);
        this.inventory = new ChestInventory("Mavia's Gift", 12);
    }

    @Override
    public boolean enterInstance(Asset asset, AbstractWorld worldIn, OasisGame game, GlobalGameRenderer renderer, Player thePlayer) {
        this.inventory.giveItem(new PrototypeTimepiercerWeapon(asset));
        this.hintTexture = asset.getAssets().findRegion("hint");
        return super.enterInstance(asset, worldIn, game, renderer, thePlayer);
    }

    @Override
    protected void loadInteriorActions(TiledMap map) {
        super.loadInteriorActions(map);

        final MapLayer layer = map.getLayers().get("Actions");
        final RectangleMapObject object = (RectangleMapObject) layer.getObjects().get("MaviaSpawn");
        final RectangleMapObject co = (RectangleMapObject) layer.getObjects().get("Chest");
        if (object == null) {
            Logging.error(this, "Failed to load spawn point for Mavia!");
            this.enterable = false;
            return;
        } else {
            maviaSpawn.set(object.getRectangle().x * GlobalGameRenderer.SCALE, object.getRectangle().y * GlobalGameRenderer.SCALE);
        }

        if (co == null) {
            Logging.error(this, "Failed to load chest point for Mavia!");
            this.enterable = false;
        } else {
            chestLocation.set(co.getRectangle().x * GlobalGameRenderer.SCALE, co.getRectangle().y * GlobalGameRenderer.SCALE);
            hintLocation.set(chestLocation.x + (16 * (1 / 16.0f)) / 2f, chestLocation.y + (16 * (1 / 16.0f)) / 2f);
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
            game.getGui().hideGui(GuiType.DIALOG);
            mavia.setSpeakingTo(false);
            this.exit();
        } else if (player.getPosition().dst2(maviaSpawn) <= 2) {
            mavia.setSpeakingTo(true);
            game.getGui().showEntityDialog(mavia);
            game.getGui().showGui(GuiType.QUEST);
        } else if (player.getPosition().dst2(chestLocation) <= 1.6f && chestUnlocked) {
            setShowHint(false);
        }
    }

    @Override
    protected void render(SpriteBatch batch) {
        if (hint) {
            renderer.getBatch().draw(hintTexture, hintLocation.x, hintLocation.y, hintWidth * GlobalGameRenderer.SCALE,
                    hintHeight * GlobalGameRenderer.SCALE);
        }
    }

    @Override
    protected void update() {
        game.getGui().updateDialogState(mavia);

        if (hintWidth > 8 && hint) {
            hintWidth -= 0.1f;
            hintHeight -= 0.1f;
        }
    }

    public void setShowHint(boolean hint) {
        this.hint = hint;
    }

    public void setChestUnlocked(boolean chestUnlocked) {
        this.chestUnlocked = chestUnlocked;
    }
}
