package robotrace;

import static robotrace.General.*;

/**
 * Implementation of a camera with a position and orientation.
 */
class Camera {

    /**
     * The position of the camera.
     */
    public Vector eye = new Vector(3f, 6f, 5f);

    /**
     * The point to which the camera is looking.
     */
    public Vector center = Vector.O;

    /**
     * The up vector.
     */
    public Vector up = Vector.Z;

    /**
     * Updates the camera viewpoint and direction based on the
     * selected camera mode.
     */
    public void update(GlobalState gs, Robot focus) {

        switch (gs.camMode) {

            // First person mode    
            case 1:
                setFirstPersonMode(gs, focus);
                break;

            // Default mode    
            default:
                setDefaultMode(gs);
        }
    }

    /**
     * Computes eye, center, and up, based on the camera's default mode.
     */
    private void setDefaultMode(GlobalState gs) {
        /*eye = new Vector(
                gs.vDist*Math.sin(gs.phi-Math.PI/2)*Math.cos(gs.theta/2-Math.PI/2),
                -gs.vDist*Math.sin(gs.phi-Math.PI/2)*Math.sin(gs.theta/2-Math.PI/2),
                gs.vDist*Math.cos(gs.phi-Math.PI/2)
        );*/

        eye = new Vector(
                Math.cos(getAzimuth(gs)) * Math.cos(getInclination(gs)) * gs.vDist,
                -Math.sin(getAzimuth(gs)) * Math.cos(getInclination(gs)) * gs.vDist,
                Math.sin(getInclination(gs)) * gs.vDist
        );
        //Get the center point from the global state.
        this.center = gs.cnt;
        //Reset the up vector.
        this.up = Vector.Z;
    }

    /**
     * Computes eye, center, and up, based on the first person mode.
     * The camera should view from the perspective of the robot.
     */
    private void setFirstPersonMode(GlobalState gs, Robot focus) {
        eye = focus.position.add(new Vector (0,0,2));
        center = focus.viewDirection.add(new Vector (0,0,2));
        up = Vector.Z;
    }
}
