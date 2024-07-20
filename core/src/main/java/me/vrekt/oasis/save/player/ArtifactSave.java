package me.vrekt.oasis.save.player;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.item.artifact.ArtifactType;

/**
 * Single artifact save
 */
public final class ArtifactSave {

    @Expose
    private int slot;
    @Expose
    private ArtifactType type;

    @Expose
    private int level;

    public ArtifactSave(int slot, Artifact artifact) {
        this.slot = slot;
        this.type = artifact.type();
        this.level = artifact.getArtifactLevel();
    }

    public int slot() {
        return slot;
    }

    public ArtifactType type() {
        return type;
    }

    public int level() {
        return level;
    }
}
