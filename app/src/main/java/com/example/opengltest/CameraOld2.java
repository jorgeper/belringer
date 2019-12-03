package com.example.opengltest;

import android.opengl.Matrix;
import android.os.SystemClock;

public class CameraOld2 {
    private static final float ROTATION_SPEED = 180.f; // degrees/second
    private static final float CAMERA_SPEED = 12.f; // units/second
    private static final float speed = 5.f;

    private float[] currentLookAt = new float[]{0.f, 0.f, 6.f};
    private float[] nextLookAt = new float[]{0.f, 0.f, 6.f};
    private long lastUpdateTime;
    private float[] pos = new float[3]; // y always 0
    private float[] nextPos = new float[3];
    private float[] dir = new float[3];
    private float[] nextDir = new float[3];

    public CameraOld2(long time, float x, float y, float z) {
        lastUpdateTime = time;
        currentLookAt[0] = x;
        currentLookAt[1] = y;
        currentLookAt[2] = z;
    }

    private static final float DISTANCE_FROM_LOCATION = 6.0f;
    private static final float CAMERA_HEIGHT = 4.0f;
    private static final float LOCATION_AERIAL_HEIGHT = 15.f;

    private static float[] calculateUp(float[] lookAt, float[] pos, float[] up) {
        float[] y = new float[]{0, 1, 0};
        float[] lookDown = new float[]{0, -1, 0};
        float[] eye = VectorUtil.sub(lookAt, pos, new float[3]);
        float[] eyeNorm = VectorUtil.norm(eye, new float[3]);

        // Cheating, if looking straight down, special case, cross product would be zero.
        if (VectorUtil.equals(eyeNorm, lookDown)) {
            return VectorUtil.set(up, 0f, 0f, -1.0f);
        }
        float[] scratch = VectorUtil.cross(eyeNorm, y, new float[3]);
        return VectorUtil.cross(scratch, eyeNorm, up);
    }

    private static float[] calculateUp2(float[] lookAt, float[] pos, float[] dir, float[] up) {
        // rotate location direction 90 degrees clock wise
        // 90 deg rot: x = y, y = -x --> x = -z, z = x
        float[] dirRotated = new float[] { -dir[2], dir[1], dir[0] };

        // cross of rotated dir and pos -> lookAt vector is up
        float[] eye = VectorUtil.sub(lookAt, pos, new float[3]);
        float[] eyeNorm = VectorUtil.norm(eye, new float[3]);

        VectorUtil.cross(dirRotated, eyeNorm, up);
        return up;
    }

//    public void lookAt(float[] lookAt, float lookAtAngle, boolean animate) {
//        double angle = Math.toRadians(lookAtAngle + 90); // add 90 since 0 degree == north
//        nextPos = new float[]{ lookAt[0] + (float) Math.cos(angle) * DISTANCE_FROM_LOCATION, CAMERA_HEIGHT, lookAt[2] + (float) Math.sin(angle) * DISTANCE_FROM_LOCATION};
//        if (!animate) {
//            VectorUtil.set(pos, nextPos);
//        }
//        isDirty = true;
//        lastUpdateTime = SystemClock.elapsedRealtime();
//
//        VectorUtil.set(currentLookAt, lookAt);
//    }

    public void lookAt(float[] lookAt, float[] dir, boolean animate) {
        animateRotationFirst = true;
        VectorUtil.set(nextDir, dir);
        nextPos = new float[] {lookAt[0] -dir[0] * DISTANCE_FROM_LOCATION, lookAt[1] + CAMERA_HEIGHT, lookAt[2] -dir[2] * DISTANCE_FROM_LOCATION };
        //nextPos = new float[]{ lookAt[0] + (float) Math.cos(angle) * DISTANCE_FROM_LOCATION, CAMERA_HEIGHT, lookAt[2] + (float) Math.sin(angle) * DISTANCE_FROM_LOCATION};
        if (!animate) {
            VectorUtil.set(pos, nextPos);
            VectorUtil.set(this.dir, nextDir);
        }
        isDirty = true;
        lastUpdateTime = SystemClock.elapsedRealtime();

        VectorUtil.set(currentLookAt, lookAt);
        //VectorUtil.set(this.dir, dir);
    }

    private boolean isDirty = true;
    private boolean animateRotationFirst;

    // TODO: combine with lookAt plus param
    public void lookAtAerial(float[] lookAt,float[] dir,  boolean animate) {
        animateRotationFirst = false;
        VectorUtil.set(nextDir, new float[] { 0, 0, -1});
        nextPos = new float[] { lookAt[0], lookAt[1] + LOCATION_AERIAL_HEIGHT, lookAt[2]};

        // TODO: animate camera up.
        //double angle = Math.toRadians(lookAtAngle + 90); // add 90 since 0 degree == north
        if (!animate) {
            VectorUtil.set(pos, nextPos);
            VectorUtil.set(this.dir, nextDir);
        }
        isDirty = true;
        lastUpdateTime = SystemClock.elapsedRealtime();

        VectorUtil.set(currentLookAt, lookAt);
        //VectorUtil.set(this.dir, dir);
    }

    public boolean tick(long time) {
        if (time <= lastUpdateTime) {
            lastUpdateTime = time;
            return false;
        }

        if (!isDirty) {
            return false;
        }

        float dt = (time - lastUpdateTime) / 1000.f;
        float dd = CAMERA_SPEED * dt;

        if (!animateRotationFirst) {
            isDirty = animatePosition(dd);
            if (!isDirty) {
                isDirty = animateRotation(dd);
            }
        } else {
            isDirty = animateRotation(dd);
            if (!isDirty) {
                isDirty = animatePosition(dd);
            }
        }

        lastUpdateTime = time;

        return true;
    }

    private boolean animatePosition(float dd) {
        pos = VectorUtil.lerp(pos, nextPos, dd, new float[3]); // TODO: no need to create new float array
        if (VectorUtil.equals(nextPos, pos, 3)) {
            VectorUtil.set(pos, nextPos);
            return false;
        }
        return true;
    }

    private boolean animateRotation(float dd) {
        dir = VectorUtil.lerp(dir, nextDir, dd, new float[3]);
        if (VectorUtil.equals(nextDir, dir, 3)) {
            VectorUtil.set(dir, nextDir);
            return false;
        }
        return true;
    }

    public void set(float[] viewMatrix) {
        float[] up = calculateUp2(currentLookAt, pos, dir, new float[3]);
        Matrix.setLookAtM(viewMatrix, 0, pos[0], pos[1], pos[2], currentLookAt[0], currentLookAt[1], currentLookAt[2], up[0], up[1], up[2]);

    }
}