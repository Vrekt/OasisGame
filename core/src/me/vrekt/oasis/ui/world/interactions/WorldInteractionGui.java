package me.vrekt.oasis.ui.world.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.ui.world.Gui;
import me.vrekt.oasis.ui.world.WorldGui;

public final class WorldInteractionGui extends Gui {

    private final Vector2 interactionCoordinates = new Vector2(0, 0);
    private boolean interactionInitialized;

    private final Image interaction;
    private String text;

    public WorldInteractionGui(WorldGui gui) {
        super(gui);

        final TextureRegion texture = gui.getAsset().getAtlas(Asset.INTERACTIONS).findRegion("interaction");
        this.interaction = new Image(texture);
        this.interaction.setVisible(false);

        gui.addElementToStack(interaction, .5f * Gdx.graphics.getWidth(), texture.getRegionHeight(),
                (Gdx.graphics.getHeight() + (texture.getRegionHeight() * 2f)) / 2f);
    }

    @Override
    public void show(String text) {
        this.text = text;
        this.interaction.setVisible(true);
    }

    @Override
    public void render(BitmapFont font, BitmapFont big, Batch batch, GlyphLayout layout) {
        if (!interaction.isVisible()) return;

        if (!interactionInitialized) {
            interactionCoordinates.set(0, 0);
            interaction.localToStageCoordinates(interactionCoordinates);
            interactionInitialized = true;
        }

        if (text != null) {
            big.setColor(Color.BLACK);
            layout.setText(big, text);
            big.draw(batch, text, interactionCoordinates.x + (interaction.getWidth() - layout.width) / 2f,
                    interactionCoordinates.y + (interaction.getHeight() - (layout.height / 2f)));
        }
    }

    @Override
    public void resize() {
        interactionCoordinates.set(0, 0);
        interaction.localToStageCoordinates(interactionCoordinates);
        interactionInitialized = false;
    }

    @Override
    public void hide() {
        this.text = null;
        this.interaction.setVisible(false);
    }

    @Override
    public boolean isShowing() {
        return interaction.isVisible();
    }
}
