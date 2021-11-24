package me.vrekt.oasis.quest.type;

public enum QuestSection {

    ORIGINS_OF_HUNNEWELL("Origins of Hunnewell");

    private final String name;

    QuestSection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
