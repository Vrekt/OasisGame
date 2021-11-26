package me.vrekt.oasis.world.districts.hydro;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.mob.cicin.CicinSerpentMob;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.districts.AbstractDistrict;
import me.vrekt.oasis.world.districts.DistrictReward;
import me.vrekt.oasis.world.districts.DistrictType;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

/**
 * A domain that houses the cicin serpent, rewards TBD.
 */
public final class ChambersOfMistDistrict extends AbstractDistrict {

    private final Vector2 mobSpawn = new Vector2();

    public ChambersOfMistDistrict(DistrictType districtType, OasisGame game, Vector2 entrance, AbstractWorld worldIn) {
        super(districtType, game, entrance, worldIn);

        setDescription("Chambers Of Mist was carved from centuries of rain. Born from water, the Cicin Serpent feeds on anybody who enters the chamber.");
        addLevel("Revenge of the Serpent I", 10);
        addLevel("Revenge of the Serpent II", 25);
        addLevel("Revenge of the Serpent III", 60);
        addReward(DistrictReward.LIRA, 5000);
        addReward(DistrictReward.WAND_OF_EMBRACING, 1);
        addReward(DistrictReward.AMBITIOUS_MEDICS_BOX, 1);
    }

    @Override
    protected void loadActions(MapLayer layer) {
        final RectangleMapObject object = (RectangleMapObject) layer.getObjects().get("MobSpawn");
        mobSpawn.set(object.getRectangle().x * GlobalGameRenderer.SCALE, object.getRectangle().y * GlobalGameRenderer.SCALE);
    }

    @Override
    protected void spawnEntities() {
        final CicinSerpentMob mob = new CicinSerpentMob("Cicin Serpent", mobSpawn.x, mobSpawn.y, game, worldIn);
        mob.loadEntity(game.getAsset());
        addEntity(mob);

        switch (level) {
            case 1:
                mob.setLevel(10);
                mob.setHealth(100);
                break;
            case 2:
                mob.setLevel(20);
                mob.setHealth(400);
                break;
            case 3:
                mob.setLevel(30);
                mob.setHealth(800);
                break;
        }
    }

    @Override
    protected void update() {

    }

    @Override
    protected void handleInteractionKeyPressed() {

    }
}
