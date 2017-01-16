package robotrace;

import static java.lang.Math.sqrt;

public class General {
    public static float getAzimuth(GlobalState globalState) {
        return globalState.theta;
    }

    public static float getInclination(GlobalState globalState) {
        return globalState.phi;
    }

    public static float[] Cross(float[] vector1, float[] vector2) {
        float[] newVector = new float[3];
        newVector[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];
        newVector[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
        newVector[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
        return newVector;
    }

    public static void Normalize(float[] v) {

        double d, x, y, z;

        x = v[0];
        y = v[1];
        z = v[2];

        d = sqrt((x * x) + (y * y) + (z * z));

        v[0] = (float) (x / d);
        v[1] = (float) (y / d);
        v[2] = (float) (z / d);
    }
}
