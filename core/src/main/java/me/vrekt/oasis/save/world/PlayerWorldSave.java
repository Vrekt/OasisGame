package me.vrekt.oasis.save.world;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.InteriorWorldType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents all the worlds that a player has visited and thus should be saved.
 */
public final class PlayerWorldSave {

    @Expose
    @SerializedName("world_in_name")
    private String worldIn;

    @Expose
    @SerializedName("parent_world_name")
    private String parentWorld;

    @Expose
    @SerializedName("is_in_interior")
    private boolean inInterior;

    @Expose
    @SerializedName("interior_type")
    private InteriorWorldType interiorType;

    @Expose
    @SerializedName("current_world_in")
    private AbstractWorldSaveState world;

    @Expose
    @SerializedName("visited_worlds")
    private Map<String, AbstractWorldSaveState> worlds;

    public PlayerWorldSave(OasisGame game, PlayerSP player) {
        this.worldIn = player.getWorldState().getWorldName();
        this.inInterior = player.getWorldState().isInterior();

        if (inInterior) {
            this.parentWorld = player.getInteriorWorldIn().getParentWorld().getWorldName();
            this.interiorType = player.getInteriorWorldIn().type();
            this.world = new InteriorWorldSave(player.getInteriorWorldIn());
        } else {
            this.world = new DefaultWorldSave(player.getWorldState());
        }

        this.worlds = new HashMap<>();
        // will only return normal worlds and not interior worlds
        for (GameWorld gameWorld : game.getWorldManager().worlds().values()) {
            // only save this world if visited, and it's not already set as the active world
            if (gameWorld.hasVisited() && !worldIn.equals(gameWorld.getWorldName())) {
                this.worlds.put(gameWorld.getWorldName(), new DefaultWorldSave(gameWorld, inInterior ? worldIn : null));
            }
        }
    }

    /**
     * @return world in name
     */
    public String worldIn() {
        return worldIn;
    }

    /**
     * @return parent world name
     */
    public String parentWorld() {
        return parentWorld;
    }

    /**
     * @return if we are in an interior
     */
    public boolean inInterior() {
        return inInterior;
    }

    /**
     * @return the interior type if any
     */
    public InteriorWorldType interiorType() {
        return interiorType;
    }

    /**
     * @return active world
     */
    public AbstractWorldSaveState world() {
        return world;
    }

    /**
     * @return all visited worlds, excluding active interior
     */
    public Map<String, AbstractWorldSaveState> worlds() {
        return worlds;
    }
}
