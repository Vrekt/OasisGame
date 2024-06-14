package me.vrekt.oasis.gui.guis.inventory.utility;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.item.artifact.ItemArtifact;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import org.apache.commons.lang3.StringUtils;

/**
 * Item information like its stats or attributes
 */
public final class ItemInformationContainer {

    private final VisImage icon;
    private final Tooltip tooltip;

    public ItemInformationContainer(VisImage icon, Tooltip tooltip) {
        this.icon = icon;
        this.tooltip = tooltip;

        icon.setVisible(false);
        tooltip.setVisible(false);
    }

    /**
     * Update this information container with attribute information
     *
     * @param manager   manager
     * @param attribute attribute
     * @return the actor
     */
    public VisImage updateAttribute(GuiManager manager, Attribute attribute) {
        icon.setDrawable(attribute.subType().get(manager));
        icon.setVisible(true);

        tooltip.setText(attribute.name() + StringUtils.LF + attribute.description());
        tooltip.setVisible(true);

        return icon;
    }

    /**
     * Update artifact information
     *
     * @param artifact artifact
     * @return the actor
     */
    public VisImage updateArtifact(ItemArtifact artifact) {
        icon.setDrawable(new TextureRegionDrawable(artifact.getIcon()));
        icon.setVisible(true);

        tooltip.setText("Artifact Level: " + artifact.getArtifact().getArtifactLevel()
                + "\n" + "Artifact Duration: " + artifact.getArtifact().getArtifactDuration()
                + " \n" + "Artifact Cooldown: " + artifact.getArtifact().getArtifactCooldown());
        tooltip.setVisible(true);
        return icon;
    }

    /**
     * Update a weapon's range information
     *
     * @param manager manager
     * @param weapon  weapon
     * @return the actor
     */
    public VisImage updateRange(GuiManager manager, ItemWeapon weapon) {
        icon.setDrawable(manager.style().getWeaponRangeIcon());
        icon.setVisible(true);

        tooltip.setText("Range ~= " + weapon.getRange());
        tooltip.setVisible(true);
        return icon;
    }

    /**
     * Update a weapon's damage information
     *
     * @param manager manager
     * @param weapon  weapon
     * @return the actor
     */
    public VisImage updateDamage(GuiManager manager, ItemWeapon weapon) {
        icon.setDrawable(manager.style().getWeaponDamageIcon());
        icon.setVisible(true);

        tooltip.setText("Damage ~= " + weapon.getBaseDamage());
        tooltip.setVisible(true);
        return icon;
    }

    /**
     * Update a weapon's critical hit chance information
     *
     * @param manager manager
     * @param weapon  weapon
     * @return the actor
     */
    public VisImage updateCriticalChance(GuiManager manager, ItemWeapon weapon) {
        icon.setDrawable(manager.style().getWeaponCriticalIcon());
        icon.setVisible(true);

        tooltip.setText("Critical chance ~= " + weapon.getCriticalHitChance());
        tooltip.setVisible(true);
        return icon;
    }

    /**
     * Hide
     */
    public void hide() {
        icon.setVisible(false);
        tooltip.setVisible(false);
    }

}
