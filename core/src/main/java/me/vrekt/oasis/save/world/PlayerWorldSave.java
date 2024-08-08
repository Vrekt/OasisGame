package me.vrekt.oasis.save.world;

import com.badlogic.gdx.utils.Disposable;
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
public final class PlayerWorldSave implements Disposable {

    @Expose
    @SerializedName("world_in_id")
    private int worldIn;

    @Expose
    @SerializedName("parent_world_id")
    private int parentWorld;

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
    private Map<Integer, AbstractWorldSaveState> worlds;

    public PlayerWorldSave(OasisGame game, PlayerSP player) {
        this.worldIn = player.getWorldState().worldId();
        this.inInterior = player.getWorldState().isInterior();

        if (inInterior) {
            this.parentWorld = player.getInteriorWorldIn().getParentWorld().worldId();
            this.interiorType = player.getInteriorWorldIn().type();
            this.world = new InteriorWorldSave(player.getInteriorWorldIn());
        } else {
            this.world = new DefaultWorldSave(player.getWorldState());
            this.parentWorld = -1;
        }

        this.worlds = new HashMap<>();
        // will only return normal worlds and not interior worlds 
        for (GameWorld gameWorld : game.worldManager().worlds().values()) {
            // only save this world if visited, and it's not already set as the active world
            if (gameWorld.hasVisited() && worldIn != gameWorld.worldId()) {
                this.worlds.put(gameWorld.worldId(), new DefaultWorldSave(gameWorld, inInterior ? worldIn : -1));
            }
        }
    }

    /**
     * @return world in ID
     */
    public int worldIn() {
        return worldIn;
    }

    /**
     * @return parent world ID
     */
    public int parentWorld() {
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
    public Map<Integer, AbstractWorldSaveState> worlds() {
        return worlds;
    }

    @Override
    public void dispose() {
        world.dispose();
        worlds.values().forEach(AbstractWorldSaveState::dispose);
        worlds.clear();
    }
}
