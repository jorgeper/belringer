package com.example.opengltest;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;

import androidx.annotation.ColorInt;

public class Roads {
    private static final int AVENUE_COUNT = 25;
    private static final int STREET_COUNT = 25;
    private static final float BLOCK_WIDTH = 8;
    private static final float ROAD_WIDTH =0.3f;

    private final Plane road;
    private final Color streetColor;
    private Color fogColor;

    public Roads(Context context, Color streetColor, Color fogColor) {
        road = Plane.quad(context, BLOCK_WIDTH, ROAD_WIDTH);
        this.streetColor = streetColor;
        this.fogColor = fogColor;
    }

    public void draw(float[] vPMatrix, Color fogColor) {
        this.fogColor = fogColor;
        for (int i = 0; i < AVENUE_COUNT; ++i) {
            for (int j = 0; j < STREET_COUNT; ++j) {
                drawRoad(i, j, vPMatrix, /* isStreet= */ true);
                drawRoad(i, j, vPMatrix, /* isStreet= */ false);
            }
        }
    }

    private void drawRoad(int i, int j, float[] vPMatrix, boolean isStreet) {
        float[] translationM = new float[16];
        Matrix.setIdentityM(translationM, 0);
        Matrix.translateM(translationM, 0, i * BLOCK_WIDTH + ROAD_WIDTH /2.f, 0, j * BLOCK_WIDTH - ROAD_WIDTH /2.f);
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
