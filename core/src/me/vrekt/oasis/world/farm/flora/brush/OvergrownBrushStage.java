package me.vrekt.oasis.world.farm.flora.brush;

/**
 * The 3 stages of a overgrown brush that must be raked.
 */
public enum OvergrownBrushStage {

    OVERGROWN_1("farm/overgrownplot1.png"),
    OVERGROWN_2("farm/overgrownplot2.png"),
    OVERGROWN_3("farm/overgrownplot3.png"),
    FINISHED("farm/emptyplot.png");

    private final String asset;

    OvergrownBrushStage(String asset) {
        this.asset = asset;
    }

    public String getAsset() {
        return asset;
    }
}
