package me.vrekt.oasis.world.farm.ui;

/**
 * Interaction options and their textures
 */
public enum AllotmentInteractionOption {

    RAKE("raking_interaction"), NONE(null);

    private final String asset;

    AllotmentInteractionOption(String asset) {
        this.asset = asset;
    }

    public String getAsset() {
        return asset;
    }
}
