package belringer.maps.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.opengltest.R;

import belringer.maps.CameraType;
import belringer.maps.MapRenderer;

import belringer.maps.MapView;

public class MainActivity extends AppCompatActivity {
    private MapView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        glView = findViewById(R.id.surface);

        findViewById(R.id.left).setOnClickListener(view -> glView.moveBy(-1.f, 0.f));
        findViewById(R.id.right).setOnClickListener(view -> glView.moveBy(1.f, 0.f));
        findViewById(R.id.up).setOnClickListener(view -> glView.moveBy(0.f, -1.f));
        findViewById(R.id.down).setOnClickListener(view -> glView.moveBy(0.f, 1.f));
        findViewById(R.id.view_type).setOnClickListener(view -> switchViewType());
    }

    private void switchViewType() {
        glView.setCameraType(glView.getCameraType()== CameraType.AERIAL ? CameraType.CHASE : CameraType.AERIAL);
    }
}
