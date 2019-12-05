package belringer.maps;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static belringer.maps.Constants.GROUND_WIDTH;

public class MapRenderer implements GLSurfaceView.Renderer {
    private final float[] pMatrix = new float[16];
    private final float[] vMatrix = new float[16];
    private final float[] vpMatrix = new float[16];
    private final Context context;

    private Camera camera;
    private Grid grid;
    private Axes axes;
    private Plane ground;
    private Roads roads;
    private Location location;
    private long time;
    private MapRendererListener listener;
    private CameraType cameraType = CameraType.CHASE;
    private boolean isDirty = true;

    MapRenderer(Context context, MapRendererListener listener) {
        this.context = context;
        this.listener = listener;
    }

    CameraType getCameraType() {
        return cameraType;
    }

    void setCameraType(CameraType cameraType) {
        this.cameraType = cameraType;
        camera.lookAt(location.getPosition(), location.getDirection(), cameraType, true);
    }

    static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        time = SystemClock.uptimeMillis();

        // Debugging aids.
        axes = new Axes(context);
        grid = new Grid(context, 1000, 1);

        camera = new Camera(time, 0.f, 0.f, 0.f);

        // The ground is a big 1000x1000 quad.
        ground = new Plane(context, 2, 2, GROUND_WIDTH, GROUND_WIDTH);

        roads = new Roads(context, Constants.ROAD_COLOR, Constants.FOG_COLOR);
        location = new Location(context);

        // Start at the corner of the city, looking north.
        location.moveTo(0, 0, false);
        location.setAngle(0, false);

        camera.lookAt(location.getPosition(), location.getDirection(), cameraType, false);

        location.setListener(() -> {
            // TODO: if animating camera, don't chase location
            camera.lookAt(location.getPosition(), location.getDirection(), cameraType, false);
            listener.onDirty();
        });

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // for blending location sprite

        listener.onDirty();
    }

    boolean tick() {
        time = SystemClock.elapsedRealtime();
        boolean returnValue = isDirty;
        returnValue |= location.tick(time);
        returnValue |= camera.tick(time);

        // TODO: this should be calculated from all children, but for now this is good enough.
        isDirty = false;
        return returnValue;
    }

    public void onDrawFrame(GL10 unused) {
        // TODO: actually do make use of the dirty flag.
        // We currently render the entire scene in every frame in the render loop regardless of
        // whether things changed or not.
        tick();

        // Calculate the view projection matrix.
        camera.set(vMatrix);
        Matrix.multiplyMM(vpMatrix, 0, pMatrix, 0, vMatrix, 0);

        // Translate the ground so that it is centered around the origin.
        float[] translationM = new float[16];
        Matrix.setIdentityM(translationM, 0);
        Matrix.translateM(translationM, 0, -GROUND_WIDTH / 2, 0, -GROUND_WIDTH / 2);
        float[] groundM = new float[16];
        Matrix.multiplyMM(groundM, 0, vpMatrix, 0, translationM, 0);

        // For aerial camera, disable fog effect, since that's based on distance to the camera, not
        // strictly based on the horizon.
        // TODO: use a nice fading animation for the fog density.
        Color fogColor = Constants.FOG_COLOR;
        if (cameraType == CameraType.AERIAL) {
            fogColor = Color.valueOf(Color.TRANSPARENT);;
        }

        // Clear up the frame, using the sky color. We'll start laying out the rest of the layers
        // on top.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        ground.draw(groundM, Constants.GROUND_COLOR, fogColor);
        roads.draw(vpMatrix, fogColor);
        location.draw(vpMatrix);

        // Debugging aids.
        grid.draw(vpMatrix);
        axes.draw(vpMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(pMatrix, 0, -ratio, ratio, -1, 1, 2, 100);
    }

    void moveTo(float x, float z) {
        location.moveTo(x, z, true);
    }
}