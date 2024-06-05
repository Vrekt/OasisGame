package me.vrekt.oasis.save.player;

import com.badlogic.gdx.utils.IntMap;
import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.artifact.Artifact;

import java.util.LinkedList;

/**
 * Save the players artifact inventory
 */
public final class PlayerArtifactSave {

    @Expose
    private LinkedList<ArtifactSave> artifacts;

    public PlayerArtifactSave(PlayerSP player) {
        if (player.getArtifacts().isEmpty()) return;

        this.artifacts = new LinkedList<>();
        for (IntMap.Entry<Artifact> entry : player.getArtifacts()) {
            artifacts.add(new ArtifactSave(entry.key, entry.value));
        }
    }

    public LinkedList<ArtifactSave> artifacts() {
        return artifacts;
    }
}
