package me.vrekt.oasis.item.artifact;

import me.vrekt.oasis.item.ItemEquippable;

/**
 * Represents an artifact item, sort of like a special ability.
 */
public abstract class ItemArtifact extends ItemEquippable {

    public ItemArtifact(String itemName, int itemId, String description) {
        super(itemName, itemId, description);
    }

    public abstract Artifact getArtifact();

}
