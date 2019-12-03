package com.example.opengltest;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

public class Location {
    private static final float ANGLE_SPEED = 180.f; // degrees/second
    private static final float SPEED = 1.f; // units/second
    private static final float HALF_WIDTH = 0.5f;
    private final TexturedQuad iconQuad;
    private final TexturedQuad haloQuad;

    private float[] pos = new float[3]; // y always 0
    private float[] nextPos = new float[3];
    private float angle;
    private float nextAngle;
    private float[] direction = new float[3];

    public Location(Context context) {
        iconQuad = new TexturedQuad(context, R.drawable.location_icon);
        haloQuad = new TexturedQuad(context, R.drawable.location_halo);
        line = new Line();
    }

    public void moveTo(float x, float z, boolean animate) {
        nextPos[0] = x;
        nextPos[1] = 0.f;
        nextPos[2] = z;

        if (!animate) {
            VectorUtil.set(pos, nextPos);
        }
        isDirty = true;
        //notifyChange();
    }

    public void moveTo2(float x, float z, boolean animate) {
        nextPos[0] = x;
        nextPos[1] = 0.f;
        nextPos[2] = z;

        if (!animate) {
            VectorUtil.set(pos, nextPos);
        }

        // Normalized next - pos vector.
        float[] sub = new float[3];
        VectorUtil.sub(nextPos, pos, sub);
//        VectorUtil.div(sub, length, sub);

        float[] north = new float[] { 0.f, 0.f, -1.f};

        // Subtract from 90 as the angle is with the x axis.
        //float alpha = 90.f - (float) Math.toDegrees(Math.acos(nextPos[0] - pos[0] / length));

        float alpha = (float) Math.toDegrees(Math.atan2(sub[0], -sub[2]));
        Log.i("jorgeper", "angle: " + alpha);
        setAngle(alpha, animate);

        isDirty = true;
        //notifyChange();
    }

    public void moveBy(float dx, float dz, boolean animate) {
        nextPos[0] = pos[0] + dx;
        nextPos[1] = 0.f;
        nextPos[2] = pos[2] + dz;
        //Log.i("jorgeper", "move: "  + ", animate: " + animate + ", " + VectorUtil.toString(pos) + ", nextPos: " + VectorUtil.toString(nextPos));
        if (!animate) {
            pos = nextPos;
        }
        isDirty = true;
        lastUpdateTime = SystemClock.elapsedRealtime();
        //notifyChange();
    }

    public void setAngle(float angle, boolean animate) {
        nextAngle = angle;
        lastUpdateTime = SystemClock.elapsedRealtime();
        if (!animate) {
            this.angle = nextAngle;
        }
        isDirty = true;
        //notifyChange();
    }

    private void notifyChange() {
        if (listener != null) {
            listener.onLocationChanged();
        }
    }

    public float[] getPos() {
        return pos;
    }

    public float getAngle() {
        return angle;
    }

    public float[] getDirection() {
        double cos = Math.cos(Math.toRadians(90 - angle));
        double sin = Math.sin(Math.toRadians(90 - angle));
        direction[0] = (float) cos;
        direction[1] = 0;
        direction[2] = (float) -sin;
        return direction;
    }

    private boolean isDirty = true;
    private long lastUpdateTime = 0;
    private static final int HALO_PULSE_PERIOD = 2000; // 2 seconds per full pulse
    private static final float HALO_PULSE_RANGE = 0.15f;

    private float haloScale = 1.0f;

    public boolean tick(long time) {
        if (time <= lastUpdateTime) {
            lastUpdateTime = time;
            return false;
        }

//        if (VectorUtil.equals(nextAngle, angle) && VectorUtil.equals(pos, nextPos)) {
//            return false;
//        }
        if (!isDirty) {
            return false;
        }

        float t = (float)(time % HALO_PULSE_PERIOD);
        // -1 ... 1 in period:
        float sin = (float) Math.sin((float) (t * Math.PI) / 1000.f);
        haloScale = 0.9f + (sin + 1) * HALO_PULSE_RANGE;
        boolean isHaloDirty = true;

        //Log.i("jorgeper", "Halo: " + haloScale);

        float dt = (time - lastUpdateTime) / 1000.f;

        // First perform any rotations before starting the move into that direction
        float da = ANGLE_SPEED * dt;
        angle = VectorUtil.lerp(angle, nextAngle, da);

        boolean isAngleDirty = true;
        if (VectorUtil.equals(nextAngle, angle)) {
            angle = nextAngle;
            isAngleDirty = false;
        }
        //Log.i("jorgeper", "deltas: " + dt + ", " + da + ", angle: "  + angle + ", nextAngle: " + nextAngle);

        boolean isPosDirty = true;
        if (!isAngleDirty) {
            float dd = SPEED * dt;
            pos = VectorUtil.lerp(pos, nextPos, dd, new float[3]);

            //Log.i("jorgeper", "deltas: " + dt + ", " + dd + ", pos: " + VectorUtil.toString(pos) + ", nextPos: " + VectorUtil.toString(nextPos));

            if (VectorUtil.equals(nextPos, pos, 3)) {
                //Log.i("jorgeper", "done: pos: "  + VectorUtil.toString(pos) + ", nextPos: " + VectorUtil.toString(nextPos) );
                VectorUtil.set(pos, nextPos);
                isPosDirty = false;
            }
        }

        if (isAngleDirty || isPosDirty) {
            notifyChange();
        }
        lastUpdateTime = time;
        isDirty = isAngleDirty || isPosDirty || isHaloDirty;
        return true;
    }

    // rotation
    // 0 degrees is looking north (-z)
    // 90 degrees is clockwise from there (so looking east)
    public void draw(float[] mvpMatrix) {
        float[] translationM = new float[16];
        float[] scratchM = new float[16];
        float[] rotationM = new float[16];
        float[] scaleM = new float[16];
        float[] translationMInv = new float[16];

        // Icon transform
        Matrix.setIdentityM(translationM, 0);
        Matrix.translateM(translationM, 0, - HALF_WIDTH, 0, -HALF_WIDTH);
        Matrix.setRotateM(rotationM, 0, -angle, 0, 1.f, 0.f);
        Matrix.setIdentityM(translationMInv, 0);
        Matrix.translateM(translationMInv, 0,  pos[0], pos[1], pos[2]);

        Matrix.multiplyMM(scratchM, 0, rotationM, 0, translationM, 0);
        Matrix.multiplyMM(scratchM, 0, translationMInv, 0, scratchM, 0);
        Matrix.multiplyMM(scratchM, 0, mvpMatrix, 0, scratchM, 0);

        // Halo transform
        Matrix.setIdentityM(scaleM, 0);
        Matrix.scaleM(scaleM, 0, haloScale, haloScale, haloScale);

        float[] scratchHaloM = new float[16];
        Matrix.multiplyMM(scratchHaloM, 0, rotationM, 0, translationM, 0);
        Matrix.multiplyMM(scratchHaloM, 0, scaleM, 0, scratchHaloM, 0);
        Matrix.multiplyMM(scratchHaloM, 0, translationMInv, 0, scratchHaloM, 0);
        Matrix.multiplyMM(scratchHaloM, 0, mvpMatrix, 0, scratchHaloM, 0);

        haloQuad.draw(scratchHaloM);
        iconQuad.draw(scratchM);

        float[] dir = getDirection();
        float[] dirLine = new float[] { pos[0], pos[1], pos[2], dir[0] + pos[0], dir[1] + pos[1], dir[2] + pos[2]};

        line.draw(mvpMatrix, Color.valueOf(0xffff0000), dirLine);
    }

    private Line line;
    private LocationListener listener;

    public void setListener(LocationListener listener) {
        this.listener = listener;
    }

    public interface LocationListener {
        void onLocationChanged();
    }
}

