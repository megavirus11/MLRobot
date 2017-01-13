package robotrace;

public class General {
    public static float getAzimuth(GlobalState globalState) {
        return globalState.theta;
    }

    public static float getInclination(GlobalState globalState) {
        return globalState.phi;
    }
}
