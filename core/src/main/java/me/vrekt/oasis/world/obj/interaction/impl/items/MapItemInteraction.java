package me.vrekt.oasis.world.obj.interaction.impl.items;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.utility.Pooling;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * An item that is inserted into the tiled map
 */
public final class MapItemInteraction extends AbstractInteractableWorldObject {

    private static final String KEY = "oasis:map_item";

    private ParticleEffectPool.PooledEffect itemEffect;
    private Item item;

    public MapItemInteraction() {
        super(WorldInteractionType.MAP_ITEM, KEY);
    }

    @Override
    public void setObject(MapObject object) {
        super.setObject(object);

        final String item = object.getProperties().get("item", null, String.class);
        if (item != null) {
            try {
                final Items items = Items.valueOf(item.toUpperCase());
                this.item = ItemRegistry.createItem(items, 1);

                setSize(this.item.sprite().getRegionWidth() * OasisGameSettings.SCALE,
                        this.item.sprite().getRegionHeight() * OasisGameSettings.SCALE
                );
                setInteractionRange(4.0f);

                itemEffect = Pooling.hint();
                itemEffect.setPosition(position.x + (size.x / 2f), position.y + (size.y / 2f));
                itemEffect.scaleEffect(0.5f);
                itemEffect.start();
            } catch (IllegalArgumentException exception) {
                GameLogging.exceptionThrown(this, "Failed to find the correct item for a map item object, item=%s", exception, item);
                this.isEnabled = false;
            }
        }
    }

    @Override
    public void interact() {
        world.player().getInventory().add(item);
        world.removeInteraction(this);
        item = null;

        Pooling.freeHint(itemEffect);
        itemEffect = null;
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (this.item == null) return;

        batch.draw(item.sprite(), position.x, position.y, size.x, size.y);
        if (itemEffect != null) {
            itemEffect.update(delta);
            itemEffect.draw(batch);
        }
    }

}
