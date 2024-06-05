package me.vrekt.oasis.save.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.save.inventory.InventorySave;
import me.vrekt.oasis.save.world.PlayerWorldSave;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * All player data and the worlds they have visited.
 */
public final class PlayerSave {

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
    @SerializedName("artifact_inventory")
    private LinkedList<ArtifactSave> artifacts;

    @Expose
    @SerializedName("active_quests")
    private List<QuestSave> activeQuests;

    @Expose
    @SerializedName("active_effects")
    private LinkedList<EffectSave> activeEffects;

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
        for (IntMap.Entry<Artifact> entry : player.getArtifacts()) {
            artifacts.add(new ArtifactSave(entry.key, entry.value));
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
            activeEffects.add(new EffectSave(player.activeEffect()));
        }
    }

    /**
     * Save all active quests, hopefully more than 1.
     *
     * @param player player
     */
    private void saveQuests(PlayerSP player) {
        this.activeQuests = new ArrayList<>();
        player.getQuestManager().getActiveQuests().forEach((type, quest) -> {
            final QuestSave save = new QuestSave(type, quest);
            activeQuests.add(save);
        });
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
    public LinkedList<ArtifactSave> artifacts() {
        return artifacts;
    }

    /**
     * @return active quests
     */
    public List<QuestSave> quests() {
        return activeQuests;
    }

    /**
     * @return active effects
     */
    public LinkedList<EffectSave> effects() {
        return activeEffects;
    }
}
