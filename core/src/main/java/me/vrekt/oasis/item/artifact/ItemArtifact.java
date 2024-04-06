package me.vrekt.oasis.item.artifact;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.item.ItemEquippable;

/**
 * Represents an artifact item, sort of like a special ability.
 */
public abstract class ItemArtifact extends ItemEquippable {

    protected TextureRegion icon;

    public ItemArtifact(String key, String name, String description) {
        super(key, name, description);
    }

    public abstract Artifact getArtifact();

    public TextureRegion getIcon() {
        return icon;
    }
}
