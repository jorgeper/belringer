package belringer.maps;

import android.graphics.Color;

class Constants {
    static final Color GROUND_COLOR = Color.valueOf(0xfffefdf7);
    static final Color FOG_COLOR = Color.valueOf(0xff82d5d9);
    static final Color ROAD_COLOR = Color.valueOf(0xffdad9d3);
    static final float CITY_BLOCK_WIDTH = 8;
    static final float ROAD_WIDTH =0.3f;
    static final float GROUND_WIDTH = 1000.f;
    static final int AVENUE_COUNT = 25;
    static final int STREET_COUNT = 25;
    static final float CHASE_CAMERA_DISTANCE_FROM_LOCATION = 6.0f;
    static final float CHASE_CAMERA_HEIGHT = 4.0f;
    static final float AERIAL_CAMERA_HEIGHT = 15.f;

    // Location halo animation constants.
    static final int HALO_PULSE_PERIOD = 2000; // 2 seconds per full pulse
    static final float HALO_PULSE_RANGE = 0.10f; // contract and expand by 0.15% per period.
}
