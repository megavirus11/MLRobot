
package robotrace;

/**
 * Implementation of RaceTrack, creating a track from a parametric formula
 */
public class ParametricTrack extends RaceTrack {
    
    @Override
    protected Vector getPoint(double t) {
        //P (t) = (10 cos(2πt), 14 sin(2πt), 1)
        Vector v = new Vector(10*Math.cos(2*Math.PI*t), 14*Math.sin(2*Math.PI*t), 1);
    return v;

    }

    @Override
    protected Vector getTangent(double t) {
        Vector v = new Vector(-2*Math.PI*10*Math.sin(2*Math.PI*t), 2*Math.PI*14*Math.cos(2*Math.PI*t), 0);
        return v;
    }
    
}
