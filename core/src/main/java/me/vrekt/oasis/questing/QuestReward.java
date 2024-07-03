package me.vrekt.oasis.questing;

import me.vrekt.oasis.item.utility.ItemDescriptor;

public record QuestReward(QuestRewardType type, int amount, ItemDescriptor descriptor) {

    public static final ItemDescriptor XP_DESCRIPTOR = new ItemDescriptor("quest_reward_tack", "XP");

}
