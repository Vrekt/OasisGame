package me.vrekt.oasis.classes;

public enum ClassType {

    EARTH("Earth", "The earth class allows you to manipulate the land around you."),
    NATURE("Nature", "The nature class allows you to harness the power of Mother Nature."),
    WATER("Water", "The water class allows you to control and move water in amazing ways."),
    BLOOD("Blood", "The blood class allows you to manipulate your enemies efficiently."),
    LAVA("Lava", "The lava class allows you harness the power of heat.");

    private final String className;
    private final String description;

    ClassType(String name, String description) {
        this.className = name;
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public String getDescription() {
        return description;
    }
}
