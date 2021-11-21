package me.vrekt.oasis.gui.book.pages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.gui.book.BookPage;
import org.apache.commons.text.WordUtils;

/**
 * The players inventory page UI.
 * <p>
 * TODO: Streamline inventory
 */
public final class InventoryBookPage extends BookPage {

    private GlyphLayout layout;
    private Player player;

    private boolean itemsInitialized;
    private String itemName, itemInformation;

    public InventoryBookPage(OasisGame game, TextureAtlas atlas) {
        super(1);
        this.player = game.thePlayer;
        this.currentTabTexture = atlas.findRegion("book_tab", 2);
    }

    @Override
    public void render(Batch batch, BitmapFont font, float x, float y) {
        if (!itemsInitialized) initializeItemLocations(x, y);
        if (layout == null) layout = new GlyphLayout(font, "");
        font.setColor(Color.BLACK);

        y -= marginY * .6;

        if (itemInformation != null) {
            layout.setText(font, itemName);
            font.draw(batch, itemName, x + innerMargin + ((pageSize) - layout.width), y);

            y -= ((marginY * .3f) + layout.height);
            layout.setText(font, itemInformation);
            font.draw(batch, itemInformation, x + innerMargin + ((pageSize) - layout.width), y);
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (player.getInventory().hasItemAt(i)) {
                player.getInventory().getItemAt(i, (item, location) -> {
                    final float w = item.getTexture().getRegionWidth() * 1.5f;
                    final float h = item.getTexture().getRegionHeight() * 1.5f;
                    batch.draw(item.getTexture(), location.x + (location.width - w) / 2f, location.y + (location.height - h) / 2f, w, h);
                    if (item.getAmount() > 1) {
                        layout.setText(font, item.getAmount() + "");
                        font.draw(batch, item.getAmount() + "", location.x + (location.width - (layout.width / 2f)), location.y + (layout.height / 2f));
                    }
                });
            }
        }

    }

    private void initializeItemLocations(float x, float y) {
        this.itemsInitialized = true;
        x += inventoryMarginX;
        y -= inventoryMarginY;

        final float start = x;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            final Item item = player.getInventory().getSlotItems().get(i);
            if (item != null) {
                final float w = item.getTexture().getRegionWidth() * 1.5f;
                final float h = item.getTexture().getRegionHeight() * 1.5f;

                player.getInventory().setItemLocation(i, new Rectangle(x, y - h, w, h));
            } else {
                player.getInventory().setItemLocation(i, new Rectangle(x, y - 16 * 1.5f, 16 * 1.5f, 16 * 1.5f));
            }

            x += 24 * 1.5f;

            // new line in inventory
            if (i == 2 || i == 5 || i == 8 || i == 11 || i == 14 || i == 17) {
                y -= 25 * 1.5f;
                x = start;
            }
        }

    }

    @Override
    public void hide() {

    }

    @Override
    public void handleClick(float x, float y) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (player.getInventory().hasItemAt(i)) {
                player.getInventory().getItemAt(i, (item, location) -> {
                    if (location.contains(x, y)) {
                        // player clicked on this item
                        itemName = item.getName();
                        itemInformation = WordUtils.wrap(item.getDescription(), 15);
                    }
                });
            }
        }
    }

}
