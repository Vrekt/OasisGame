package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Shown when the player picks up and item or is given one
 */
public final class HudItemAcquiredComponent extends HudComponent {

    private static final float TIME = 2.0f;

    private final Array<ItemComponent> components = new Array<>(3);
    private final Queue<ItemAppearanceContainer> itemAppearanceQueue = new LinkedList<>();

    private final LinkedList<Item> itemQueue = new LinkedList<>();
    private int itemsBeingShown;

    private ItemComponent activeUpdateComponent;

    public HudItemAcquiredComponent(GuiManager manager) {
        super(HudComponentType.ITEM_ACQUIRED, manager);

        rootTable.setVisible(false);
        rootTable.bottom().right().padRight(8).padBottom(24);

        for (int i = 0; i < 3; i++) {
            final VisImage slot = new VisImage(Styles.getTheme());
            final VisImage item = new VisImage();
            item.setOrigin(16 / 2f, 16 / 2f);

            final VisLabel amountLabel = new VisLabel(StringUtils.EMPTY, Styles.getSmallWhite());

            final VisTable itemTable = new VisTable(false);
            itemTable.add(item).size(32, 32);

            final Stack overlay = new Stack(slot, itemTable);
            overlay.setVisible(false);
            components.add(new ItemComponent(overlay, item, amountLabel));

            rootTable.add(overlay).size(48, 48).padBottom(2f);
            rootTable.add(amountLabel).padLeft(2f);
            if (i != 2) rootTable.row();
        }

        guiManager.addGui(rootTable);
    }

    @Override
    public void update(float tick) {
    }

    private void populateItem(Item item) {

    }

    public void showItemAcquired(Item item) {
        itemAppearanceQueue.add(new ItemAppearanceContainer(item, GameManager.getTick()));
    }

    private record ItemAppearanceContainer(Item item, float tickAdded) {

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
            timeAdded = GameManager.getTick();

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
