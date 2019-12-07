package belringer.maps;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class CityBlock {
    private class BuildingSpec {
        float x;
        float z;
        float width;
        float height;
    }

    private final List<Building> buildings;
    private final Random rand = new Random();
    private final HashMap<Integer, BuildingSpec> specs = new HashMap<>();

    CityBlock(Context context, int street, int avenue, CityModel city) {
        buildings = new ArrayList<>();

        int buildindsPerBlockStreet = Math.max(2, rand.nextInt(CityModel.BUILDINGS_PER_BLOCK_STREET + 1));
        int buildingsPerBlockAvenue = Math.max(3, rand.nextInt(CityModel.BUILDINGS_PER_BLOCK_AVENUE + 1));

        float maxBuildingWidth = (city.streetBlockWidth() / buildindsPerBlockStreet) - CityModel.BUILDING_ALLEY_WIDTH;
        float maxBuildingHeight = (city.avenueBlockHeight() / buildingsPerBlockAvenue) - CityModel.BUILDING_ALLEY_WIDTH;

        float halfRoad = Constants.ROAD_WIDTH / 2.f;
        float z = city.streetZ(street) + halfRoad + Constants.SIDEWALK_WIDTH;
        for (int i = 0; i < buildingsPerBlockAvenue; ++i) {
            buildindsPerBlockStreet = Math.max(2, rand.nextInt(CityModel.BUILDINGS_PER_BLOCK_STREET + 1));
            maxBuildingWidth = (city.streetBlockWidth() / buildindsPerBlockStreet) - CityModel.BUILDING_ALLEY_WIDTH;
            float x = city.avenueX(avenue) + halfRoad + Constants.SIDEWALK_WIDTH;

            for (int j = 0; j < buildindsPerBlockStreet; ++j) {

                if ((j == 0 || j == buildindsPerBlockStreet - 1) || (
                        (i == 0 || i == buildingsPerBlockAvenue - 1))) {
                    float rd1 = rand.nextFloat() * 0.2f;
                    float rd2 = rand.nextFloat() * 0.2f;
                    float rd3 = rand.nextFloat() * 0.21f;
                    float rd4 = rand.nextFloat() * 0.2f;

                    boolean coallesce = rand.nextBoolean();

                    Building building = new Building(context, null, x + rd1, z + rd2, maxBuildingWidth - rd1 - rd3, maxBuildingHeight - rd2 - rd4);
                    buildings.add(building);
                }
                x += maxBuildingWidth + CityModel.BUILDING_ALLEY_WIDTH;
            }
            z += maxBuildingHeight+ CityModel.BUILDING_ALLEY_WIDTH;
        }
    }

    private float getRandom(float maxSize, float maxDelta) {
        return maxSize - maxDelta * rand.nextFloat();
    }

    void draw(float[] vPMatrix) {
        for (Building building : buildings) {
            building.draw(vPMatrix);
        }
    }
}
