package me.vrekt.oasis.save.world;

import com.badlogic.gdx.math.Vector2;
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

    @Expose
    @SerializedName("entered_position")
    private Vector2 enteredPosition;

    public InteriorSave() {
    }

    public InteriorSave(GameWorldInterior world) {
        this.map = world.getWorldMap();
        this.name = world.getWorldName();
        this.interiorType = world.type();
        this.interior = true;
        this.enterable = world.isEnterable();
        this.enteredPosition = world.getGame().getWorldManager().parentWorldPosition();

        writeEntities(world);
        writeObjects(world);
    }

    public InteriorWorldType interiorType() {
        return interiorType;
    }

    public boolean enterable() {
        return enterable;
    }

    public Vector2 enteredPosition() {
        return enteredPosition;
    }
}
