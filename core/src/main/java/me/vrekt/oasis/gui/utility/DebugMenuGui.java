package me.vrekt.oasis.gui.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.utility.logging.GameLogging;

/**
 * Debug menu for helping develop the game
 */
public final class DebugMenuGui extends Gui {

    private final VisTable rootTable;
    private final VisLabel memoryUsed, freeMemory, tickTime;

    public DebugMenuGui(GameGui gui, Asset asset) {
        super(gui, asset, "debugMenu");

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(asset.get("pause")));
        TableUtils.setSpacingDefaults(rootTable);

        final VisTable primary = new VisTable();

        memoryUsed = new VisLabel("Memory used: ", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        freeMemory = new VisLabel("Free memory: ", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        tickTime = new VisLabel("Server Tick: " + new Label.LabelStyle(gui.getMedium(), Color.WHITE));

        final VisTextField keyInputField = new VisTextField("");
        final VisTextField amountField = new VisTextField("1");
        final VisTextButton spawnButton = new VisTextButton("Spawn Item");
        final VisCheckBox equipItemCheck = new VisCheckBox("Equip Item?");

        primary.add(memoryUsed);
        primary.row();
        primary.add(freeMemory);
        primary.row();
        primary.add(tickTime);
        primary.row();
        primary.add(new VisLabel("Spawn Item By Key: ", new Label.LabelStyle(gui.getMedium(), Color.WHITE)));
        primary.add(keyInputField);
        primary.row();
        primary.add(amountField);
        primary.row();
        primary.add(spawnButton);
        primary.add(equipItemCheck);
        primary.row();

        spawnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {

                    int amount;
                    if (amountField.getText().isEmpty()) {
                        amount = 1;
                    } else {
                        amount = Integer.parseInt(amountField.getText());
                    }

                    if (ItemRegistry.doesItemExist(keyInputField.getText())) {
                        final Item item = ItemRegistry.createItem(keyInputField.getText());
                        item.setAmount(amount);
                        item.load(asset);

                        GameManager.getPlayer().getInventory().addItem(item);
                        if (equipItemCheck.isChecked() && item instanceof ItemWeapon) {
                            GameManager.getPlayer().equipItem((ItemWeapon) item);
                        }
                        GameLogging.info(this, "Spawned new item: " + item.getItemName());
                    } else {
                        GameLogging.error(this, "No item found: " + keyInputField.getText());
                    }
                } catch (NumberFormatException exception) {
                    exception.printStackTrace();
                }
            }
        });

        final VisLabel back = new VisLabel("<- Back", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                back.setColor(Color.LIGHT_GRAY);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                back.setColor(Color.WHITE);
            }

        });

        primary.add(back);
        rootTable.add(primary);

        gui.createContainer(rootTable).fill();
    }

    @Override
    public void update() {
        memoryUsed.setText("Memory used: " + formatSize(Gdx.app.getJavaHeap()));
        freeMemory.setText("Free memory: " + formatSize(Runtime.getRuntime().freeMemory()));
        tickTime.setText("Server Tick: " + GameManager.getOasis().getServer().getGameServer().getWorldTickTime() + "ms");
    }

    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
    }
}
