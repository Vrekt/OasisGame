package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;
import me.vrekt.oasis.item.artifact.Artifact;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * The players artifact inventory HUD
 */
public final class HudArtifactComponent extends HudComponent {

    private final LinkedList<ArtifactComponentSlot> artifactComponentSlots = new LinkedList<>();

    public HudArtifactComponent(GuiManager manager) {
        super(HudComponentType.ARTIFACT, manager);

        rootTable.setVisible(true);
        rootTable.bottom().left().padLeft(8).padBottom(24);

        final TextureRegionDrawable slot = new TextureRegionDrawable(asset("theme"));
        for (int i = 0; i < 3; i++) {
            createArtifactContainer(slot, rootTable, i);
        }

        guiManager.addGui(rootTable);
    }

    @Override
    public void update(float tick) {
        for (int i = 0; i < player.getArtifacts().size; i++) {
            final Artifact artifact = player.getArtifacts().get(i);
            if (artifact != null) artifactComponentSlots.get(i).update(artifact);
        }
    }


    /**
     * Show the artifact apply effect
     *
     * @param slot     the slot
     * @param cooldown the cooldown
     */
    public void showArtifactAbilityUsed(int slot, float cooldown) {
        artifactComponentSlots.get(slot).activate(cooldown);
    }

    /**
     * Create separate containers for each artifact slot
     *
     * @param theme  the theme
     * @param parent the parent owner
     * @param index  the current index iterating
     */
    private void createArtifactContainer(TextureRegionDrawable theme, VisTable parent, int index) {
        final VisImage background = new VisImage(theme);
        final VisTable table = new VisTable(true);
        final VisImage icon = new VisImage();
        final ArtifactComponentSlot slot = new ArtifactComponentSlot(icon);
        table.add(icon).size(32, 32);

        final Stack stack = new Stack(background, new Container<Table>(table));
        parent.add(stack).size(48, 48);
        parent.row().padTop(4);

        artifactComponentSlots.add(index, slot);
    }

    /**
     * Store basic information about an artifact slot.
     */
    private static final class ArtifactComponentSlot {
        private final VisImage artifactImageComponent;
        private final Tooltip tooltip;
        private Artifact artifact;

        public ArtifactComponentSlot(VisImage artifactImageComponent) {
            this.artifactImageComponent = artifactImageComponent;
            this.tooltip = new Tooltip.Builder(StringUtils.EMPTY)
                    .target(artifactImageComponent)
                    .style(Styles.getTooltipStyle())
                    .build();
        }

        /**
         * Update this component only if changed or new
         *
         * @param artifact the current artifact iterating
         */
        void update(Artifact artifact) {
            if (this.artifact == null || !this.artifact.is(artifact)) {
                this.artifact = artifact;
                artifactImageComponent.setDrawable(new TextureRegionDrawable(artifact.getSprite()));
                tooltip.setText(artifact.getName());
            }
        }

        /**
         * Activate the hud effect for this slot
         *
         * @param cooldown cooldown until active again
         */
        void activate(float cooldown) {
            artifactImageComponent.getColor().a = 0.0f;
            artifactImageComponent.addAction(Actions.fadeIn(cooldown));
        }

    }

}
