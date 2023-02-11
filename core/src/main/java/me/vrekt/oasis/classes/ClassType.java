package me.vrekt.oasis.classes;

public enum ClassType {

    EARTH("Earth", "The earth class allows you to manipulate the land around you.", "earth_class_icon"),
    NATURE("Nature", "The nature class allows you to harness the power of Mother Nature.", "nature_class_icon"),
    WATER("Water", "The water class allows you to control and move water in amazing ways.", "water_class_icon"),
    BLOOD("Blood", "The blood class allows you to manipulate your enemies efficiently.", "blood_class_icon"),
    LAVA("Lava", "The lava class allows you harness the power of heat.", "lava_class_icon");

    private final String className;
    private final String description;
    private final String icon;

    ClassType(String name, String description, String icon) {
        this.className = name;
        this.description = description;
        this.icon = icon;
    }

    public String getClassName() {
        return className;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
