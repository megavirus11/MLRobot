
package robotrace;

/**
 * Implementation of RaceTrack, creating a track from a parametric formula
 */
public class ParametricTrack extends RaceTrack {
    
    @Override
    protected Vector getPoint(double t) {

        return Vector.O;

    }

    @Override
    protected Vector getTangent(double t) {

        return Vector.O;

    }
    
}
