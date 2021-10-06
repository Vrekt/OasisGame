package me.vrekt.oasis.world.screen;

import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.ui.game.GameInterfaceAdapter;
import me.vrekt.oasis.world.asset.WorldAsset;

public abstract class AbstractWorldScreen extends GameInterfaceAdapter {

    // world stuff
    protected final WorldAsset asset;

    // quest handling
    protected final QuestManager quests;

    public AbstractWorldScreen(OasisGame game, WorldAsset asset) {
        super(game);
        this.asset = asset;
        this.quests = game.getQuestManager();
    }
}
