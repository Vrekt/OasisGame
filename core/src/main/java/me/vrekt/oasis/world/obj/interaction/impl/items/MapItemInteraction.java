package me.vrekt.oasis.world.obj.interaction.impl.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.save.inventory.ItemSave;
import me.vrekt.oasis.save.world.obj.WorldObjectSaveState;
import me.vrekt.oasis.utility.Pooling;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * An interaction that is an item that can be picked up
 */
public final class MapItemInteraction extends AbstractInteractableWorldObject {

    private static final float PATCH_PADDING_Y = 12f;
    private static final float PATCH_PADDING_X = 30f;
    private Item item;

    private ParticleEffectPool.PooledEffect effect;
    private final boolean isMapObject;

    public MapItemInteraction(GameWorld world, Item item, Vector2 position) {
        super(WorldInteractionType.MAP_ITEM, item.key());

        setWorldIn(world);
        setPosition(position.x, position.y);
        calculateSize(item);
        setInteractionRange(2.5f);
        enable();

        this.isMapObject = false;
        this.handleMouseState = false;
        this.isUiComponent = true;
        this.item = item;
        this.saveSerializer = true;
    }

    public MapItemInteraction() {
        super(WorldInteractionType.MAP_ITEM);

        this.isMapObject = true;
        this.handleMouseState = false;
        this.isUiComponent = true;
        this.saveSerializer = true;
    }

    private void calculateSize(Item item) {
        if (item.dropScale()) {
            setSize(item.itemWidthDropped(), item.itemHeightDropped());
        } else {
            setSize(item.sprite().getRegionWidth() * OasisGameSettings.SCALE,
                    item.sprite().getRegionHeight() * OasisGameSettings.SCALE);
        }
    }

    /**
     * @return the item
     */
    public Item item() {
        return item;
    }

    @Override
    public void load(Asset asset) {
        if (isMapObject) {
            loadMapObject();
        }

        effect = Pooling.hint();
        effect.setPosition(position.x + (size.x / 2f), position.y + (size.y / 2f));
        effect.scaleEffect(0.5f);
        effect.start();
    }

    /**
     * Load the map object
     */
    private void loadMapObject() {
        final String item = object.getProperties().get("item", null, String.class);
        if (item != null) {
            try {
                final Items items = Items.valueOf(item.toUpperCase());
                final int amount = TiledMapLoader.ofInt(object, "item_amount", 1);
                this.item = ItemRegistry.createItem(items, amount);

                calculateSize(this.item);
                setInteractionRange(4.0f);
            } catch (IllegalArgumentException exception) {
                GameLogging.exceptionThrown(this, "Failed to find the correct item for a map item object, item=%s", exception, item);
                disable();
            }
        }
    }

    @Override
    public void interact() {
        item.addToPlayer(world.player());
        Pooling.freeHint(effect);
        effect = null;

        world.removeInteraction(this);
        item = null;
        effect = null;
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (this.item == null) return;
        batch.draw(item.sprite(), position.x, position.y, size.x, size.y);

        // draw the effect on top
        effect.update(delta);
        effect.draw(batch);
    }

    @Override
    public void renderUiComponents(SpriteBatch batch, GuiManager manager, BitmapFont font, Vector3 position) {
        if (this.item == null) return;

        if (isMouseOver(world.getCursorInWorld())) {
            final float width = manager.getStringWidth(item.name()) + PATCH_PADDING_X;
            final float height = manager.getStringHeight(item.name()) + PATCH_PADDING_Y;

            font.setColor(Color.SKY);
            Styles.paddedTheme().draw(batch, position.x - width / 2f + (PATCH_PADDING_Y + size.x), position.y - (height + size.y * PATCH_PADDING_Y), width, height);
            font.draw(batch, item.name(), position.x - width / 2f + (PATCH_PADDING_Y + size.x * 6f), position.y - (height + size.y * PATCH_PADDING_Y) / 2f);
        }
    }

    @Override
    public WorldObjectSaveState save(JsonObject to, Gson gson) {
        to.add("dropped_item", gson.toJsonTree(new ItemSave(0, item)));
        return new WorldObjectSaveState(world, this, to);
    }

    @Override
    public void load(WorldObjectSaveState save, Gson gson) {
        if (save.data() != null) {
            final JsonObject parent = save.data().getAsJsonObject("dropped_item");
            final Vector2 position = gson.fromJson(save.data().get("position"), Vector2.class);
            final Items typeOf = Items.valueOf(parent.get("type").getAsString());
            final int amount = parent.get("amount").getAsInt();
            world.spawnWorldDrop(typeOf, amount, position);
        }
    }

    @Override
    public void dispose() {
        if (effect != null) {
            Pooling.freeHint(effect);
            effect = null;
        }
        item = null;
    }
}
