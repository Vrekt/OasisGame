package me.vrekt.oasis.world.farm;

/**
 * Various stages in a farming allotment.
 */
public enum AllotmentStatus {

    /**
     * Plot is empty and plants/herbs are allowed to be planted.
     */
    EMPTY(true, false, "emptyplot") {
        @Override
        public AllotmentStatus getNextStage() {
            return AllotmentStatus.PLANTED;
        }
    },

    /**
     * Plot is not farmable yet.
     */
    OVERGROWN_1(false, false, "overgrownplot1") {
        @Override
        public AllotmentStatus getNextStage() {
            return AllotmentStatus.EMPTY;
        }
    },

    /**
     * Plot is not farmable yet.
     */
    OVERGROWN_2(false, false, "overgrownplot2") {
        @Override
        public AllotmentStatus getNextStage() {
            return AllotmentStatus.OVERGROWN_1;
        }
    },

    /**
     * Plot is not farmable yet.
     */
    OVERGROWN_3(false, false, "overgrownplot3") {
        @Override
        public AllotmentStatus getNextStage() {
            return AllotmentStatus.OVERGROWN_2;
        }
    },

    /**
     * Plot can be harvested
     */
    HARVESTABLE(false, true, null) {
        @Override
        public AllotmentStatus getNextStage() {
            return AllotmentStatus.EMPTY;
        }
    },


    /**
     * Plot is planted and growing
     */
    PLANTED(false, false, null) {
        @Override
        public AllotmentStatus getNextStage() {
            return AllotmentStatus.HARVESTABLE;
        }
    };

    private final boolean canGrow, canHarvest;
    private final String asset;

    AllotmentStatus(boolean canGrow, boolean canHarvest, String asset) {
        this.canGrow = canGrow;
        this.canHarvest = canHarvest;
        this.asset = asset;
    }

    /**
     * Retrieve the next stage in the growth process.
     *
     * @return the next stage
     */
    public abstract AllotmentStatus getNextStage();

    public boolean canGrow() {
        return canGrow;
    }

    public boolean canHarvest() {
        return canHarvest;
    }

    public boolean isOvergrown() {
        return this == OVERGROWN_1 || this == OVERGROWN_2 || this == OVERGROWN_3;
    }

    public String getAsset() {
        return asset;
    }
}
