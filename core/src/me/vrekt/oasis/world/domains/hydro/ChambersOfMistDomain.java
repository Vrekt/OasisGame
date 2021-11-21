package me.vrekt.oasis.world.domains.hydro;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.mob.cicin.CicinSerpentMob;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.domains.AbstractDomain;
import me.vrekt.oasis.world.domains.DomainType;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

/**
 * A domain that houses the cicin serpent, rewards TBD.
 */
public final class ChambersOfMistDomain extends AbstractDomain {

    private final Vector2 mobSpawn = new Vector2();

    public ChambersOfMistDomain(DomainType domainType, OasisGame game, Vector2 entrance, AbstractWorld worldIn) {
        super(domainType, game, entrance, worldIn);
    }

    @Override
    protected void loadActions(MapLayer layer) {
        final RectangleMapObject object = (RectangleMapObject) layer.getObjects().get("MobSpawn");
        mobSpawn.set(object.getRectangle().x * GlobalGameRenderer.SCALE, object.getRectangle().y * GlobalGameRenderer.SCALE);
    }

    @Override
    protected void spawnEntities() {
        final CicinSerpentMob mob = new CicinSerpentMob("Placeholder", mobSpawn.x, mobSpawn.y, game, worldIn);
        mob.load(game.getAsset());

        addEntityInInterior(mob);
    }

    @Override
    protected void update() {

    }

    @Override
    protected void handleInteractionKeyPressed() {

    }
}
