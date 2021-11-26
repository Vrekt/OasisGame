package me.vrekt.oasis.gui.district;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.districts.AbstractDistrict;
import me.vrekt.oasis.world.districts.DistrictReward;

/**
 * A gui for choosing the level of district to fight against
 */
public final class DistrictEntranceGui extends Gui {

    private Table root;

    private final Table rewardsTable, levelsTable;
    private final Label districtName, districtDescription;

    private Label levelLabel;
    private String levelClickedOn;

    private AbstractDistrict district;

    public DistrictEntranceGui(GameGui gui) {
        super(gui);
        isShowing = false;

        root = new Table();
        root.setVisible(false);
        gui.createContainer(root).fill();

        final Table left = new Table().top().padTop(32f);
        final Table right = new Table().top().padTop(32f);

        left.setBackground(new TextureRegionDrawable(gui.getAsset().get("domain_background")));
        right.setBackground(new TextureRegionDrawable(gui.getAsset().get("domain_background")));

        // expand left and right to fill space.
        root.add(left).growY();
        root.add(right).grow();

        this.districtName = new Label("", gui.getSkin(), "big", Color.BLACK);
        this.districtDescription = new Label("", gui.getSkin(), "small", Color.BLACK);
        districtDescription.setWrap(true);
        this.rewardsTable = new Table();
        this.levelsTable = new Table();
        this.levelLabel = new Label("", gui.getSkin(), "small", Color.BLACK);

        right.add(districtName).left();
        right.row();
        right.add(districtDescription).width((Gdx.graphics.getWidth()) / 2.5f).left();
        right.row();
        right.add(new Label("Possible Rewards", gui.getSkin(), "big", Color.BLACK)).padTop(32f).left();
        right.row();
        right.add(rewardsTable).left();
        right.row();

        final Table startTable = new Table();
        startTable.add(levelLabel);
        startTable.row();

        final Table buttonTable = new Table();
        buttonTable.setBackground(new TextureRegionDrawable(gui.getAsset().get("green_button")));
        buttonTable.add(new Label("Start", gui.getSkin(), "small", Color.BLACK));
        startTable.add(buttonTable);
        right.add(startTable).padTop(32).center();

        buttonTable.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (levelClickedOn != null) {
                    Logging.info(DistrictEntranceGui.this, "Loading district information for: " + district.getType());
                    final boolean result = district.enterInstance(asset,
                            (AbstractWorld) gui.getGame().getPlayer().getWorldIn(),
                            gui.getGame(),
                            gui.getGame().getRenderer(),
                            gui.getGame().getPlayer());
                    if (result) {
                        hideGui();
                        ((AbstractWorld) gui.getGame().getPlayer().getWorldIn()).exit();
                    } else {
                        Logging.error(this, "Failed to load district information for: " + district.getType());
                    }
                    return true;
                }
                return false;
            }
        });

        left.add(new Label("Levels", gui.getSkin(), "big", Color.BLACK));
        left.row();
        left.add(levelsTable).padLeft(6);
    }

    /**
     * Populate district information for this UI
     *
     * @param district district
     */
    public void setDistrictTryingToEnter(AbstractDistrict district) {
        this.district = district;

        districtName.setText(district.getType().getPrettyName());
        districtDescription.setText(district.getDescription());

        levelsTable.clear();
        rewardsTable.clear();

        final TextureRegionDrawable background = new TextureRegionDrawable(gui.getAsset().get("quest_chapter"));
        district.getLevels().forEach((name, level) -> {
            final Table table = new Table();
            table.setBackground(background);

            table.add(new Label(name, gui.getSkin(), "small", Color.BLACK)).padLeft(16).padRight(16);
            table.row();
            table.add(new Label("Recommended character level: " + level, gui.getSkin(), "smaller", Color.BLACK)).padTop(6);
            levelsTable.add(table).width((Gdx.graphics.getWidth()) / 2f).left();
            levelsTable.row().padTop(6);

            table.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    levelLabel.setText(name);
                    levelClickedOn = name;
                    return true;
                }
            });

        });

        for (DistrictReward reward : district.getRewards().keySet()) {
            rewardsTable.add(new Image(gui.getAsset().get(reward.getResource()))).size(32, 32).left();
        }
        rewardsTable.row();
        for (Integer amount : district.getRewards().values()) {
            rewardsTable.add(new Label(Integer.toString(amount), gui.getSkin(), "smaller", Color.BLACK)).left().padLeft(8);
        }
    }

    @Override
    public void showGui() {
        super.showGui();
        root.setVisible(true);
    }

    @Override
    public void hideGui() {
        super.hideGui();
        root.setVisible(false);
    }
}
