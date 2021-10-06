package me.vrekt.oasis.world.farm.flora.brush;

/**
 * The 3 stages of a overgrown brush that must be raked.
 */
public enum OvergrownBrushStage {

    OVERGROWN_1("overgrownplot1"),
    OVERGROWN_2("overgrownplot2"),
    OVERGROWN_3("overgrownplot3"),
    FINISHED("emptyplot");

    private final String asset;

    OvergrownBrushStage(String asset) {
        this.asset = asset;
    }

    public String getAsset() {
        return asset;
    }
}
