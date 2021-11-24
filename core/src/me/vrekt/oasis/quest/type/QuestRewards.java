package me.vrekt.oasis.quest.type;

public enum QuestRewards {

    ETHE("coin"), COMMON_XP_BOOK("xp_book_common");

    private final String texture;

    QuestRewards(String texture) {
        this.texture = texture;
    }

    public String getTexture() {
        return texture;
    }
}
