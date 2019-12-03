package com.example.opengltest;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

// https://developer.android.com/training/graphics/opengl
// https://developer.android.com/guide/topics/graphics/opengl
// https://medium.com/@xzan/opengl-a-noobs-guide-for-android-developers-5eed724e07ad
// https://blog.jayway.com/2009/12/04/opengl-es-tutorial-for-android-part-ii-building-a-polygon/ <-- GL 10
public class TestGLRenderer implements GLSurfaceView.Renderer {

    private Camera camera;
    private Grid grid;
    private Axes axes;
    private Triangle triangle;
    private Plane ground;
    private Roads roads;
    private Location location;
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] vPMatrix = new float[16];
    private final Context context;
    private long time;
    private Listener listener;
    private boolean aerial = false;

    TestGLRenderer(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setAerial(boolean aerial) {
        this.aerial = aerial;
        if (aerial) {
            camera.lookAtAerial(location.getPos(), location.getDirection(), true);
        } else {
            camera.lookAt(location.getPos(), location.getDirection(), true);
        }
    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        time = SystemClock.uptimeMillis();

        // Set the background frame color
        GLES20.glClearColor(0.507f, 0.83f, 0.84f, 1.0f);
        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        camera = new Camera(time, 0.f, 4.5f, 10.f);
        triangle = new Triangle();
        axes = new Axes();
        grid = new Grid(1000, 1);
        ground = new Plane(context, 2, 2, 1000, 1000);
        roads = new Roads(context, roadColor, fogColor);
        location = new Location(context);

        location.moveTo(0, 0, false); // 0 degree = looking north (-z)
        location.setAngle(0, false);

        if (aerial) {
            camera.lookAtAerial(location.getPos(), location.getDirection(), false);
        } else {
            camera.lookAt(location.getPos(), location.getDirection(), false);
        }

        location.setListener(() -> {
            // TODO: if animating camera, don't chase location
            if (aerial) {
                camera.lookAtAerial(location.getPos(),location.getDirection(), false);
            } else {
                camera.lookAt(location.getPos(), location.getDirection(), false);
            }
            listener.onDirty();
        });

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        listener.onDirty();
    }


    interface Listener {
        void onDirty();
    }

    private static final Color groundColor = Color.valueOf(0xfffefdf7);
    private static final Color fogColor = Color.valueOf(0xff82d5d9);
    private static final Color roadColor = Color.valueOf(0xffdad9d3);

    private boolean isDirty = true;

    public boolean tick() {
        time = SystemClock.elapsedRealtime();
        boolean returnValue = isDirty;
        returnValue |= location.tick(time);
        returnValue |= camera.tick(time);

        // TODO: this should be calculated from all children, but for now this is good enough.
        isDirty = false;
        return returnValue;
    }

    public void onDrawFrame(GL10 unused) {
        //Log.i("jorgeper", "onDrawFrame");

        tick();
//        if (!tick()) {
//            Log.i("jorgeper", "onDrawFrame: !dirty");
//            return;
//        }

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        camera.set(viewMatrix);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // https://developer.android.com/training/graphics/opengl/motion
        float[] translationM = new float[16];
        Matrix.setIdentityM(translationM, 0);
        Matrix.translateM(translationM, 0, -500, 0, -500);
        float[] scratchM = new float[16];
        Matrix.multiplyMM(scratchM, 0, vPMatrix, 0, translationM, 0);

        Color fogColor = this.fogColor;
        if (aerial) {
            fogColor = Color.valueOf(0x00000000);
        }
        ground.draw(scratchM, groundColor, fogColor);
        roads.draw(vPMatrix, fogColor);
        location.draw(vPMatrix);

        grid.draw(vPMatrix);

        //axes.draw(vPMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 2, 100);
    }

//    public void move(float dx, float dy, float dz) {
//        camera.moveBy(new float[]{dx, dy, dz});
//    }

    public void moveBy(float dx, float dz, float angle) {
        location.moveBy(dx, dz, true);
        location.setAngle(angle, true);
    }

    public void moveTo(float x, float z, float angle) {
        location.moveTo2(x, z, true);
        //location.setAngle(angle, true);
    }
}