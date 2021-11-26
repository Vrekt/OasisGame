package me.vrekt.oasis.world.districts;

public enum DistrictReward {

    LIRA("Lira", "lira"),
    WAND_OF_EMBRACING("Wand of Embracing", "wand_of_embracing"),
    AMBITIOUS_MEDICS_BOX("Ambitious Medics' Box", "ambitious_medics_box");

    private final String name, resource;

    DistrictReward(String name, String resource) {
        this.name = name;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public String getResource() {
        return resource;
    }
}
