package me.vrekt.oasis.quest.quests;

public enum QuestDifficulty {

    BEGINNER("Beginner"), EASY("Easy"), MEDIUM("Medium"), HARD("Hard");

    private final String name;

    QuestDifficulty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
