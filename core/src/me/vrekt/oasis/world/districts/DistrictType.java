package me.vrekt.oasis.world.districts;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.districts.hydro.ChambersOfMistDistrict;

/**
 * All domain types within the entire game
 */
public enum DistrictType {

    /**
     * Houses the cicin' serpent.
     */
    CHAMBERS_OF_MIST("Chambers Of Mist", "domains/ChambersOfMist.tmx") {
        @Override
        public AbstractDistrict createDomain(OasisGame game, Vector2 entrance, AbstractWorld worldIn) {
            return new ChambersOfMistDistrict(CHAMBERS_OF_MIST, game, entrance, worldIn);
        }
    };

    private final String prettyName;
    private final String resource;

    DistrictType(String prettyName, String resource) {
        this.prettyName = prettyName;
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public abstract AbstractDistrict createDomain(OasisGame game, Vector2 entrance, AbstractWorld worldIn);

}
