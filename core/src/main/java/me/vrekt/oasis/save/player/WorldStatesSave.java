package me.vrekt.oasis.save.player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.save.world.DefaultWorldSave;
import me.vrekt.oasis.save.world.InteriorSave;
import me.vrekt.oasis.save.world.WorldSave;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Parent world and interior world (if any) state save
 */
public final class WorldStatesSave {

    @Expose
    @SerializedName("world_in")
    private String worldIn;

    @Expose
    @SerializedName("parent_world")
    private String parentWorld;

    @Expose
    @SerializedName("in_interior")
    private boolean inInterior;

    @Expose
    @SerializedName("interior_type")
    private InteriorWorldType interiorType;

    @Expose
    @SerializedName("active_world_state")
    private WorldSave world;

    @Expose
    private Map<String, WorldSave> worlds;

    public WorldStatesSave(OasisGame game, PlayerSP player) {
        this.worldIn = player.getWorldState().getWorldName();
        this.inInterior = player.isInInteriorWorld();

        if (inInterior) {
            this.interiorType = player.getInteriorWorldIn().type();
            this.parentWorld = player.getInteriorWorldIn().getParentWorld().getWorldName();
        }

        this.world = inInterior ? new InteriorSave(player.getInteriorWorldIn()) : new DefaultWorldSave(player.getWorldState());
        this.worlds = new HashMap<>();

        for (GameWorld gameWorld : game.getWorldManager().worlds().values()) {
            // only save this world if visited and its not already set as the active world
            if (gameWorld.hasVisited() && !StringUtils.equals(this.world.name(), gameWorld.worldName())) {
                this.worlds.put(gameWorld.getWorldName(), new DefaultWorldSave(gameWorld));
            }
        }

    }

    public String worldIn() {
        return worldIn;
    }

    public String parentWorld() {
        return parentWorld;
    }

    public boolean inInterior() {
        return inInterior;
    }

    public InteriorWorldType interiorType() {
        return interiorType;
    }

    public Map<String, WorldSave> worlds() {
        return worlds;
    }

    public WorldSave world() {
        return world;
    }
}
