package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.consumables.food.CrimsonCapItem;
import org.apache.commons.lang3.StringUtils;

/**
 * Shown when the player picks up and item or is given one
 */
public final class HudItemAcquiredComponent extends HudComponent {

    private final Array<ItemAcquiredComponent> components = new Array<>(true, 5);

    public HudItemAcquiredComponent(GuiManager manager) {
        super(HudComponentType.ITEM_ACQUIRED, manager);

        rootTable.setVisible(false);
        rootTable.bottom().right().padRight(8).padBottom(96);

        final NinePatch patch = new NinePatch(asset("theme_transparent"), 6, 6, 6, 6);
        final NinePatchDrawable transparent = new NinePatchDrawable(patch);

        // populate all pre defined components
        for (int i = 0; i < 5; i++) {
            final VisTable container = new VisTable();
            container.setBackground(transparent);
            container.setVisible(false);

            final VisImage item = new VisImage(asset(CrimsonCapItem.TEXTURE));
            item.setOrigin(16 / 2f, 16 / 2f);

            final VisTable itemTable = new VisTable(false);
            itemTable.add(item).size(32, 32);

            final VisLabel amount = new VisLabel("x1", Styles.getMediumWhiteMipMapped());
            amount.setColor(Color.LIGHT_GRAY);

            container.add(itemTable);
            container.add(amount).padLeft(4f);
            components.add(new ItemAcquiredComponent(container, item, amount));

            rootTable.add(container).padTop(2f).padRight(8);
            rootTable.row();
        }

        guiManager.addGui(rootTable);
    }


    @Override
    public void update(float tick) {
        for (ItemAcquiredComponent component : components) {
            if (!component.isAvailable && GameManager.hasTimeElapsed(component.tickAdded, 1.88f)) {
                component.hide();
            }
        }
    }

    public void showItemAcquired(Item item) {
        final ItemAcquiredComponent component = findAvailableComponent();
        if (component != null) {
            component.populateAndShow(item);
        }

        if (!isShowing) {
            show();
        }
    }

    private ItemAcquiredComponent findAvailableComponent() {
        for (ItemAcquiredComponent component : components) {
            if (component.isAvailable) return component;
        }
        return null;
    }

    private final class ItemAcquiredComponent {
        private final VisTable container;
        private final VisImage item;
        private final VisLabel amount;

        private boolean isAvailable = true;
        private float tickAdded;

        public ItemAcquiredComponent(VisTable container, VisImage item, VisLabel amount) {
            this.container = container;
            this.item = item;
            this.amount = amount;
        }

        /**
         * Populate item details and show
         *
         * @param item item
         */
        public void populateAndShow(Item item) {
            isAvailable = false;

            this.item.setDrawable(new TextureRegionDrawable(item.sprite()));
            this.amount.setText("x" + item.amount());
            fadeIn(container, 1.0f);

            tickAdded = GameManager.tick();
        }

        /**
         * Hide this component
         */
        public void hide() {
            fadeOut(container, 1.0f);
            isAvailable = true;
        }

    }

    private static final class ItemComponent {
        private final Stack container;
        private final VisImage item;
        private final VisLabel amountLabel;

        private boolean isShowing;
        private float timeAdded;

        public ItemComponent(Stack container, VisImage item, VisLabel amountLabel) {
            this.container = container;
            this.item = item;
            this.amountLabel = amountLabel;
        }

        void show(float delay) {
            isShowing = true;
            timeAdded = GameManager.tick();

            container.getColor().a = 0.0f;
            container.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(1.0f)));
        }

        void reset() {
            amountLabel.setText(StringUtils.EMPTY);
            isShowing = false;

            container.getColor().a = 1.0f;
            container.addAction(Actions.sequence(
                    Actions.fadeOut(1.0f),
                    Actions.visible(false),
                    Actions.run(() -> item.setDrawable((Drawable) null))));
        }

    }

}
