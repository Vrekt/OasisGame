package me.vrekt.oasis.save.world;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;

/**
 * Represents the data of an interior world.
 */
public final class InteriorWorldSave extends AbstractWorldSaveState {

    @Expose
    @SerializedName("type")
    private InteriorWorldType interiorType;

    @Expose
    private boolean enterable;

    @Expose
    @SerializedName("entered_position")
    private Vector2 enteredPosition;

    public InteriorWorldSave() {

    }

    public InteriorWorldSave(GameWorldInterior world) {
        this.map = world.getWorldMap();
        this.name = world.getWorldName();
        this.interiorType = world.type();
        this.interior = true;
        this.enterable = world.isEnterable();
        this.enteredPosition = world.getGame().getWorldManager().parentWorldPosition();

        writeEntities(world);
        writeObjects(world);
    }

    /**
     * @return type of
     */
    public InteriorWorldType interiorType() {
        return interiorType;
    }

    /**
     * @return if this interior is enterable.
     */
    public boolean enterable() {
        return enterable;
    }

    /**
     * @return the position of the parent world where the player entered.
     */
    public Vector2 enteredPosition() {
        return enteredPosition;
    }
}
