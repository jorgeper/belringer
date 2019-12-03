package com.example.opengltest;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

// https://www.learnopengles.com/android-lesson-four-introducing-basic-texturing/
public class TexturedQuad {
    private FloatBuffer vertexBuffer;
    private final int program;
    private final FloatBuffer texBuffer;
    private final int texDataHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
//    static float triangleCoords[] = {
//            -1.0f, 1.0f, 1.0f,
//            -1.0f, -1.0f, 1.0f,
//            1.0f, 1.0f, 1.0f,
//            -1.0f, -1.0f, 1.0f,
//            1.0f, -1.0f, 1.0f,
//            1.0f, 1.0f, 1.0f,
//    };

    static float triangleCoords[] = {
            0.f, 0.f, 0.0f,
            0.f, 0.f, 1.0f,
            1.0f, 0.f, 0.0f,
            0.f, 0.f, 1.0f,
            1.0f, 0.f, 1.0f,
            1.0f, 0.f, 0.0f,
    };

    static float texCoords[] = {
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
    };

    public TexturedQuad(Context context, int drawableId) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // moveTo the buffer to read the first coordinate
        vertexBuffer.position(0);

        texBuffer = ByteBuffer.allocateDirect(texCoords.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        texBuffer.put(texCoords).position(0);

        texDataHandle = Util.loadTexture(context, drawableId);

        String vertexShaderCode = Util.readTextFileFromRawResource(context, R.raw.textured_vertex_shader);
        int vertextShader = TestGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        String fragmentShaderCode = Util.readTextFileFromRawResource(context, R.raw.textured_fragment_shader);
        int fragmentShader = TestGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = GLES20.glCreateProgram();

        GLES20.glAttachShader(program, vertextShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(program);

        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 4*3, vertexBuffer);

        // Pass in the texture coordinate information
        int texCoordinateHandle = GLES20.glGetAttribLocation(program, "aTexCoordinate");
        GLES20.glEnableVertexAttribArray(texCoordinateHandle);
        GLES20.glVertexAttribPointer(texCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, texBuffer);

        int texHandle = GLES20.glGetUniformLocation(program, "uTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texDataHandle);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(texHandle, 0);

        int vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, triangleCoords.length / 3);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordinateHandle);
    }
}