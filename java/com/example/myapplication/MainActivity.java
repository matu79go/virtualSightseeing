/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLink;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



/**
 * This shows how to create an activity with access to all the options in Panorama
 * which can be adjusted dynamically
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener,StepListener {

    // Mt.Fuji
    private static final LatLng FUJI = new LatLng(35.3657551, 138.7329359);
    // Gunkan Island
    private static final LatLng GUNKAN = new LatLng(32.6269494, 129.7385099);
    // Hawaii
    private static final LatLng HAWAII = new LatLng(21.2725085, -157.8238191);
    // venice
    private static final LatLng VENICE = new LatLng(45.4329418, 12.341122);
    // GrandCanyon
    private static final LatLng GRANDCAYON  = new LatLng(36.0650956, -112.1371072);
    // Mikano
    private static final LatLng MILANO = new LatLng(45.4642996, 9.189493);
    // Mont Saint Michel
    private static final LatLng MONT_SAINT_MICHEL = new LatLng(48.6340617, -1.510557);
    // Colosseo
    private static final LatLng COLOSSEO = new LatLng(41.8904757, 12.4919313);
    // Galapagos
    private static final LatLng GALAPAGOS = new LatLng(-1.2391839, -90.3857192);

    /**
     * The amount in degrees by which to scroll the camera
     */
    private static final int PAN_BY_DEG = 30;

    private static final float ZOOM_BY = 0.5f;

    private StreetViewPanorama mStreetViewPanorama;

    private SeekBar mCustomDurationBar;

    // motion sensor
    private TextView textView;
    private StepDetector simpleStepDetector;
    private LotateDetector simpleLotateDetector;

    private SensorManager sensorManager;
    private Sensor accel;
    private Sensor gyro;
    private Sensor magnetic;

    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private static final String TEXT_NUM_LOTATE = "Number of Lotate: ";

    private int numSteps;
    private int numLotate;
    private int numMagnet;

    private TextView TvSteps;

    // chrome cast
    private CastContext mCastContext;
    private androidx.mediarouter.app.MediaRouteButton mMediaRouteButton;

    // direction
    private static final int AZIMUTH_THRESH = 15;
    private static final int MATRIX_SIZE = 16;
    private float[] mgValues = new float[3];
    private float[] acValues = new float[3];

    private int nowScale = 0;
    private int oldScale = 0;
    private int nowAzimuth = 0;
    private int oldAzimuth = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view);

        // sensor
        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // accelator
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // gyro
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        simpleLotateDetector = new LotateDetector();
        simpleLotateDetector.registerListener(this);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        mStreetViewPanorama = panorama;
                        // Only set the panorama to SYDNEY on startup (when no panoramas have been
                        // loaded which is when the savedInstanceState is null).
                        if (savedInstanceState == null) {
                            mStreetViewPanorama.setPosition(HAWAII);
                        }
                    }
                });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!checkReady()) {
            return false;
        }

        switch (item.getItemId()) {
            case R.id.menuItem1:
                mStreetViewPanorama.setPosition(FUJI, 30);
                return true;

            case R.id.menuItem2:
                mStreetViewPanorama.setPosition(GUNKAN, 30);
                return true;

            case R.id.menuItem3:
                mStreetViewPanorama.setPosition(HAWAII, 30);
                return true;

            case R.id.menuItem4:
                mStreetViewPanorama.setPosition(VENICE, 30);
                return true;

            case R.id.menuItem5:
                mStreetViewPanorama.setPosition(GRANDCAYON, 30);
                return true;

            case R.id.menuItem6:
                mStreetViewPanorama.setPosition(MILANO, 30);
                return true;

            case R.id.menuItem7:
                mStreetViewPanorama.setPosition(MONT_SAINT_MICHEL, 30);
                return true;

            case R.id.menuItem8:
                mStreetViewPanorama.setPosition(COLOSSEO, 30);
                return true;

            case R.id.menuItem9:
                mStreetViewPanorama.setPosition(GALAPAGOS, 30);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float sensorX, sensorY, sensorZ;

        // direction
        float[] inR = new float[MATRIX_SIZE];
        float[] outR = new float[MATRIX_SIZE];
        float[] I = new float[MATRIX_SIZE];
        float[] orValues = new float[3];

        // acceleter
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);


        }
        // gyro
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            simpleLotateDetector.updateGyro(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);

        }

    }

    private int rag2Doc(float rad) {
        return (int)Math.floor(Math.abs(Math.toDegrees(rad)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(MainActivity.this, gyro, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this,accel);
        sensorManager.unregisterListener(this,gyro);
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        // move forward by each 5 step
        if ( numSteps % 5 == 0 ){
            movePosition();
        }
    }


    @Override
    public void lotate(long timeNs, String type) {

        // lotate on street view
        if ( type == LotateDetector.TYPE_RIGHT ){
            lotateRight();
        }
        else if ( type == LotateDetector.TYPE_LEFT ) {
            lotateLeft();
        }

    }



    /**
     * When the panorama is not ready the PanoramaView cannot be used. This should be called on
     * all entry points that call methods on the Panorama API.
     */
    private boolean checkReady() {
        if (mStreetViewPanorama == null) {
            Toast.makeText(this, R.string.panorama_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    private void lotateRight() {
        mStreetViewPanorama.animateTo(
                new StreetViewPanoramaCamera.Builder().zoom(
                        mStreetViewPanorama.getPanoramaCamera().zoom)
                        .tilt(mStreetViewPanorama.getPanoramaCamera().tilt)
                        .bearing(mStreetViewPanorama.getPanoramaCamera().bearing + PAN_BY_DEG)
                        .build(), getDuration());
    }
    private void lotateLeft() {
        mStreetViewPanorama.animateTo(
                new StreetViewPanoramaCamera.Builder().zoom(
                        mStreetViewPanorama.getPanoramaCamera().zoom)
                        .tilt(mStreetViewPanorama.getPanoramaCamera().tilt)
                        .bearing(mStreetViewPanorama.getPanoramaCamera().bearing - PAN_BY_DEG)
                        .build(), getDuration());
    }

    public void movePosition() {
        StreetViewPanoramaLocation location = mStreetViewPanorama.getLocation();
        StreetViewPanoramaCamera camera = mStreetViewPanorama.getPanoramaCamera();
        if (location != null && location.links != null) {
            StreetViewPanoramaLink link = findClosestLinkToBearing(location.links, camera.bearing);
            mStreetViewPanorama.setPosition(link.panoId);
        }
    }

    public static StreetViewPanoramaLink findClosestLinkToBearing(StreetViewPanoramaLink[] links,
            float bearing) {
        float minBearingDiff = 360;
        StreetViewPanoramaLink closestLink = links[0];
        for (StreetViewPanoramaLink link : links) {
            if (minBearingDiff > findNormalizedDifference(bearing, link.bearing)) {
                minBearingDiff = findNormalizedDifference(bearing, link.bearing);
                closestLink = link;
            }
        }
        return closestLink;
    }

    // Find the difference between angle a and b as a value between 0 and 180
    public static float findNormalizedDifference(float a, float b) {
        float diff = a - b;
        float normalizedDiff = diff - (float) (360 * Math.floor(diff / 360.0f));
        return (normalizedDiff < 180.0f) ? normalizedDiff : 360.0f - normalizedDiff;
    }

    private long getDuration() {
        return mCustomDurationBar.getProgress();
        //return;
    }
}
