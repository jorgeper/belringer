package belringer.maps;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

class CityBlocks {
    private final List<CityBlock> blocks = new ArrayList<>();

    CityBlocks(Context context, CityModel city) {
        for (int i = 0; i < CityModel.AVENUE_COUNT; ++i) {
            for (int j = 0; j < CityModel.STREET_COUNT; ++j) {
                CityBlock block = new CityBlock(context, i + 1, j + 1, city);
                blocks.add(block);
            }
        }
    }

    void draw(float[] vPMatrix) {
        for (CityBlock block : blocks) {
            block.draw(vPMatrix);
        }
    }
}
