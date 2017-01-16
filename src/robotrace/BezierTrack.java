
package robotrace;

/**
 * Implementation of RaceTrack, creating a track from control points for a 
 * cubic Bezier curve
 */
public class BezierTrack extends RaceTrack {
    
    private Vector[] controlPoints;
    private int n; //number of controlpoints -1
    
    BezierTrack(Vector[] controlPoints) {
        this.controlPoints = controlPoints;
        n = controlPoints.length;
    }
    
    @Override
    protected Vector getPoint(double t) {
        int k = 3*(int)Math.floor((t*(double)(n))/3); //first point of curve
        double tLocal = (t%(double)(3.0/(double)n))/(3.0/(double)n); //local t on Bezier curve
        return CalculateBezierPoint(
                tLocal, 
                controlPoints[(k)%n], 
                controlPoints[(k+1)%n], 
                controlPoints[(k+2)%n], 
                controlPoints[(k+3)%n]);
    }

    @Override
    protected Vector getTangent(double t) {
        int k = 3*(int)Math.floor((t*(double)(n))/3); //first point of curve
        double tLocal = (t%(double)(3.0/(double)n))/(3.0/(double)n); //local t on Bezier curve
        return CalculateBezierTangent(
                tLocal, 
                controlPoints[(k)%n], 
                controlPoints[(k+1)%n], 
                controlPoints[(k+2)%n], 
                controlPoints[(k+3)%n]).normalized();

    }
    
    Vector CalculateBezierPoint(double t, Vector p0, Vector p1, Vector p2, Vector p3) {
        double u = 1-t;
        double tt = t*t;
        double uu = u*u;
        double uuu = uu * u;
        double ttt = tt * t;

        Vector p = p0.scale(uuu); //first term
        p = p.add(p1.scale(3 * uu * t)); //second term
        p = p.add(p2.scale(3 * u * tt)); //third term
        p = p.add(p3.scale(ttt)); //fourth term

        return p;
    }
    
    Vector CalculateBezierTangent(double t, Vector p0, Vector p1, Vector p2, Vector p3) {
        double u = 1-t;
        double tt = t*t;
        double uu = u*u;
        double uuu = uu * u;
        double ttt = tt * t;

        Vector p = p0.scale(-3*uu); //first term
        p = p.add(p1.scale((3 * uu) - (6*u* t))); //second term
        p = p.add(p2.scale((6 * u * t)-3*tt)); //third term
        p = p.add(p3.scale(3*tt)); //fourth term
        
        
//        dC(t)/dt = T(t) =
//        -3*P0*(1 - t)^2 + 
//        P1*(3*(1 - t)^2 - 6*(1 - t)*t) + 
//        P2*(6*(1 - t)*t - 3*t^2) +
//        3*P3*t^2
        return p;
    }

}
