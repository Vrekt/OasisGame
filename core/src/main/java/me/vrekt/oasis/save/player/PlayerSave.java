package me.vrekt.oasis.save.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.item.artifact.ArtifactType;
import me.vrekt.oasis.questing.Quest;
import me.vrekt.oasis.questing.QuestObjective;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.save.inventory.InventorySave;
import me.vrekt.oasis.save.world.PlayerWorldSave;
import me.vrekt.oasis.world.effects.Effect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * All player data and the worlds they have visited.
 */
public final class PlayerSave implements Disposable {

    @Expose
    private String name;

    @Expose
    private Vector2 position;

    @Expose
    private InventorySave inventory;

    @Expose
    @SerializedName("all_worlds_and_state")
    private PlayerWorldSave worldSave;

    @Expose
    @SerializedName("artifacts")
    private LinkedList<PlayerArtifacts> artifacts;

    @Expose
    @SerializedName("active_quests")
    private List<PlayerQuests> activeQuests;

    @Expose
    @SerializedName("active_effects")
    private LinkedList<Effect> activeEffects;

    public PlayerSave(OasisGame game, PlayerSP player) {
        this.name = player.name();
        this.position = player.getPosition();
        this.inventory = new InventorySave(player.getInventory());
        this.worldSave = new PlayerWorldSave(game, player);

        saveArtifacts(player);
        saveEffects(player);
        saveQuests(player);
    }

    /**
     * Save artifacts if any
     *
     * @param player player
     */
    private void saveArtifacts(PlayerSP player) {
        if (player.getArtifacts().isEmpty()) return;

        this.artifacts = new LinkedList<>();
        for (IntMap.Entry<Artifact> artifact : player.getArtifacts()) {
            this.artifacts.add(new PlayerArtifacts(artifact.value.type(), artifact.key, artifact.value.level()));
        }
    }

    /**
     * Save effects if any
     *
     * @param player player
     */
    private void saveEffects(PlayerSP player) {
        if (player.activeEffect() != null) {
            activeEffects = new LinkedList<>();
            final JsonObject data = new JsonObject();
            player.activeEffect().save(data, SaveManager.SAVE_GAME_GSON);

            activeEffects.add(player.activeEffect());
        }
    }

    /**
     * Save all active quests, hopefully more than 1.
     *
     * @param player player
     */
    private void saveQuests(PlayerSP player) {
        this.activeQuests = new ArrayList<>();
        for (Map.Entry<QuestType, Quest> entry : player.getQuestManager().getActiveQuests().entrySet()) {
            this.activeQuests.add(new PlayerQuests(
                    entry.getKey(),
                    entry.getValue().objectives(),
                    entry.getValue().currentObjectiveStep())
            );
        }
    }

    /**
     * @return the name of the player
     */
    public String name() {
        return name;
    }

    /**
     * @return the position
     */
    public Vector2 position() {
        return position;
    }

    /**
     * @return the inventory
     */
    public InventorySave inventory() {
        return inventory;
    }

    /**
     * @return world state and visited worlds.
     */
    public PlayerWorldSave worldSave() {
        return worldSave;
    }

    /**
     * @return artifact inventory
     */
    public LinkedList<PlayerArtifacts> artifacts() {
        return artifacts;
    }

    /**
     * @return active quests
     */
    public List<PlayerQuests> quests() {
        return activeQuests;
    }

    /**
     * @return active effects
     */
    public LinkedList<Effect> effects() {
        return activeEffects;
    }

    /**
     * Player artifact storage
     *
     * @param type  the type
     * @param slot  the slot it is in
     * @param level the level
     */
    public record PlayerArtifacts(ArtifactType type, int slot, int level) {
    }

    /**
     * Player active quest storage
     *
     * @param type       type
     * @param objectives list of objectives
     * @param index      current quest step/index
     */
    public record PlayerQuests(QuestType type, LinkedList<QuestObjective> objectives, int index) {

    }

    @Override
    public void dispose() {
        name = null;
        position = null;
        inventory.dispose();
        inventory = null;
        worldSave.dispose();
        worldSave = null;

        if (artifacts != null) artifacts.clear();
        if (activeQuests != null) activeQuests.clear();
        if (activeEffects != null) activeEffects.clear();
    }
}
