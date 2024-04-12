package me.vrekt.oasis.ui;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.OasisGame;

/**
 * Screen for joining localhost multiplayer games
 */
public final class MultiplayerGamesScreen extends ScreenAdapter {

    private final OasisGame game;
    private final Stage stage;

    public MultiplayerGamesScreen(OasisGame game) {
        this.game = game;
        this.stage = new Stage();

        VisTable rootTable = new VisTable(true);
        rootTable.setBackground(new TextureRegionDrawable(game.getAsset().get("pause")));
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        final Label.LabelStyle style = new Label.LabelStyle(game.getAsset().getLarge(), Color.WHITE);
        final VisList<VisLabel> list = new VisList<>();
        final VisLabel header = new VisLabel("LAN multiplayer games", style);
    }
}
