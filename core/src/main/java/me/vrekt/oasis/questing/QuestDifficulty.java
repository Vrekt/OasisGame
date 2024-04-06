package me.vrekt.oasis.questing;

public enum QuestDifficulty {

    BEGINNER("beginner_quest_icon", "Beginner"),
    EASY("easy_quest_icon", "Easy"),
    MEDIUM("medium_quest_icon", "Medium"),
    HARD("hard_quest_icon", "Hard"),
    MASTER("master_quest_icon","Master");

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
