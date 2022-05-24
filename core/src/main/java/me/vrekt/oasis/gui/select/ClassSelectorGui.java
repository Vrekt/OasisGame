package me.vrekt.oasis.gui.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.classes.ClassType;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.world.tutorial.TutorialOasisWorld;

import java.util.Locale;

/**
 * Allows the player to select their desired class.
 */
public final class ClassSelectorGui extends Gui {

    private final Table rootTable;
    private final Image classImage;

    private final TextureRegionDrawable middle, end;
    private ClassType classType = ClassType.NATURE;

    // dialog options and title
    private final TypingLabel classDescription;

    public ClassSelectorGui(GameGui gui, Asset asset) {
        super(gui);

        rootTable = new Table();
        rootTable.setVisible(false);
        rootTable.left();

        this.middle = new TextureRegionDrawable(asset.get("dialog_option"));
        this.end = new TextureRegionDrawable(asset.get("dialog_end_picture"));

        final Table name = new Table();
        name.add(new Label("Pick A Class", gui.getSkin(), "medium", Color.BLACK));

        final Table optionsTable = new Table();
        Table optionsGroup = new Table();

        optionsTable.add(name).padBottom(2f);
        optionsTable.row();
        optionsTable.add(optionsGroup);

        gui.createContainer(rootTable).bottom();
        rootTable.add(optionsTable).right().padBottom(4);
        rootTable.row();

        final Table table = new Table();
        table.setBackground(new TextureRegionDrawable(asset.get("interaction_dialog")));
        table.left();

        final Table entity = new Table();
        entity.add(this.classImage = new Image(asset.get("nature_class_icon"))).size(48, 48).left();
        table.add(entity).padLeft(16);

        table.add(this.classDescription = new TypingLabel(ClassType.NATURE.getDescription(), gui.getSkin(), "medium", Color.BLACK))
                .width(448).padTop(16).padBottom(16).padRight(8).padLeft(8);
        this.classDescription.setWrap(true);

        table.row();

        final Table selector = new Table();

        final Table option = new Table();
        final Table bck = new Table();

        // click listener for choose button
        option.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // set player class type to current one
                hideGui();

                // just prevent general stuff, usually player is 100% in tutorial world
                gui.getGame().getPlayer().setClassType(classType);
                if (gui.getGame().getPlayer().getGameWorldIn() instanceof TutorialOasisWorld) {
                    // advance mavia stage to the class player picked

                    final EntityInteractable mavia = gui.getGame()
                            .getPlayer()
                            .getGameWorldIn()
                            .getByType(EntityNPCType.MAVIA);

                    if (mavia == null) {
                        // this should not happen
                        return true;
                    }

                    // advance state
                    mavia.advanceDialogStage("mavia_dialog_" + classType.getClassName().toLowerCase(Locale.ROOT));

                    // show dialog gui
                    gui.showEntityDialog(mavia);
                    gui.showGui(GuiType.DIALOG);
                }

                return true;
            }
        });

        bck.setBackground(middle);
        bck.add(new Label("Choose", gui.getSkin(), "small", Color.WHITE));

        option.add(new Image(asset.get("dialog_start_empty")));
        option.add(bck);
        option.add(new Image(end));

        selector.add(option);
        table.add(selector).padLeft(16);

        rootTable.add(table);

        optionsGroup.clear();

        optionsGroup.add(createClassOption("Nature", "nature_class_icon", ClassType.NATURE, "nature_icon_dialog")).padBottom(2f);
        optionsGroup.row();
        optionsGroup.add(createClassOption("Earth", "earth_class_icon", ClassType.EARTH, "earth_icon_dialog")).padBottom(2f);
        optionsGroup.row();
        optionsGroup.add(createClassOption("Water", "water_class_icon", ClassType.WATER, "water_icon_dialog")).padBottom(2f);
        optionsGroup.row();
        optionsGroup.add(createClassOption("Blood", "blood_class_icon", ClassType.BLOOD, "blood_icon_dialog")).padBottom(2f);
        optionsGroup.row();
        optionsGroup.add(createClassOption("Lava", "lava_class_icon", ClassType.LAVA, "lava_icon_dialog")).padBottom(2f);
        optionsGroup.row();
    }

    /**
     * Adopted from {@link me.vrekt.oasis.gui.dialog.DialogGui}
     */
    private Table createClassOption(String text, String key, ClassType classType, String start) {
        final Table option = new Table();
        final Table bck = new Table();

        bck.setBackground(middle);
        bck.add(new Label(text, gui.getSkin(), "small", Color.WHITE));

        option.add(new Image(asset.get(start)));
        option.add(bck);
        option.add(new Image(end));

        option.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                classDescription.restart(classType.getDescription());
                classImage.setDrawable(new TextureRegionDrawable(asset.get(key)));
                ClassSelectorGui.this.classType = classType;
                return true;
            }
        });

        return option;
    }

    @Override
    public void showGui() {
        rootTable.setVisible(true);
        isShowing = true;
    }

    @Override
    public void hideGui() {
        rootTable.setVisible(false);
        isShowing = false;
    }
}
