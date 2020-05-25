package com.example.myapplication;
import android.util.Log;

public class LotateDetector {

    private static final int ACCEL_RING_SIZE = 50;
    private static final int VEL_RING_SIZE = 10;

    // change this threshold according to your sensitivity preferences
    private static final float STEP_THRESHOLD = 80f;

    // 0.25秒
    private static final int STEP_DELAY_NS = 1000000000;

    private int accelRingCounter = 0;
    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];
    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];
    private long lastStepTimeNs = 0;
    private float oldVelocityEstimate = 0;

    private StepListener listener;

    public void registerListener(StepListener listener) {
        this.listener = listener;
    }
    // 右 or 左
    public static final String TYPE_RIGHT = "right";
    public static final String TYPE_LEFT = "left";
    // 1秒
    private static final int LOTATE_DELAY_NS = 1000000000;
    // fire lotate power
    private static final float LOTATE_POWER = 2.0f;
    private static final float LOTATE_POWER_OTHER = 3.0f;

    private String type = TYPE_LEFT;


    public void updateGyro(long timeNs, float x, float y, float z) {

        //

        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        // First step is to update our guess of where the global z vector is.
        accelRingCounter++;
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

        float[] worldZ = new float[3];
        worldZ[0] = SensorFilter.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = SensorFilter.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = SensorFilter.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

        float normalization_factor = SensorFilter.norm(worldZ);

        worldZ[0] = worldZ[0] / normalization_factor;
        worldZ[1] = worldZ[1] / normalization_factor;
        worldZ[2] = worldZ[2] / normalization_factor;

        float currentZ = SensorFilter.dot(worldZ, currentAccel) - normalization_factor;
        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

        float velocityEstimate = SensorFilter.sum(velRing);

//        Log.d("lotate_test : ", "" + velocityEstimate);
//        Log.d("lotate_y : ", "" + y);

        float absVelocityEstimate = Math.abs(velocityEstimate);

//        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
//                && (timeNs - lastStepTimeNs > STEP_DELAY_NS)) {
        if (absVelocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
                && (timeNs - lastStepTimeNs > STEP_DELAY_NS)) {
            //listener.lotate(timeNs);
            // check if lotate is right or left
            type = (y > 0) ? TYPE_LEFT : TYPE_RIGHT ;
            listener.lotate(timeNs, type);
            lastStepTimeNs = timeNs;
        }
        oldVelocityEstimate = velocityEstimate;
    }


    public void updateLotate(long timeNs, float x, float y, float z) {

//        Log.d("lotate_test : ", "aaa");

//        String type = "right";

        // 1秒間
        //if ( (y > 1.5) && timeNs - lastStepTimeNs > LOTATE_DELAY_NS ) {
        if ( Math.abs(y) > LOTATE_POWER && ( Math.abs(x) < LOTATE_POWER_OTHER && Math.abs(z) < LOTATE_POWER_OTHER ) ) {
            type = (y > 0) ? TYPE_LEFT : TYPE_RIGHT ;
            if ( timeNs - lastStepTimeNs > LOTATE_DELAY_NS ) {
                listener.lotate(timeNs, type);
                lastStepTimeNs = timeNs;
            }
        }


    }

}
