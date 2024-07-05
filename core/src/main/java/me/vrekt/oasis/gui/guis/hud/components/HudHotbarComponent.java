package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.weapons.ItemWeapon;

/**
 * Players item hotbar
 */
public final class HudHotbarComponent extends HudComponent {

    private final Array<HotbarComponentSlot> hotbarIconComponents = new Array<>();
    private HotbarComponentSlot selectedSlot;

    public HudHotbarComponent(GuiManager manager) {
        super(HudComponentType.HOT_BAR, manager);

        rootTable.bottom().padBottom(8);

        // 6 hotbar slots
        final Label.LabelStyle style = new Label.LabelStyle(guiManager.getSmallFont(), Color.LIGHT_GRAY);

        for (int i = 0; i < 6; i++) {
            // adapter from InventoryGui class
            final VisImage slot = new VisImage(Styles.getTheme());
            final VisImage item = new VisImage();
            item.setOrigin(16 / 2f, 16 / 2f);

            final VisTable itemTable = new VisTable(false);
            itemTable.add(item).size(32, 32);

            final Stack overlay = new Stack(slot, itemTable);

            final VisTable slotNumber = new VisTable(true);
            final VisLabel slotNumberLabel = new VisLabel(Integer.toString(i + 1), style);
            slotNumber.top().left();
            slotNumber.add(slotNumberLabel).top().left().padLeft(2);
            overlay.add(slotNumber);

            hotbarIconComponents.add(new HotbarComponentSlot(slot, item));

            slot.setColor(Color.WHITE);
            rootTable.add(overlay).size(48, 48).padLeft(2);
        }

        guiManager.addGui(rootTable);
    }

    @Override
    public void update(float tick) {
        // TODO: Fix this nasty shit
        for (IntMap.Entry<Item> entry : player.getInventory().items()) {
            if (entry.value == null || !player.getInventory().isHotbar(entry.key)) continue;
            hotbarIconComponents.get(entry.key).replaceIfRequired(entry.value);
        }
    }

    /**
     * Set a hotbar slot was selected
     *
     * @param slot the slot
     */
    public void hotbarItemSelected(int slot) {
        // reset current slot only if we have one and its active
        if (selectedSlot != null && hotbarIconComponents.indexOf(selectedSlot, true) != slot) selectedSlot.reset();
        // update selected slot
        selectedSlot = hotbarIconComponents.get(slot);
        selectedSlot.setSelected();

        // TODO: Mechanics still being decided
        if (selectedSlot.item instanceof ItemWeapon weapon) {
            player.equipItem(weapon);
        }
    }

    /**
     * Remove an item from the hotbar slot
     * TODO: Maybe in the future just detect this with update
     * TODO: but, since its removed physically from the list it won't be iterated next update to be detected.
     *
     * @param slot the slot
     */
    public void hotbarItemRemoved(int slot) {
        hotbarIconComponents.get(slot).removeItemFromSlot();
    }

    private static final class HotbarComponentSlot {

        private final VisImage slot, itemIcon;
        private Item item;

        HotbarComponentSlot(VisImage slot, VisImage itemIcon) {
            this.slot = slot;
            this.itemIcon = itemIcon;
        }

        public void replaceIfRequired(Item other) {
            if (replaceItem(other)) setItemInSlot(other);
        }

        /**
         * @param other comparing
         * @return {@code true} if the incoming item should replace what's in this slot
         */
        public boolean replaceItem(Item other) {
            if (this.item == null) return true;
            return !this.item.compare(other);
        }

        public void setSelected() {
            slot.setColor(Color.GRAY);
        }

        public void reset() {
            slot.setColor(Color.WHITE);
        }

        public void setItemInSlot(Item item) {
            itemIcon.setDrawable(new TextureRegionDrawable(item.sprite()));
            itemIcon.setScale(item.scale());

            itemIcon.setOrigin(item.sprite().getRegionWidth() / 2f, item.sprite().getRegionHeight() / 2f);

            this.item = item;
        }

        public void removeItemFromSlot() {
            itemIcon.setDrawable((Drawable) null);
            this.item = null;
        }
    }

}
