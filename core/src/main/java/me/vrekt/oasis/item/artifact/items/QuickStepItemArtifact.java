package me.vrekt.oasis.item.artifact.items;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.item.artifact.ItemArtifact;
import me.vrekt.oasis.item.artifact.artifacts.QuickStepArtifact;
import me.vrekt.oasis.item.utility.ItemDescriptor;

public final class QuickStepItemArtifact extends ItemArtifact {
    public static final String KEY = "oasis:quickstep_artifact_item";
    public static final String NAME = "Quickstep Artifact";
    public static final String TEXTURE = "quickstep_artifact_item";

    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    private Artifact artifact;

    public QuickStepItemArtifact() {
        super(Items.QUICKSTEP_ARTIFACT, KEY, NAME, "Allows you to move faster!");
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);

        this.artifact = new QuickStepArtifact();
        this.artifact.load(asset);
        this.artifact.item(this);

        this.icon = asset.get("quickstep_artifact_icon");
        this.rarity = ItemRarity.COSMIC;
    }

    @Override
    public void equip(PlayerSP player) {
        player.equipArtifactToArtifactInventory(this);
    }

    @Override
    public Artifact getArtifact() {
        return artifact;
    }
}
