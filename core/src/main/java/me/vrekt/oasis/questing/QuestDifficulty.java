package me.vrekt.oasis.questing;

public enum QuestDifficulty {

    BEGINNER("beginner_quest_difficulty", "Beginner"),
    EASY("easy_quest_difficulty", "Easy"),
    MEDIUM("medium_quest_difficulty", "Medium"),
    HARD("hard_quest_difficulty", "Hard"),
    MASTER("master_quest_difficulty", "Master");

    private final String asset, prettyName;

    QuestDifficulty(String asset, String prettyName) {
        this.asset = asset;
        this.prettyName = prettyName;
    }

    public String getAsset() {
        return asset;
    }


    public String getPrettyName() {
        return prettyName;
    }
}
