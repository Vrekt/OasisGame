package me.vrekt.oasis.save.player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.player.sp.PlayerSP;

import java.util.ArrayList;
import java.util.List;

/**
 * Save quest progress
 */
public final class PlayerQuestSave {

    @Expose
    @SerializedName("active_quests")
    private List<QuestSave> activeQuests;

    public PlayerQuestSave(PlayerSP player) {
        this.activeQuests = new ArrayList<>();

        player.getQuestManager().getActiveQuests().forEach((type, quest) -> {
            final QuestSave save = new QuestSave(type, quest);
            activeQuests.add(save);
        });
    }

    public List<QuestSave> activeQuests() {
        return activeQuests;
    }
}
