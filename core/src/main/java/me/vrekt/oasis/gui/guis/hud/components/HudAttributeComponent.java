package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;

import java.util.LinkedList;

/**
 * Shows active player effects
 */
public final class HudAttributeComponent extends HudComponent {

    private final LinkedList<AttributeComponentSlot> attributeComponentSlots = new LinkedList<>();

    public HudAttributeComponent(GuiManager manager) {
        super(HudComponentType.ATTRIBUTE, manager);

        rootTable.setVisible(false);
        rootTable.bottom().right().padRight(8).padBottom(24);

        for (int i = 0; i < 3; i++) createAttributeContainer(rootTable);

        guiManager.addGui(rootTable);
    }

    @Override
    public void update(float tick) {
        for (AttributeComponentSlot component : attributeComponentSlots) {
            component.update();
        }
    }

    /**
     * Creates an attribute container for the attribute icon
     *
     * @param parent parent table
     */
    private void createAttributeContainer(VisTable parent) {
        final VisImage icon = new VisImage();
        icon.setVisible(false);

        parent.add(icon).size(36, 36);
        parent.row();

        attributeComponentSlots.add(new AttributeComponentSlot(icon));
    }

    /**
     * Show an attribute icon component
     *
     * @param attribute the attribute
     */
    public void showAttribute(Attribute attribute) {
        show();

        for (AttributeComponentSlot component : attributeComponentSlots) {
            if (!component.isActive) {
                component.show(attribute);
                break;
            }
        }
    }

    /**
     * Hide the components table if no attributes are active anymore.
     */
    private void hideAttributeComponentsIfNoneActive() {
        boolean visible = false;
        for (AttributeComponentSlot component : attributeComponentSlots) {
            if (component.isActive) {
                visible = true;
                break;
            }
        }
        if (!visible) hide();
    }

    private final class AttributeComponentSlot {
        private Attribute activeAttribute;
        private final VisImage icon;
        private boolean isActive;

        AttributeComponentSlot(VisImage icon) {
            this.icon = icon;
        }

        void show(Attribute attribute) {
            this.activeAttribute = attribute;

            icon.getColor().a = 0.0f;
            icon.addAction(Actions.fadeIn(1.0f));
            icon.setDrawable(activeAttribute.subType().get());
            icon.setVisible(true);
            isActive = true;
        }

        void expire() {
            icon.addAction(
                    Actions.sequence(
                            Actions.fadeOut(1.0f),
                            Actions.visible(false),
                            Actions.run(HudAttributeComponent.this::hideAttributeComponentsIfNoneActive))
            );
            activeAttribute = null;
            isActive = false;
        }

        void update() {
            if (activeAttribute == null) return;

            if (activeAttribute.isExpired()) {
                expire();
            }
        }
    }

}
