package com.example.opengltest;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;

import androidx.annotation.ColorInt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

//http://www.ozone3d.net/tutorials/glsl_fog/p03.php
public class Plane{
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private final int program;

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.95f, 0.95f, 0.85f, 1.0f };
    float fogColor[] = { 0.95f, 0.95f, 0.85f, 1.0f };
    float coords[];
    short indices[];

    public static Plane quad(Context context, float width, float height) {
        return new Plane(context, 2, 2, width, height);
    }

    public Plane(Context context, int width, int height, float stepW, float stepH) {
        makePlane(width, height, stepW, stepH);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                coords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(coords);
        // moveTo the buffer to read the first coordinate
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);

        String vertexShaderCode = Util.readTextFileFromRawResource(context, R.raw.plane_vertex_shader);
        int vertextShader = TestGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        String fragmentShaderCode = Util.readTextFileFromRawResource(context, R.raw.plane_fragment_shader);
        int fragmentShader = TestGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = GLES20.glCreateProgram();

        GLES20.glAttachShader(program, vertextShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    // https://android.googlesource.com/platform/development/+/master/samples/OpenGL/HelloOpenGLES20/src/com/example/android/opengl/Square.java
    // https://www.learnopengles.com/tag/triangle-strips/
    private void makePlane(int width, int height, float stepW, float stepH) {
        coords = new float[width * height * 3]; // 3 coords per vertex
        indices = new short[(height-1) * width  * 2];

        short k = 0;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                coords[k++] = j * stepW;
                coords[k++] = 0.f;
                coords[k++] = i * stepH;
            }
        }

        k = 0;
        for (short i = 0; i < height -1 ; ++i) {
            for (short j = 0; j < width ; ++j) {
                short l = (short)(i * width + j);

                indices[k++] = l;
                indices[k++] = (short)(l + width);
            }
        }
    }

    public void draw(float[] mvpMatrix, Color color, Color fogColor) {
        GLES20.glUseProgram(program);
        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 4*3, vertexBuffer);

        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color.getComponents(), 0);

        int fogColorHandle = GLES20.glGetUniformLocation(program, "vFogColor");
        GLES20.glUniform4fv(fogColorHandle, 1, fogColor.getComponents(), 0);

        int vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}