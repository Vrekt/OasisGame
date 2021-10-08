package me.vrekt.oasis.world.screen;

import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.ui.game.GameInterfaceAdapter;

public abstract class AbstractWorldScreen extends GameInterfaceAdapter {

    // world stuff
    protected final Asset asset;

    // quest handling
    protected final QuestManager quests;

    public AbstractWorldScreen(OasisGame game, Asset asset) {
        super(game);
        this.asset = asset;
        this.quests = game.getQuestManager();
    }
}
