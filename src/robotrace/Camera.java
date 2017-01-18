package robotrace;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import static robotrace.General.*;

/**
 * Implementation of a camera with a position and orientation.
 */
class Camera {
    /**
     * The far plane must have a minimum distance, or else nothing will be
     * visible when zoomed all the way in. FIXES THE ZOOMING IN EVERYTHING WHITE + SHADER PROBLEM
     */
    private static final float MIN_FAR_PLANE_DIST = 500f;

    /**
     * The position of the camera.
     */
    public Vector eye;

    /**
     * The point to which the camera is looking.
     */
    public Vector center;

    /**
     * The up vector.
     */
    public Vector up;

    private float fovAngle;
    private float planeNear;
    private float planeFar;

    /**
     * Updates the camera viewpoint and direction based on the
     * selected camera mode.
     */
    public void update(GL2 gl, GLU glu, GlobalState gs, Robot focus) {
        switch (gs.camMode) {

            // First person mode    
            case 1:
                setFirstPersonMode(gs, focus);
                break;

            // Default mode    
            default:
                setDefaultMode(gs);
        }
        setView(gl, glu, gs);
    }

    public void setCamera(GlobalState gs, float fovAngle) {
        final float dist = (float) center.subtract(eye).length();
        if (fovAngle < 1d) {
            this.fovAngle = (float) Math.toDegrees(Math.atan(dist / (2d * 10f)));
        } else {
            this.fovAngle = fovAngle;
        }
        final float planeNearNew = 0.1f * dist;
        final float planeFarNew = Math.max(MIN_FAR_PLANE_DIST, 10f * dist);
        if (planeNearNew <= 0f) {
            this.planeNear = 1f;
        } else {
            this.planeNear = planeNearNew;
        }
        if (planeFarNew <= 0f) {
            this.planeFar = 1f;
        } else {
            this.planeFar = planeFarNew;
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

        this.eye = new Vector(
                Math.cos(getAzimuth(gs)) * Math.cos(getInclination(gs)) * gs.vDist,
                Math.sin(getAzimuth(gs)) * Math.cos(getInclination(gs)) * gs.vDist,
                Math.sin(getInclination(gs)) * gs.vDist
        ).add(gs.cnt);
        //Get the center point from the global state.
        this.center = gs.cnt;
        //Reset the up vector.
        this.up = Vector.Z;

        setCamera(gs, fovAngle);
    }

    /**
     * Computes eye, center, and up, based on the first person mode.
     * The camera should view from the perspective of the robot.
     */
    private void setFirstPersonMode(GlobalState gs, Robot focus) {
        eye = focus.position.add(new Vector(0, 0, 2));
        center = focus.viewDirection.add(new Vector(0, 0, 2));
        up = Vector.Z;
        final float fovAngle = 40f;
        setCamera(gs, fovAngle);
    }

    private void setView(GL2 gl, GLU glu, GlobalState gs) {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);

        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        glu.gluPerspective(fovAngle, (float) gs.w / gs.h, planeNear, planeFar);

        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        glu.gluLookAt(eye.x(), eye.y(), eye.z(),
                center.x(), center.y(), center.z(),
                up.x(), up.y(), up.z());

        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
    }
}
