package me.vrekt.oasis.gui.guis.other;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Styles;

public final class ShopGui extends Gui {

    public ShopGui(GuiManager guiManager) {
        super(GuiType.SHOP, guiManager);

        rootTable.top().left();
        rootTable.setVisible(false);
        rootTable.setFillParent(true);
        rootTable.setBackground(drawable("shop_gui"));

        final VisImage tab = new VisImage(drawable("enchanted_fern_brew_item"));
        tab.setScale(2.0f, 2.0f);

        final VisTable table = new VisTable();
        table.top().left();
        table.left().padLeft(22).padTop(8);

        final String[] images = new String[6];
        images[0] = "silver_spore_item";
        images[1] = "staff_of_obsidian_item";
        images[2] = "staff_of_earth_item";
        images[3] = "pig_heart";
        images[4] = "twilight_brew_item";
        images[5] = "witch_hat_item";

        final NinePatch patch = new NinePatch(asset("theme_transparent"), 6, 6, 6, 6);
        final NinePatchDrawable d = new NinePatchDrawable(patch);

        final VisTable content = new VisTable();
        final VisLabel label = new VisLabel("Enchanted Fern Brew", Styles.getMediumWhiteMipMapped());
        label.setColor(new Color(201 / 255f, 226 / 255f, 158 / 255f, 1.0f));
        content.add(label).left();
        content.row();
        content.add(new VisLabel("An enchanted potion with earthly powers.", Styles.getSmallWhite())).left();
        content.row();

        final VisTable bb = new VisTable();

        final VisImageTextButton button = new VisImageTextButton("Buy x1", Styles.getImageTextButtonStyle());
        final VisImageTextButton button2 = new VisImageTextButton("Buy All", Styles.getImageTextButtonStyle());
        bb.add(button).left().padRight(4);
        bb.add(button2).left();
        content.add(bb);


        for (int i = 0; i < 6; i++) {
            // adapter from InventoryGui class
            final VisImage slot = new VisImage(d);
            final VisImage item = new VisImage(drawable(images[i]));
            item.setOrigin(16 / 2f, 16 / 2f);

            final VisTable itemTable = new VisTable(false);
            itemTable.add(item).size(32, 32);

            final Stack overlay = new Stack(slot, itemTable);

            slot.setColor(Color.WHITE);
            if (i == 3) {
                table.row().padTop(4);
            }

            final Tooltip tooltip = new Tooltip.Builder(content).style(Styles.getTooltipStyle()).target(overlay).build();

            table.add(overlay).size(48, 48).padLeft(2);
        }

        final VisTable top = new VisTable();
        top.top().left().padLeft(52).padTop(30);
        top.add(tab).left();

        final VisTable bottom = new VisTable();

        rootTable.add(top).left();
        rootTable.row();
        rootTable.add(table);

        guiManager.addGui(rootTable);
    }

}
