package me.vrekt.oasis.save.world.player;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.item.artifact.Artifact;

/**
 * Single artifact save
 */
public final class ArtifactSave {

    @Expose
    private int slot;

    @Expose
    private String key;

    @Expose
    private int level;

    public ArtifactSave(int slot, Artifact artifact) {
        this.slot = slot;
        this.key = artifact.getKey();
        this.level = artifact.getArtifactLevel();
    }
}
