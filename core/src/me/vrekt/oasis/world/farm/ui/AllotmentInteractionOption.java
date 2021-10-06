package me.vrekt.oasis.world.farm.ui;

/**
 * Options for when interacting with an allotment
 */
public enum AllotmentInteractionOption {

    /**
     * Clear this allotment for planting
     */
    RAKE("raking_interaction"),

    /**
     * Plant something here.
     */
    PLANT(null);

    private final String region;

    AllotmentInteractionOption(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

}
