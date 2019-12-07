package belringer.maps;

import android.graphics.Color;

class Constants {
    static final Color ROAD_TEXT_COLOR = Color.valueOf(0xff616161);
    static final Color BUILDING_TEXT_COLOR = Color.valueOf(0xffa1a1a1);
    static final Color PARK_TEXT_COLOR = Color.valueOf(0xff4d811f);
    static final Color GROUND_COLOR = Color.valueOf(0xfffefdf7);
    static final Color FOG_COLOR = Color.valueOf(0xff82d5d9);
    static final Color ROAD_COLOR = Color.valueOf(0xffdad9d3);
    static final Color BUILDING_COLOR = Color.valueOf(0xffefece7);
    static final Color PARK_COLOR = Color.valueOf(0xffc6ebb5);
    static final float CITY_BLOCK_WIDTH = 2;
    static final float ROAD_WIDTH =0.25f;
    static final float SIDEWALK_WIDTH =0.2f;
    static final float ROAD_LABEL_HEIGHT=0.6f;
    static final float BUILDING_LABEL_HEIGHT=0.4f;
    static final float GROUND_WIDTH = 1000.f;
    static final float CHASE_CAMERA_DISTANCE_FROM_LOCATION = 6.0f;
    static final float CHASE_CAMERA_HEIGHT = 6.0f;
    static final float AERIAL_CAMERA_HEIGHT = 30.f;

    // Location halo animation constants.
    static final int HALO_PULSE_PERIOD = 2000; // 2 seconds per full pulse
    static final float HALO_PULSE_RANGE = 0.10f; // contract and expand by 0.15% per period.
}
