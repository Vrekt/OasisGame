package me.vrekt.oasis.gui.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;

import java.util.HashMap;
import java.util.Map;

/**
 * The players inventory
 */
public final class PlayerInventoryGui extends Gui {

    // slots
    private final Map<Integer, Image> slots = new HashMap<>();

    // tabs
    private InventoryTabState weapons, valueables, food;
    // describes what the content is being sorted by
    private Label tabHeader;

    private Table itemAttributes;
    private Image itemHeaderImage;
    private Label itemHeader;

    public PlayerInventoryGui(GameGui gui) {
        super(gui);

        final Table root = new Table();
        root.setVisible(false);
        gui.createContainer(root).fill();

        initializeNavigation();
        // create navigation tabs at the top,
        // initialize 2 here because there is a break for some reason
        final Table tabs = new Table();
        final Table tabs2 = new Table();
        tabs.setBackground(new TextureRegionDrawable(gui.getAsset().get("inventory_color")));
        tabs2.setBackground(new TextureRegionDrawable(gui.getAsset().get("inventory_color")));

        // add weapon tab icons
        tabs.add(weapons.actor).size(48, 48);
        tabs.add(valueables.actor).size(48, 48);
        tabs.add(food.actor).size(48, 48);
        root.add(tabs).fill();
        root.add(tabs2).fill();
        root.row();

        // create right/left tables for content
        final Table right = new Table().left().top();
        final Table left = new Table().left().top();

        left.setBackground(new TextureRegionDrawable(gui.getAsset().get("inventory_background")));
        right.setBackground(new TextureRegionDrawable(gui.getAsset().get("inventory_background")));

        // expand left and right to fill space.
        root.add(left).growY();
        root.add(right).grow();

        left.add(itemHeader = new Label("Weapons", gui.getSkin(), "big", Color.BLACK)).left();
        left.row();
        left.add(initializePlayerInventory()).left();

        right.add(initializeInformation()).left();

    }

    private void initializeNavigation() {
        this.weapons = new InventoryTabState("weapon_icon", "weapon_icon_active");
        this.valueables = new InventoryTabState("valueables_icon", "valueables_icon_active");
        this.food = new InventoryTabState("food_icon", "food_icon_active");
    }

    private Table initializePlayerInventory() {
        final Table inventory = new Table();
        final TextureRegionDrawable slot = new TextureRegionDrawable(gui.getAsset().get("inventory_slot"));
        final TextureRegionDrawable rare = new TextureRegionDrawable(gui.getAsset().get("inventory_slot_rare"));

        for (int i = 0; i < 24; i++) {
            if (i % 6 == 0 && i != 0) inventory.row();

            final Stack stack = new Stack();
            final Item item = gui.getGame().getPlayer().getInventory().getItemAt(i);
            if (item != null) {
                switch (item.getRarity()) {
                    case EPIC:
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
            inventory.add(stack).size(64, 64).padLeft(2f).padBottom(2f);
        }

        return inventory;
    }

    // initialize item information on the right side
    private Table initializeInformation() {
        final Table information = new Table();
        this.itemAttributes = new Table();
        this.itemHeaderImage = new Image();
        this.itemHeader = new Label("", gui.getSkin(), "big", Color.WHITE);

        information.left().padLeft(32f);
        information.add(itemHeaderImage).size(64, 64).left();
        information.add(itemHeader).left();
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
                itemAttributes.clear();
                itemHeader.setText(item.getName());
                itemHeaderImage.setDrawable(new TextureRegionDrawable(item.getTexture()));
                itemAttributes.add(new Label(item.getRarity().name(), gui.getSkin(), "big", Color.PURPLE)).left();
                itemAttributes.row();

                boolean mainStat = true;
                for (ItemAttributeType type : item.getAttributes().keySet()) {
                    final int level = item.getAttributeLevel(type);
                    if (mainStat) {
                        itemAttributes.add(new Label(type + " +" + level, gui.getSkin(), "small", Color.WHITE)).left();
                        mainStat = false;
                    } else {
                        itemAttributes.add(new Label(type + " +" + level, gui.getSkin(), "smaller", Color.WHITE)).left();
                    }
                    itemAttributes.row();
                }
                return true;
            }
            return false;
        }
    }

}
