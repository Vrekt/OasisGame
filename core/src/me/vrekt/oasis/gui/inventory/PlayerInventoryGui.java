package me.vrekt.oasis.gui.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.ItemRarity;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;
import me.vrekt.oasis.item.items.other.AmbitiousMedicsBox;
import me.vrekt.oasis.item.items.other.BlessingOfAthena;
import me.vrekt.oasis.item.items.other.EnchantedBandOfPower;
import me.vrekt.oasis.item.items.other.WandOfEmbracing;
import me.vrekt.oasis.item.items.weapons.BladeOfAverrWeapon;
import me.vrekt.oasis.item.items.weapons.FrostbittenAvernicWeapon;
import me.vrekt.oasis.item.items.weapons.PrototypeTimepiercerWeapon;

import java.util.HashMap;
import java.util.Map;

/**
 * The players inventory
 */
public final class PlayerInventoryGui extends Gui {

    private Table root, itemAttributes, right, i;
    private TextButton button;

    // inventory slot backgrounds
    private final TextureRegionDrawable slot, rare, epic, background;

    // slots
    private final Map<Integer, Image> slots = new HashMap<>();

    private Image itemHeaderImage;
    private Label itemHeader, tabHeader, itemDesc;

    public PlayerInventoryGui(GameGui gui) {
        super(gui);

        // debug
        gui.getGame().getPlayer().getInventory().giveItem(new PrototypeTimepiercerWeapon(gui.getAsset()));
        gui.getGame().getPlayer().getInventory().giveItem(new FrostbittenAvernicWeapon(gui.getAsset()));
        gui.getGame().getPlayer().getInventory().giveItem(new BladeOfAverrWeapon(gui.getAsset()));
        gui.getGame().getPlayer().getInventory().giveItem(new BlessingOfAthena(gui.getAsset()));
        gui.getGame().getPlayer().getInventory().giveItem(new AmbitiousMedicsBox(gui.getAsset()));
        gui.getGame().getPlayer().getInventory().giveItem(new EnchantedBandOfPower(gui.getAsset()));
        gui.getGame().getPlayer().getInventory().giveItem(new WandOfEmbracing(gui.getAsset()));

        // slot images
        slot = new TextureRegionDrawable(gui.getAsset().get("inventory_slot"));
        rare = new TextureRegionDrawable(gui.getAsset().get("inventory_slot_rare"));
        epic = new TextureRegionDrawable(gui.getAsset().get("inventory_slot_epic"));
        background = new TextureRegionDrawable(gui.getAsset().get("inventory_background"));

        root = new Table();
        root.setVisible(false);
        root.setBackground(background);
        gui.createContainer(root).fill();

        // create right/left tables for content
        right = new Table().left().top();
        final Table left = new Table().left().top();
        itemAttributes = new Table().left();
        itemDesc = new Label("", gui.getSkin(), "small", Color.BLACK);
        itemDesc.setWrap(true);

        // expand left and right to fill space.
        root.add(left).growY();
        root.add(right).grow();

        left.add(tabHeader = new Label("Weapons", gui.getSkin(), "big", Color.BLACK)).left();
        left.row();
        left.add(initializePlayerInventory());

        i = initializeInformation();

        right.add(i).padLeft(16).left();
        right.row();
        right.add(itemDesc).width(256).padLeft(16).left();
    }

    @Override
    public void showGui() {
        root.setVisible(true);
        super.showGui();
    }

    @Override
    public void hideGui() {
        root.setVisible(false);
        super.hideGui();
    }

    private Table initializePlayerInventory() {
        final Table inventory = new Table();

        for (int i = 0; i < 24; i++) {
            // row inventory every 6 slots.
            if (i % 4 == 0 && i != 0) inventory.row();

            final Stack stack = new Stack();
            final Item item = gui.getGame().getPlayer().getInventory().getItemAt(i);
            if (item != null) {
                switch (item.getRarity()) {
                    case EPIC:
                        stack.add(new Image(epic));
                        break;
                    case RARE:
                        stack.add(new Image(rare));
                        break;
                    default:
                        stack.add(new Image(slot));
                }
                stack.add(new Image(item.getTexture()));
            } else {
                stack.add(new Image(slot));
            }


            stack.addListener(new ItemSlotClickHandler(i));
            inventory.add(stack).size(64, 64).padLeft(1f).padBottom(1f);
        }
        return inventory;
    }

    // initialize item information on the right side
    private Table initializeInformation() {
        final Table information = new Table();
        this.itemHeaderImage = new Image();
        this.itemHeader = new Label("", gui.getSkin(), "big", Color.SLATE);
        this.itemHeader.setWrap(true);

        this.itemDesc = new Label("", gui.getSkin(), "small", Color.BLACK);
        this.itemDesc.setWrap(true);

        information.left();
        information.add(itemHeaderImage).size(64, 64).left();
        information.add(itemHeader).width(256).left();
        information.row();
        information.add(itemAttributes).left();
        return information;
    }

    /**
     * Represents a single state of an inventory tab.
     */
    private final class InventoryTabState {
        final Image actor;
        final TextureRegionDrawable active, inactive;
        boolean state = false;

        public InventoryTabState(String inactive, String active) {
            this.inactive = new TextureRegionDrawable(PlayerInventoryGui.this.gui.getAsset().get(inactive));
            this.active = new TextureRegionDrawable(PlayerInventoryGui.this.gui.getAsset().get(active));
            this.actor = new Image(this.inactive);
        }

        void changeState() {
            state = !state;
            if (state) {
                setActive();
            } else {
                setInactive();
            }
        }

        void setActive() {
            actor.setDrawable(active);
            state = true;
        }

        void setInactive() {
            actor.setDrawable(inactive);
            state = false;
        }
    }

    private final class ItemSlotClickHandler extends ClickListener {
        private final int index;

        public ItemSlotClickHandler(int index) {
            this.index = index;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            final Item item = gui.getGame().getPlayer().getInventory().getItemAt(index);

            if (item != null) {
                final Color color = item.getRarity() == ItemRarity.EPIC ?
                        Color.PURPLE : Color.ROYAL;

                itemAttributes.clear();
                itemHeader.setText(item.getName());
                itemHeaderImage.setDrawable(new TextureRegionDrawable(item.getTexture()));
                itemAttributes.add(new Label(item.getRarity().name(), gui.getSkin(), "big", color)).left();
                itemAttributes.row();

                for (ItemAttributeType type : item.getAttributes().keySet()) {
                    final int level = item.getAttributeLevel(type);

                    itemAttributes.add(new Label(type.getName() + " +" + level, gui.getSkin(), "small", color))
                            .width(64).left();
                    itemAttributes.row();
                }
                itemDesc.setText(item.getDescription());
            }
            return true;
        }
    }

}
