package com.example.opengltest;

import android.opengl.Matrix;

public class CameraOld {
    private static final float speed = 5.f;

    private float[] currentLookAt = new float[] { 0.f, 0.f, 6.f};
    private float[] nextLookAt = new float[] { 0.f, 0.f, 6.f};
    private long lastUpdateTime;
    private boolean isMoving;

    public CameraOld(long time, float x, float y, float z) {
        lastUpdateTime = time;
        isMoving = false;
        currentLookAt[0] = x;
        currentLookAt[1] = y;
        currentLookAt[2] = z;
        nextLookAt[0] = x;
        nextLookAt[1] = y;
        nextLookAt[2] = z;
    }

    public void moveTo(float x, float y, float z) {
        nextLookAt[0] = x;
        nextLookAt[1] = y;
        nextLookAt[2] = z;
        isMoving = true;
    }

    public void moveBy(float[] delta) {
        nextLookAt = VectorUtil.add(nextLookAt, delta, nextLookAt);
        isMoving = true;
    }

    private static final float DISTANCE_FROM_LOCATION = 6.0f;
    private static final float CAMERA_HEIGHT = 4.0f;
    private static final float LOCATION_AERIAL_HEIGHT = 15.f;

//    public void lookAtLocation2(float[] xzr, float[] viewMatrix) {
//        double angle = Math.toRadians(xzr[2] + 90); // add 90 since 0 degree == north
//        float[] pos = new float[] { xzr[0] + (float) Math.cos(angle) * DISTANCE_FROM_LOCATION, CAMERA_HEIGHT, xzr[1] + (float) Math.sin(angle) * DISTANCE_FROM_LOCATION};
//
//        currentLookAt[0] = xzr[0];
//        currentLookAt[1] = 0.f;
//        currentLookAt[2] = xzr[1];
//
//        Matrix.setLookAtM(viewMatrix, 0, pos[0], pos[1], pos[2], currentLookAt[0], currentLookAt[1], currentLookAt[2], 0f, 1.0f, 0.0f);
//    }

    private static float[] calculateUp(float[] lookAt, float[] pos, float[] up) {
        float[] y = new float[] {0, 1, 0};
        float[] lookDown = new float[] {0, -1, 0};
        float[] eye = VectorUtil.sub(lookAt, pos, new float[3]);
        float[] eyeNorm = VectorUtil.norm(eye, new float[3]);

        // Cheating, if looking straight down, special case, cross product would be zero.
        if (VectorUtil.equals(eyeNorm, lookDown)) {
            return VectorUtil.set(up, 0f, 0f, -1.0f);
        }
        float[] scratch = VectorUtil.cross(eyeNorm, y, new float[3]);
        return VectorUtil.cross(scratch, eyeNorm, up);
    }

    public void lookAtLocation(float[] lookAt, float lookAtAngle, float[] viewMatrix) {
        double angle = Math.toRadians(lookAtAngle + 90); // add 90 since 0 degree == north
        float[] pos = new float[] { lookAt[0] + (float) Math.cos(angle) * DISTANCE_FROM_LOCATION, CAMERA_HEIGHT, lookAt[2] + (float) Math.sin(angle) * DISTANCE_FROM_LOCATION};

        currentLookAt[0] = lookAt[0];
        currentLookAt[1] = lookAt[1];
        currentLookAt[2] = lookAt[2];

        float[] up = calculateUp(lookAt, pos, new float[3]);
//        Matrix.setLookAtM(viewMatrix, 0, pos[0], pos[1], pos[2], currentLookAt[0], currentLookAt[1], currentLookAt[2], 0f, 1.0f, 0.0f);
        Matrix.setLookAtM(viewMatrix, 0, pos[0], pos[1], pos[2], currentLookAt[0], currentLookAt[1], currentLookAt[2], up[0], up[1], up[2]);
    }

    public void lookAtLocationAerial(float[] lookAt, float[] viewMatrix) {
        float[] pos = new float[] { lookAt[0], lookAt[1] + LOCATION_AERIAL_HEIGHT, lookAt[2]};

        currentLookAt[0] = lookAt[0];
        currentLookAt[1] = lookAt[1];
        currentLookAt[2] = lookAt[2];

        float[] up = calculateUp(lookAt, pos, new float[3]);
        Matrix.setLookAtM(viewMatrix, 0, pos[0], pos[1], pos[2], currentLookAt[0], currentLookAt[1], currentLookAt[2],  up[0], up[1], up[2]);
        //Matrix.setLookAtM(viewMatrix, 0, pos[0], pos[1], pos[2], currentLookAt[0], currentLookAt[1], currentLookAt[2], 0f, 0f, -1.0f);
    }

    public boolean tick(long time) {
        if (time <= lastUpdateTime || !isMoving) {
            lastUpdateTime = time;
            return false;
        }
        float dt = (time - lastUpdateTime) / 1000.f;
        float dd = speed * dt;
        float[] newCurrentPos = new float[3];
        currentLookAt = VectorUtil.lerp(currentLookAt, nextLookAt, dd, newCurrentPos);
        if (VectorUtil.equals(currentLookAt, nextLookAt)) {
            currentLookAt = nextLookAt;
            isMoving = false;
        }
        //Log.i("jorgeper", "Camera tick, dt: " + dt + ", moving: " + isMoving + ", cur: " + VectorUtil.toString(currentLookAt) + ", next: " + VectorUtil.toString(nextLookAt));
        lastUpdateTime = time;
        return true;
    }

    public float[] getLookAt() {
        return currentLookAt;
    }
}
