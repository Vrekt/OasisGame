package me.vrekt.oasis.save.player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.save.world.DefaultWorldSave;
import me.vrekt.oasis.save.world.InteriorSave;
import me.vrekt.oasis.save.world.WorldSave;
import me.vrekt.oasis.world.interior.InteriorWorldType;

/**
 * Parent world and interior world (if any) state save
 */
public final class ActiveWorldStateSave {

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
    @SerializedName("world")
    private WorldSave world;

    public ActiveWorldStateSave(PlayerSP player) {
        this.worldIn = player.getWorldState().getWorldName();
        this.inInterior = player.isInInteriorWorld();
        if (inInterior) {
            this.interiorType = player.getInteriorWorldIn().type();
            this.parentWorld = player.getInteriorWorldIn().getParentWorld().getWorldName();
        }

        this.world = inInterior ? new InteriorSave(player.getInteriorWorldIn()) : new DefaultWorldSave(player.getWorldState());
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

    public WorldSave world() {
        return world;
    }
}
