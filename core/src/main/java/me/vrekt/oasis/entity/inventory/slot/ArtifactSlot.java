package me.vrekt.oasis.entity.inventory.slot;

public enum ArtifactSlot {

    ARTIFACT_ONE(0), ARTIFACT_TWO(1), ARTIFACT_THREE(2);

    private final int slot;
    ArtifactSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
