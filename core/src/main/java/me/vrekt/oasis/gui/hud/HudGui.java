package me.vrekt.oasis.gui.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.inventory.renderer.InventoryUiHandler;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.item.Item;

/**
 * Hud GUI includes on screen inventory, class, mini-map, etc.
 */
public final class HudGui extends Gui {

    private final Table rootTable, classSelectorTable, notificationTable, fpsTable;
    private final Table interactionTable;
    // rendering of hud inventory
    private final InventoryUiHandler inventoryRenderer;

    // amount of item collected + icon image of item
    private final Label amountLabel, fpsLabel;
    private final Image iconImage, classImage;

    // last hint popped up
    private long lastHint;

    public HudGui(GameGui gui, Asset asset) {
        super(gui, asset);
        OasisPlayerSP player = gui.getGame().getPlayer();

        fpsTable = new Table();
        fpsTable.setVisible(true);
        fpsTable.top().left();

        // FPS Information
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = asset.getMedium();
        style.fontColor = Color.WHITE;
        fpsTable.add(fpsLabel = new Label("FPS: ", style));

        rootTable = new Table();
        rootTable.setVisible(true);
        rootTable.left();

        gui.createContainer(rootTable).bottom();
        gui.createContainer(fpsTable).top().left();

        // the actual table that holds the inventory slots
        final Table inventory = new Table();

        // the table holding player class information
        classSelectorTable = new Table();
        classSelectorTable.setVisible(true);
        classSelectorTable.left();

        interactionTable = new Table();
        interactionTable.setVisible(false);
        interactionTable.bottom();

        gui.createContainer(interactionTable).bottom().padBottom(64);
        gui.createContainer(classSelectorTable).bottom().left().pad(8);

        interactionTable.add(new Label("Interact: >>E<<", gui.getSkin(), "large", Color.DARK_GRAY));

        // notifications
        notificationTable = new Table();
        notificationTable.setVisible(false);
        notificationTable.top().right();

        gui.createContainer(notificationTable).top().right().pad(4);
        final Table notification = new Table();
        notification.setBackground(new TextureRegionDrawable(asset.get("hint_dropdown")));

        notification.add(new Label("You received ", gui.getSkin(), "medium", Color.DARK_GRAY));
        notification.add(iconImage = new Image()).padBottom(6).padTop(6).padRight(2);
        notification.add(new Label("x", gui.getSkin(), "small", Color.DARK_GRAY));
        notification.add(amountLabel = new Label("1", gui.getSkin(), "medium", Color.DARK_GRAY));
        notificationTable.add(notification).size(256, 32);

        // initialize HUD inventory.
        final TextureRegionDrawable slot = new TextureRegionDrawable(asset.get("inventory_slot"));
        this.inventoryRenderer = new InventoryUiHandler(player.getInventory().getInventorySize(), player);
        this.inventoryRenderer.initialize(inventory, slot);

        classSelectorTable.add(classImage = new Image(asset.get("nature_class"))).size(48, 48);
        rootTable.add(inventory).padBottom(8);
    }

    @Override
    public void update() {
        if (notificationTable.getColor().a == 1 && (System.currentTimeMillis() - lastHint >= 2500))
            notificationTable.addAction(Actions.fadeOut(1f));

        inventoryRenderer.update();
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
    }

    public void setClassIcon(String icon) {
        classImage.setDrawable(new TextureRegionDrawable(asset.get(icon)));
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

    public void showInteractionHint() {
        interactionTable.setVisible(true);
    }

    public void hideInteractionHint() {
        interactionTable.setVisible(false);
    }

    public boolean isInteractionHintShowing() {
        return interactionTable.isVisible();
    }

    @Override
    public void showGui() {
        rootTable.setVisible(true);
        classSelectorTable.setVisible(true);
        isShowing = true;
    }

    @Override
    public void hideGui() {
        rootTable.setVisible(false);
        classSelectorTable.setVisible(false);
        interactionTable.setVisible(false);
        notificationTable.setVisible(false);
        isShowing = false;
    }

}
