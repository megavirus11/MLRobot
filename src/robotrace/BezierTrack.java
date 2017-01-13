
package robotrace;

/**
 * Implementation of RaceTrack, creating a track from control points for a 
 * cubic Bezier curve
 */
public class BezierTrack extends RaceTrack {
    
    private Vector[] controlPoints;

    
    BezierTrack(Vector[] controlPoints) {
        this.controlPoints = controlPoints;
        


    }
    
    @Override
    protected Vector getPoint(double t) {

        return Vector.O;

    }

    @Override
    protected Vector getTangent(double t) {

        return Vector.O;

    }
    

}
