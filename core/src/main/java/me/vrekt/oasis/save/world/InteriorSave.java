package me.vrekt.oasis.save.world;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;

/**
 * Save data of an interior world
 */
public final class InteriorSave extends WorldSave {

    @Expose
    @SerializedName("type")
    private InteriorWorldType interiorType;

    @Expose
    private boolean enterable;

    public InteriorSave() {
    }

    public InteriorSave(GameWorldInterior world) {
        this.map = world.getWorldMap();
        this.name = world.getWorldName();
        this.interiorType = world.type();
        this.interior = true;
        this.enterable = world.isEnterable();

        writeEntities(world);
        writeContainers(world);
    }
}
