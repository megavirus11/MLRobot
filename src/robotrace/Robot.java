package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Robot {
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);

    /** The material from which this robot is built. */
    private final Material material;
    
    /** Head tilt. */
    public double headTilt = 0;
    
    /** Left arm tilt. */
    public double leftArmTilt = 0;
    
    /** Right arm tilt. */
    public double rightArmTilt = 0;
    
    /** Left leg tilt. */
    public double leftLegTilt = 0;
    
    /** Right leg tilt. */
    public double rightLegTilt = 0;

    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material
            
    ) {
        this.material = material;

        
    }

    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, float tAnim) {
        //initialize materials
        gl.glPushMatrix();
            gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, this.material.specular, 0);
            gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, this.material.diffuse, 0);
            gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, this.material.shininess);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslated(this.position.x, this.position.y, this.position.z);
        drawHead(gl, glu, glut, tAnim); 
        drawTorso(gl, glu, glut, tAnim);    
        drawLeftArm(gl, glu, glut, tAnim);
        drawRightArm(gl, glu, glut, tAnim);
        drawLeftLeg(gl, glu, glut, tAnim);
        drawRightLeg(gl, glu, glut, tAnim);
        gl.glPopMatrix();
        
        
    }
        
    private void drawHead(GL2 gl, GLU glu, GLUT glut, float tAnim) {
        gl.glPushMatrix();
            gl.glTranslated(0,0,0.55);
            gl.glRotated(this.headTilt * -15.0, 1, 0, 0);
            gl.glTranslated(0,0,0.15);
            glut.glutSolidCylinder(0.15, 0.30, 12, 12);
            gl.glTranslated(0,0,-0.15);
            glut.glutSolidCylinder(0.07, 0.15, 12, 12);
        gl.glPopMatrix();
    }
    
    private void drawTorso(GL2 gl, GLU glu, GLUT glut, float tAnim) {
        gl.glPushMatrix();
            gl.glTranslated(0,0,0.2);
            gl.glScaled(0.6, 0.4, 0.8);
            glut.glutSolidCube(1);
        gl.glPopMatrix();
    }
    
    private void drawLeftArm(GL2 gl, GLU glu, GLUT glut, float tAnim) {
        gl.glPushMatrix();
            gl.glTranslated(-0.4,0,0.45);
            gl.glRotated(this.leftArmTilt * -45.0, 1, 0, 0);
            gl.glTranslated(0,0,-0.4);
            gl.glScaled(0.2, 0.1, 0.8);
            glut.glutSolidCube(1);
        gl.glPopMatrix();
    }
    
    private void drawRightArm(GL2 gl, GLU glu, GLUT glut, float tAnim) {
        gl.glPushMatrix();
            gl.glTranslated(0.4,0,0.45);
            gl.glRotated(this.rightArmTilt * 45.0, 1, 0, 0);
            gl.glTranslated(0,0,-0.4);
            gl.glScaled(0.2, 0.1, 0.8);
            glut.glutSolidCube(1);
        gl.glPopMatrix();
    }
    
    private void drawLeftLeg(GL2 gl, GLU glu, GLUT glut, float tAnim) {
        gl.glPushMatrix();
            gl.glTranslated(-0.15,0,-0.2);
            gl.glRotated(this.leftLegTilt * 45.0, 1, 0, 0);
            gl.glTranslated(0,0,-0.4);
            gl.glScaled(0.2, 0.1, 0.8);
            glut.glutSolidCube(1);
        gl.glPopMatrix();
    }
    
    private void drawRightLeg(GL2 gl, GLU glu, GLUT glut, float tAnim) {
        gl.glPushMatrix();
            gl.glTranslated(0.15,0,-0.2);
            gl.glRotated(this.rightLegTilt * -45.0, 1, 0, 0);
            gl.glTranslated(0,0,-0.4);
            gl.glScaled(0.2, 0.1, 0.8);
            glut.glutSolidCube(1);
        gl.glPopMatrix();
    }
    
}
