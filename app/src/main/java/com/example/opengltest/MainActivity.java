package com.example.opengltest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// best: https://www.learnopengles.com/android-lesson-five-an-introduction-to-blending/
// text: https://stackoverflow.com/questions/1339136/draw-text-in-opengl-es
public class MainActivity extends AppCompatActivity {
    private TestGLSurfaceView glView;
    private TestGLRenderer renderer;

    private boolean aerial = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        glView = findViewById(R.id.surface);

//        findViewById(R.id.left).setOnClickListener(view -> glView.moveBy(-1.f, 0.f, -90));
//        findViewById(R.id.right).setOnClickListener(view -> glView.moveBy(1.f, 0.f, 90));
//        findViewById(R.id.up).setOnClickListener(view -> glView.moveBy(0.f, -1.f, 0));
//        findViewById(R.id.down).setOnClickListener(view -> glView.moveBy(0.f, 1.f, -180));
        findViewById(R.id.left).setOnClickListener(view -> glView.moveBy(-1.f, 0.f, 270));
        findViewById(R.id.right).setOnClickListener(view -> glView.moveBy(1.f, 0.f, 90));
        findViewById(R.id.up).setOnClickListener(view -> glView.moveBy(0.f, -1.f, 0));
        findViewById(R.id.down).setOnClickListener(view -> glView.moveBy(0.f, 1.f, 180));
        findViewById(R.id.view_type).setOnClickListener(view -> switchViewType());
    }

    private void switchViewType() {
        aerial = !aerial;
        glView.setAerial(aerial);
    }
}
