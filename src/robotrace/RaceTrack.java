package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.GL.GL_LINE_STRIP;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import static javax.media.opengl.GL2.*;
import static robotrace.General.*;
import static robotrace.Textures.*;

/**
 * Implementation of a race track that is made from Bezier segments.
 */
abstract class RaceTrack {
    
    /** The width of one lane. The total width of the track is 4 * laneWidth. */
    protected final static float laneWidth = 1.22f; 
    
    /** The number of segments of the track. */
    protected final static int segments = 120;
    
    /** Array of vectors defining the center of the track. */
    protected Vector[] trackLine = new Vector[segments+1];
    
    /**
     * Constructor for the default track.
     */
    public RaceTrack() {

    }


    
    /**
     * Draws this track, based on the control points.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, Lighting lighting) {
        double t = 0; //point on track in [0,1]
        /** Draw trackline. 
        gl.glBegin(GL_LINE_STRIP);
        for (int i = 0; i <= segments; i++) {
            t=((double) i)/segments;
            gl.glVertex3d(getPoint(t).x, getPoint(t).y, 2);
        }
        gl.glEnd();
        */
        
        Vector v = new Vector(0,0,0); //dirty vector
        Vector n = new Vector(0,0,0); //dirty normal vector
        
        /** Draw road. */
        lighting.setMaterial(gl, Material.NONE);
        lighting.setColor(gl, 1f, 1f, 1f, 1f);
        Textures.track.bind(gl);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        n = Vector.Z; // <------------------------NORMAL
        gl.glNormal3d(n.x, n.y,n.z); // <------------------------NORMAL
        gl.glBegin(GL_QUAD_STRIP);
        for (int i = 0; i <= segments; i++) {
            t=((double) i)/segments;
            //outer
            v = getPoint(t).add(getUnitNormalPointingOut(t).scale(laneWidth*2));
            gl.glTexCoord2d(1,(segments/5)*t);
            gl.glVertex3d(v.x, v.y, v.z);
            //inner
            v = getPoint(t).add(getUnitNormalPointingOut(t).scale(laneWidth*-2));
            gl.glTexCoord2d(0,(segments/5)*t);
            gl.glVertex3d(v.x, v.y, v.z);
        }
        gl.glEnd();
        
        /** Draw inner side of track. */
        //lighting.setMaterial(gl, Material.BOULDER);
        Textures.brick.bind(gl);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        gl.glBegin(GL_QUAD_STRIP);
        for (int i = 0; i <= segments; i++) {
            t=((double) i)/segments;
            n = getUnitNormalPointingOut(t).scale(-1); // <------------------------NORMAL
            gl.glNormal3d(n.x, n.y,n.z); // <------------------------NORMAL
            //top
            v = getPoint(t).add(getUnitNormalPointingOut(t).scale(laneWidth*-2));
            gl.glTexCoord2d((segments/5)*t,1);
            gl.glVertex3d(v.x, v.y, v.z);
            //bottom
            v = getPoint(t).add(Vector.Z.scale(-2)).add(getUnitNormalPointingOut(t).scale(laneWidth*-2));
            gl.glTexCoord2d((segments/5)*t,0);
            gl.glVertex3d(v.x, v.y, v.z);
        }
        gl.glEnd();
        
        /** Draw outer side of track. */
        gl.glBegin(GL_QUAD_STRIP);
        for (int i = 0; i <= segments; i++) {
            t=((double) i)/segments;
            n = getUnitNormalPointingOut(t); // <------------------------NORMAL
            gl.glNormal3d(n.x, n.y,n.z); // <------------------------NORMAL
            //top
            v = getPoint(t).add(getUnitNormalPointingOut(t).scale(laneWidth*2));
            gl.glTexCoord2d((segments/5)*t,1);
            gl.glVertex3d(v.x, v.y, v.z);
            //bottom
            v = getPoint(t).add(Vector.Z.scale(-2)).add(getUnitNormalPointingOut(t).scale(laneWidth*2));
            gl.glTexCoord2d((segments/5)*t,0);
            gl.glVertex3d(v.x, v.y, v.z);
        }
        gl.glEnd();
        
        
        /** Draw road bottom. */
        gl.glBegin(GL_QUAD_STRIP);
        n = Vector.Z.scale(-1); // <------------------------NORMAL
        gl.glNormal3d(n.x, n.y,n.z); // <------------------------NORMAL
        for (int i = 0; i <= segments; i++) {
            t=((double) i)/segments;
            //outer
            v = getPoint(t).add(Vector.Z.scale(-2)).add(getUnitNormalPointingOut(t).scale(laneWidth*2));
            gl.glTexCoord2d((segments/5)*t,1);
            gl.glVertex3d(v.x, v.y, v.z);
            //inner
            v = getPoint(t).add(Vector.Z.scale(-2)).add(getUnitNormalPointingOut(t).scale(laneWidth*-2));
            gl.glTexCoord2d((segments/5)*t,0);
            gl.glVertex3d(v.x, v.y, v.z);
        }
        gl.glEnd();
    }
    
    /**
     * Returns the center of a lane at 0 <= t < 1.
     * Use this method to find the position of a robot on the track.
     */
    public Vector getLanePoint(int lane, double t){
        return getPoint(t).add(getUnitNormalPointingOut(t).scale(laneWidth*(-2+lane+0.5)));
    }
    
    /**
     * Returns the tangent of a lane at 0 <= t < 1.
     * Use this method to find the orientation of a robot on the track.
     */
    public Vector getLaneTangent(int lane, double t){
        return getTangent(t).normalized();
    }
    
    /**
     * Returns the tangent of a lane at 0 <= t < 1.
     * Use this method to find the orientation of a robot on the track.
     */
    private Vector getUnitNormalPointingOut(double t){
        Vector v = new Vector(0,0,0);
        v = getTangent(t);
        v = v.cross(Vector.Z);
        v = v.normalized();
        return v;
    }
    
    // Returns a point on the test track at 0 <= t < 1.
    protected abstract Vector getPoint(double t);

    // Returns a tangent on the test track at 0 <= t < 1.
    protected abstract Vector getTangent(double t);
}
