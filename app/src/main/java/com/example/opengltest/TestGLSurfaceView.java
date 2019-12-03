package com.example.opengltest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

//import static android.opengl.GLES20.GL_MULTISAMPLE;

//import static javax.microedition.khronos.opengles.GL20.GL_LINE_SMOOTH_HINT;

public class TestGLSurfaceView extends GLSurfaceView {

    private TestGLRenderer renderer;

    public TestGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new TestGLRenderer(context, this::requestRender);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        //glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

//    public void move(float dx, float dy, float dz) {
//        renderer.move(dx, dy, dz);
//        //requestRender();
//    }

    private float x;
    private float z;
    private float angle;

    public void setAerial(boolean aerial) {
        renderer.setAerial(aerial);
    }

    public void moveBy(float dx, float dz, float angle) {
        x = x + dx;
        z = z + dz;
        this.angle = angle;
        renderer.moveTo(x, z, this.angle);
    }

    public void moveTo(float x, float z, float angle) {
        renderer.moveTo(x, z, angle);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        // MotionEvent reports input details from the touch screen
//        // and other input controls. In this case, you are only
//        // interested in events where the touch position changed.
//
//        float x = e.getX();
//        float y = e.getY();
//
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//
//                float dy = y - previousY;
//                float dx = x - previousX;
//
//                move(dx/100, 0, dy/100);
//        }
//
//        previousX = x;
//        previousY = y;
//        return true;
//    }
}