package belringer.maps;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;

class Roads {

    private final Plane road;
    private final Color streetColor;
    private Color fogColor;

    Roads(Context context, Color streetColor, Color fogColor) {
        road = Plane.quad(context, Constants.CITY_BLOCK_WIDTH, Constants.ROAD_WIDTH);
        this.streetColor = streetColor;
        this.fogColor = fogColor;
    }

    void draw(float[] vPMatrix, Color fogColor) {
        this.fogColor = fogColor;
        for (int i = 0; i < Constants.AVENUE_COUNT; ++i) {
            for (int j = 0; j < Constants.STREET_COUNT; ++j) {
                drawRoad(i, j, vPMatrix, /* isStreet= */ true);
                drawRoad(i, j, vPMatrix, /* isStreet= */ false);
            }
        }
    }

    private void drawRoad(int i, int j, float[] vPMatrix, boolean isStreet) {
        float[] translationM = new float[16];
        Matrix.setIdentityM(translationM, 0);
        Matrix.translateM(translationM, 0, i * Constants.CITY_BLOCK_WIDTH + Constants.ROAD_WIDTH / 2.f, 0, j * Constants.CITY_BLOCK_WIDTH - Constants.ROAD_WIDTH /2.f);
        float[] scratchM = new float[16];

        float[] rotationM = new float[16];
        if (!isStreet) {
            Matrix.setRotateM(rotationM, 0, -90.f, 0, 1.f, 0.f);
        } else {
            Matrix.setIdentityM(rotationM, 0);
        }

        Matrix.multiplyMM(scratchM, 0, translationM, 0, rotationM, 0);
        Matrix.multiplyMM(scratchM, 0, vPMatrix, 0, scratchM, 0);

        road.draw(scratchM, streetColor, fogColor);

    }
}
