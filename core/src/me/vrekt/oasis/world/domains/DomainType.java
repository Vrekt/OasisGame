package me.vrekt.oasis.world.domains;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.domains.hydro.ChambersOfMistDomain;

/**
 * All domain types within the entire game
 */
public enum DomainType {

    /**
     * Houses the cicin' serpent.
     */
    CHAMBERS_OF_MIST("Chambers Of Mist", "domains/ChambersOfMist.tmx") {
        @Override
        public AbstractDomain createDomain(OasisGame game, Vector2 entrance, AbstractWorld worldIn) {
            return new ChambersOfMistDomain(CHAMBERS_OF_MIST, game, entrance, worldIn);
        }
    };

    private final String prettyName;
    private final String resource;

    DomainType(String prettyName, String resource) {
        this.prettyName = prettyName;
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public abstract AbstractDomain createDomain(OasisGame game, Vector2 entrance, AbstractWorld worldIn);

}
