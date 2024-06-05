package me.vrekt.oasis.item.artifact.artifacts;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.item.artifact.ArtifactType;

public final class SecondWindArtifact extends Artifact {

    public SecondWindArtifact() {
        super("artifact:second_wind", ArtifactType.SECOND_WIND, "Second Wind", "Allows you to heal yourself.");
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get("secondwind_artifact"));
    }

    @Override
    public boolean apply(PlayerSP player, float tick) {
        return false;
    }

    @Override
    public void expire(PlayerSP player) {

    }

    @Override
    protected void update(PlayerSP player, float tick) {

    }
}
