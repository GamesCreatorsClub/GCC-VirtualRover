package org.ah.piwars.fishtank.game.fish;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class BoidHelper {
    // Credit: https://github.com/SebLague
    // https://github.com/SebLague/Boids/blob/master/Assets/Scripts/BoidHelper.cs

    public static final int NUM_VIEW_DIRECTIONS = 300;
    public static final Quaternion[] DIRECTIONS = new Quaternion[NUM_VIEW_DIRECTIONS];
    public static final Quaternion[][] ANGLES = new Quaternion[NUM_VIEW_DIRECTIONS][];
//    public static final float[][] ANGLES = new float[NUM_VIEW_DIRECTIONS][];
    public static final Vector3 FORWARD = new Vector3(0, 0, 1f);

    static {
        final float goldenRatio = (float)Math.sqrt(5.0) / 2.0f;
        final float angleIncrement = MathUtils.PI * 2f * goldenRatio;

        for (int i = 0; i < NUM_VIEW_DIRECTIONS; i++) {
            float t = (float) i / NUM_VIEW_DIRECTIONS;
            float inclination = (float)Math.acos(1.0 - 2.0 * t);
            float azimuth = angleIncrement * i;

            float x = (float)(Math.sin(inclination) * Math.cos(azimuth));
            float y = (float)(Math.sin(inclination) * Math.sin(azimuth));
            float z = (float)(Math.cos(inclination));
            Quaternion quaternion = new Quaternion().setFromCross (new Vector3(x, y, z).nor(), FORWARD);
            DIRECTIONS[i] = quaternion;

            float yaw = quaternion.getYawRad();
            float pitch = quaternion.getPitchRad();
            float roll = quaternion.getRollRad();

            System.out.println(String.format("%.3f %.3f %.3f", yaw, pitch, roll));

            Quaternion yawQ = new Quaternion().setFromAxisRad(0f,  1f, 0f, yaw);
            Quaternion pitchQ = new Quaternion().setFromAxisRad(1f,  0f, 0f, -roll);
            ANGLES[i] = new Quaternion[2];
            ANGLES[i][0] = yawQ;
            ANGLES[i][1] = pitchQ;
//            ANGLES[i] = new float[2];
//            ANGLES[i][0] = yaw;
//            ANGLES[i][1] = roll;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(DIRECTIONS[0]);
        System.out.println(DIRECTIONS[1]);
    }
}
