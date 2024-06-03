package me.vrekt.oasis.save.world.player;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.save.world.inventory.InventorySave;

import java.util.LinkedList;

/**
 * Player save
 */
public final class PlayerSave {

    @Expose
    private String name;

    @Expose
    private Vector2 position;

    @Expose
    InventorySave inventory;

    @Expose
    @SerializedName("artifact_inventory")
    PlayerArtifactSave artifactInventory;

    @Expose
    PlayerQuestSave quests;

    @Expose
    @SerializedName("active_effects")
    private LinkedList<EffectSave> activeEffects;

    public PlayerSave(PlayerSP player) {
        this.name = player.name();
        this.position = player.getPosition();
        this.inventory = new InventorySave(player.getInventory());
        this.artifactInventory = new PlayerArtifactSave(player);
        this.quests = new PlayerQuestSave(player);

        if (player.activeEffect() != null) {
            activeEffects = new LinkedList<>();
            activeEffects.add(new EffectSave(player.activeEffect()));
        }
    }
}
