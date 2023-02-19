package me.vrekt.oasis.gui.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.inventory.renderer.HudInventoryHandler;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.item.Item;

/**
 * Hud GUI includes on screen inventory, class, mini-map, etc.
 */
public final class HudGui extends Gui {

    private final Table rootTable;
    private final Table classIconTable;
    private final Table notificationTable;
    private final Table warningTable;
    // rendering of hud inventory
    private final HudInventoryHandler inventoryRenderer;

    // amount of item collected + icon image of item
    private final Label amountLabel, fpsLabel;
    private final Image iconImage, classImage;

    // last hint popped up
    private long lastHint, lastWarning;

    public HudGui(GameGui gui, Asset asset) {
        super(gui, asset);
        OasisPlayerSP player = gui.getGame().getPlayer();

        rootTable = new Table();
        rootTable.setVisible(true);
        rootTable.left();

        // info: FPS
        final Table fpsTable = new Table();
        fpsTable.setVisible(true);
        fpsTable.top().left();
        fpsTable.add(fpsLabel = new Label("FPS: ", new Label.LabelStyle(asset.getMedium(), Color.WHITE)));

        // info: missing item warning
        warningTable = new Table();
        warningTable.setVisible(false);
        warningTable.bottom().padBottom(16);

        final Table warning = new Table();
        warning.setBackground(new TextureRegionDrawable(asset.get("warning_display")));
        warning.add(new Label("Missing required item!", new Label.LabelStyle(asset.getMedium(), Color.BLACK))).padLeft(28);
        warningTable.add(warning);

        // info: the table holding player class information
        classIconTable = new Table();
        classIconTable.setVisible(true);
        classIconTable.left();
        classIconTable.add(classImage = new Image(asset.get("nature_class"))).size(48, 48);

        // info: notifications
        notificationTable = new Table();
        notificationTable.setVisible(false);
        notificationTable.top().right();

        final Table notification = new Table();
        notification.setBackground(new TextureRegionDrawable(asset.get("hint_dropdown")));
        notification.add(new Label("You received ", gui.getSkin(), "medium", Color.DARK_GRAY));
        notification.add(iconImage = new Image()).padBottom(6).padTop(6).padRight(2);
        notification.add(new Label("x", gui.getSkin(), "small", Color.DARK_GRAY));
        notification.add(amountLabel = new Label("1", gui.getSkin(), "medium", Color.DARK_GRAY));
        notificationTable.add(notification).size(256, 32);

        // info: init all containers
        gui.createContainer(rootTable).bottom();
        gui.createContainer(fpsTable).top().left();
        gui.createContainer(warningTable).bottom().padBottom(58);
        gui.createContainer(classIconTable).bottom().left().pad(8);
        gui.createContainer(notificationTable).top().right().pad(4);

        // info: the actual table that holds the inventory slots
        final Table inventory = new Table();

        // initialize HUD inventory.
        final TextureRegionDrawable slot = new TextureRegionDrawable(asset.get("inventory_slot"));
        this.inventoryRenderer = new HudInventoryHandler(player.getInventory().getInventorySize(), player);
        this.inventoryRenderer.initialize(inventory, slot);

        rootTable.add(inventory).padBottom(8);
    }

    @Override
    public void update() {
        // fade notification table
        if (notificationTable.getColor().a == 1 && (System.currentTimeMillis() - lastHint >= 2500))
            notificationTable.addAction(Actions.fadeOut(1f));

        // fade warning table
        if (warningTable.getColor().a == 1 && (System.currentTimeMillis() - lastWarning) >= 1500)
            warningTable.addAction(Actions.fadeOut(1));

        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        inventoryRenderer.update();
    }

    /**
     * Set the class icon for the HUD
     *
     * @param icon the icon string from assets
     */
    public void setClassIcon(String icon) {
        classImage.setDrawable(new TextureRegionDrawable(asset.get(icon)));
    }

    /**
     * Show a warning displaying an item is required
     */
    public void showMissingItemWarning() {
        if (System.currentTimeMillis() - lastWarning <= 2000) {
            return;
        }

        lastWarning = System.currentTimeMillis();
        warningTable.setVisible(true);
        warningTable.getColor().a = 0;
        warningTable.addAction(Actions.fadeIn(1));
    }

    /**
     * Display an item has been collected via a notification.
     * Used mainly for quests and tutorial purposes.
     *
     * @param item the item
     */
    public void showItemCollected(Item item) {
        if (System.currentTimeMillis() - lastHint <= 2500) {
            return;
        }

        notificationTable.setVisible(true);
        notificationTable.getColor().a = 0;
        notificationTable.addAction(Actions.fadeIn(1f));

        iconImage.setDrawable(new TextureRegionDrawable(item.getTexture()));
        amountLabel.setText("" + item.getAmount());
        lastHint = System.currentTimeMillis();
    }

    @Override
    public void show() {
        rootTable.setVisible(true);
        classIconTable.setVisible(true);
        isShowing = true;
    }

    @Override
    public void hide() {
        rootTable.setVisible(false);
        classIconTable.setVisible(false);
        warningTable.setVisible(false);
        notificationTable.setVisible(false);
        isShowing = false;
    }

}
