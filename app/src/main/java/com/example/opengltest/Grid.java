package com.example.opengltest;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Grid {
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

//    private final String fragmentShaderCode =
//            "precision mediump float;" +
//                    "uniform vec4 vColor;" +
//                    "void main() {" +
//                    "  gl_FragColor = vColor;" +
//                    "}";

    //http://www.ozone3d.net/tutorials/glsl_fog/p03.php
    private final String fragmentShaderCode =
    "precision mediump float;" +
    "uniform vec4 vColor;" +
    "void main() {" +
            "const float density = 0.05;" +
            "const float LOG2 = 1.442695;" +
//            "const vec4 fogColor = vec4(0,0,0,1);" +
            "const vec4 fogColor = vec4(0.507, 0.83, 0.84, 1);" +
            "float z = gl_FragCoord.z / gl_FragCoord.w; " +
            "float fogFactor = exp2( -density *" +
                "density * " +
                "z * " +
                "z * " +
                "LOG2 );" +
            "fogFactor = clamp(fogFactor, 0.0, 1.0); " +
            "gl_FragColor = mix(fogColor, vColor, fogFactor);" +
//    "  gl_FragColor = vColor;" +
//            "}" +
    "}";

//    private final String fragmentShaderCode = "#version 330\n" +
//
//    "uniform vec4 CameraEye;" +
//    "uniform vec4 FogColor;" +
//
//    "in VertexData{" +
//    "    vec4 mColor;" +
//    "    vec4 mVertex;" +
//    "} VertexIn;" +
//
//    "float getFogFactor(float d)" +
//    "{" +
//    "const float FogMax = 20.0;" +
//    "const float FogMin = 10.0;" +
//
//        " if (d>=FogMax) return 1;" +
//        " if (d<=FogMin) return 0;" +
//
//        "return 1 - (FogMax - d) / (FogMax - FogMin); " +
//    " } " +
//
//    "void main(void)" +
//    "{" +
//        "vec4 V = VertexIn.mVertex;" +
//        "float d = distance(CameraEye, V);" +
//        "float alpha = getFogFactor(d);" +
//        "gl_FragColor = mix(VertexIn.mColor, FogColor, alpha);" +
//    "}";


    private FloatBuffer vertexBuffer;
    private final int program;

    float color[] = { 0.5f, 0.5f, 0.5f, 0.5f };
    private float[] coords;

    private float[] makeCoords(int count, int step) {
        // count lines times two sides. plus center line, times 6 coordinates per line, times 2 dimensions.
        float[] coords = new float[((count * 2) + 1) * 2 * 6];
        float halfWidth = count * step;
        int j = 0;
        for (int i = -count; i <= count; ++i) {
            coords[j++] = i * step;
            coords[j++] = 0.f;
            coords[j++] = -halfWidth;

            coords[j++] = i * step;
            coords[j++] = 0.f;
            coords[j++] = halfWidth;
        }

        for (int i = -count; i <= count; ++i) {
            coords[j++] = -halfWidth;
            coords[j++] = 0.f;
            coords[j++] = i * step;

            coords[j++] = halfWidth;
            coords[j++] = 0.f;
            coords[j++] = i * step;
        }

        return coords;
    }

    public Grid(int count, int step) {
        coords = makeCoords(count, step);

        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);

        int vertextShader = TestGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = TestGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = GLES20.glCreateProgram();

        GLES20.glAttachShader(program, vertextShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(program);
        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 4*3, vertexBuffer);

        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        int vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, coords.length / 3);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}