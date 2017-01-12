package robotrace;

import javax.media.opengl.GL2;

import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;
import static robotrace.General.*;


public class Lighting {
    private float[] lightPosition = new float[4];
    //The diffuse component of the light source.
    private float[] diffuseLight = {1f, 1f, 1f, 1f};
     //The specular component of the light source.
    private float[] specularLight = {1f, 1f, 1f, 1f};
    //The component of the light source.
    private float[] ambientLight = {0.1f, 0.1f, 0.1f, 1f};

    /**
     * Called during openGL initialisation. This enables the necessary settings
     * and sets up the light source.
     *
     * @param gl The GL2 instance responsible for drawing the scene.
     * @param gs The GlobalState.
     */
    public void initialize(GL2 gl, GlobalState gs) {
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        SetSunPosition(gs);
        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight, 0);
        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, specularLight, 0);
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight, 0);
    }

    private void SetSunPosition(GlobalState gs) {
        final float azimuth = getAzimuth(gs)-10f;
        final float inclination = getInclination(gs)-10f;
        //Calculate the x coordinate of the sun point relative to the center point.
        final double xSunLocal = Math.cos(azimuth) * Math.cos(inclination) * gs.vDist;
        //Calculate the y coordinate of the sun point relative to the center point.
        final double ySunLocal = Math.sin(azimuth) * Math.cos(inclination) * gs.vDist;
        //Calculate the z coordinate of the sun point relative to the center point.
        final double zSunLocal = Math.sin(inclination) * gs.vDist;
        //Create a new vector with the local eye coördinates, IE relative to the center point.
        final Vector localSun = new Vector(xSunLocal, ySunLocal, zSunLocal);
        //Add the relative offet of the center point to the newly calculated coordinates of the sun point.
        final Vector worldSun = localSun.add(gs.cnt);
        this.lightPosition[0] = (float) worldSun.x();
        this.lightPosition[1] = (float) worldSun.y();
        this.lightPosition[2] = (float) worldSun.z();
        this.lightPosition[3] = 0; //infinite
    }

    /**
     * Called during the setView part of the loop. Reset the position of the
     * light source here, otherwise it will move with the camera.
     */
    public void setView(GL2 gl) {
        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);
    }

    public void setColor(GL2 gl, float red, float green, float blue, float alpha) {
        gl.glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, new float[]{red, green, blue, alpha}, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, new float[]{0f, 0f, 0f, 0f}, 0);
        gl.glMaterialf(GL_FRONT, GL_SHININESS, 0);
    }
}
