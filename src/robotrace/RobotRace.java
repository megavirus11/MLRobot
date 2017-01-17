package robotrace;

import static javax.media.opengl.GL2.*;
import static robotrace.ShaderPrograms.*;

/**
 * Handles all of the RobotRace graphics functionality,
 * which should be extended per the assignment.
 *
 * OpenGL functionality:
 * - Basic commands are called via the gl object;
 * - Utility commands are called via the glu and
 *   glut objects;
 *
 * GlobalState:
 * The gs object contains the GlobalState as described
 * in the assignment:
 * - The camera viewpoint angles, phi and theta, are
 *   changed interactively by holding the left mouse
 *   button and dragging;
 * - The camera view width, vWidth, is changed
 *   interactively by holding the right mouse button
 *   and dragging upwards or downwards; (Not required in this assignment)
 * - The center point can be moved up and down by
 *   pressing the 'q' and 'z' keys, forwards and
 *   backwards with the 'w' and 's' keys, and
 *   left and right with the 'a' and 'd' keys;
 * - Other settings are changed via the menus
 *   at the top of the screen.
 *
 * Textures:
 * Place your "track.jpg", "brick.jpg", "head.jpg",
 * and "torso.jpg" files in the folder textures.
 * These will then be loaded as the texture
 * objects track, bricks, head, and torso respectively.
 * Be aware, these objects are already defined and
 * cannot be used for other purposes. The texture
 * objects can be used as follows:
 *
 * gl.glColor3f(1f, 1f, 1f);
 * Textures.track.bind(gl);
 * gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0);
 * gl.glVertex3d(0, 0, 0);
 * gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0);
 * gl.glTexCoord2d(1, 1);
 * gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1);
 * gl.glVertex3d(0, 1, 0);
 * gl.glEnd();
 *
 * Note that it is hard or impossible to texture
 * objects drawn with GLUT. Either define the
 * primitives of the object yourself (as seen
 * above) or add additional textured primitives
 * to the GLUT object.
 */
public class RobotRace extends Base {

    /** Array of the four robots. */
    private final Robot[] robots;

    /** Instance of the camera. */
    private final Camera camera;

    /** Instance of the race track. */
    private final RaceTrack[] raceTracks;

    /** Instance of the terrain. */
    private final Terrain terrain;
    
    private double timeTicker = 0;
    private double speedTimeTicker = 0;
    private double speedTracker[] = {(double)17/20, (double)18/20, (double)19/20, (double)20/20};

    /**
     * Constructs this robot race by initializing robots,
     * camera, track, and terrain.
     */

    private final Lighting lighting = new Lighting();

    public RobotRace() {

        // Create a new array of four robots
        robots = new Robot[4];

        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD

        );

        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER

        );

        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD

        );

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE

        );

        // Initialize the camera
        camera = new Camera();

        // Initialize the race tracks
        raceTracks = new RaceTrack[2];

        // Track 1
        raceTracks[0] = new ParametricTrack();

        // Track 2
        float g = 3.5f;
        raceTracks[1] = new BezierTrack(

            new Vector[] {
//                new Vector(10,0,1),
//                new Vector(10,10,1),
//                new Vector(-10,10,1),
//                new Vector(-10,0,1),
//                new Vector(-10,-10,1),
//                new Vector(10,-10,1)
                new Vector(4,0,1),
                    new Vector(4,5,1),
                    new Vector(10,5,1),
                new Vector(10,10,1),
                    new Vector(10,15,1),
                    new Vector(5,15,1),
                new Vector(0,15,1),
                    new Vector(-5,15,1),
                    new Vector(-10,15,1),
                new Vector(-10,10,1),
                    new Vector(-10,5,1),
                    new Vector(-4,5,1),
                new Vector(-4,0,1),
                    new Vector(-4,-5,1),
                    new Vector(-10,-5,1),
                new Vector(-10,-10,1),
                    new Vector(-10,-15,1),
                    new Vector(-5,-15,1),
                new Vector(0,-15,1),
                    new Vector(5,-15,1),
                    new Vector(10,-15,1),
                new Vector(10,-10,1),
                    new Vector(10,-5,1),
                    new Vector(4,-5,1)
            }

        );

        // Initialize the terrain
        terrain = new Terrain();
    }

    /**
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    @Override
    public void initialize() {

        lighting.initialize(gl, gs);
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        // Enable face culling for improved performance
        // gl.glCullFace(GL_BACK);
        // gl.glEnable(GL_CULL_FACE);

	    // Normalize normals.
        gl.glEnable(GL_NORMALIZE);

	// Try to load four textures, add more if you like in the Textures class
        Textures.loadTextures();
        reportError("reading textures");

        // Try to load and set up shader programs
        ShaderPrograms.setupShaders(gl, glu);
        reportError("shaderProgram");

    }

    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);

        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        glu.gluPerspective(45, (float)gs.w / (float)gs.h, 0.1*gs.vDist, 10*gs.vDist);

        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        camera.update(gs, robots[3]);
        glu.gluLookAt(camera.eye.x(),    camera.eye.y(),    camera.eye.z(),
                      camera.center.x(), camera.center.y(), camera.center.z(),
                      camera.up.x(),     camera.up.y(),     camera.up.z());
        lighting.setView(gl);
    }

    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {

        //gl.glUseProgram(defaultShader.getProgramID());
        reportError("program");

        // Background color.
        //gl.glClearColor((135f / 255f), (206f / 255f), (250f / 255f), 0f); //blue
        gl.glClearColor(255f,255f,25f, 0f);

        // Clear background and depth buffer.
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);


    // Draw hierarchy example.
        //drawHierarchy();

        // Draw the axis frame.
        if (gs.showAxes) {
            drawAxisFrame();
        }
        double t = updateTimeTicker(gs.tAnim);
        for (int i = 0; i<4; i++) {
            robots[i].draw(gl, glu, glut, 0);
            robots[i].timeTracker += t*speedTracker[i];
            System.out.println("speedTracker["+i+"]="+speedTracker[i]);
            
            robots[i].position = raceTracks[gs.trackNr].getLanePoint(i, robots[i].timeTracker/10.5);
            robots[i].direction = raceTracks[gs.trackNr].getLaneTangent(i, robots[i].timeTracker/10.5);
            robots[i].viewDirection = raceTracks[gs.trackNr].getLanePoint(i, (robots[i].timeTracker+1)/10.5);
            robots[i].headTilt = Math.sin(robots[i].timeTracker*7.5);
            robots[i].leftArmTilt = Math.sin(robots[i].timeTracker*7.5);
            robots[i].rightArmTilt = Math.sin(robots[i].timeTracker*7.5);
            robots[i].leftLegTilt = Math.sin(robots[i].timeTracker*7.5);
            robots[i].rightLegTilt = Math.sin(robots[i].timeTracker*7.5);
        }
        
        // Draw the race track.
        gl.glUseProgram(trackShader.getProgramID());
        raceTracks[gs.trackNr].draw(gl, glu, glut);

        // Draw the terrain.
        gl.glUseProgram(terrainShader.getProgramID());
        terrain.draw(gl, glu, glut, lighting);
        reportError("terrain:");


    }
    
    private double updateTimeTicker(double t) {
        double fps = 60;
        t*=fps;
        double r = 0;
        if (Math.floor(t) != timeTicker){
            timeTicker = Math.floor(t);
            r = (double)1/fps;
            
        }
        
        return r;
    }
    
    private void updateSpeedTracker(double t) {
        if (Math.floor(t) != speedTimeTicker){
            speedTimeTicker = Math.floor(t);
            for (int i = 0; i < 4;i++){
                speedTracker[i] += (Math.random()/10)-0.05;
                if (speedTracker[i] > 1.2) {
                    speedTracker[i] = 1.1;
                }else if (speedTracker[i] < 0.8) {
                    speedTracker[i] = 0.8;
                }
            }
        }
    }

    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue),
     * and origin (yellow).
     */
    public void drawAxisFrame() {
        lighting.setColor(gl, 1f, 1f, 0f, 1f);
        glut.glutSolidSphere(0.1, 12, 12);
        drawAxis(1f, 0f, 0f);
        drawAxis(0f, 1f, 0f);
        drawAxis(0f, 0f, 1f);

        //Reset the color back to black.
        gl.glColor3f(0f, 0f, 0f);
    }

    public void drawAxis(float x, float y, float z)
    {
        lighting.setColor(gl, x, y, z, 1f);
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glRotatef(z == 1f ? 0f : 90f, -y, x, 0f);
        glut.glutSolidCone(0.1f, 1/6f, 12, 1);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(x/2, y/2, z/2); //Move it to either x, y, or z (depending on whats not zero)
        gl.glScalef(x == 0f ? 1f : 20, //Just scale it to either side, it gets put at 0.5 coords, scale it to 1 making it go from 0 to 1
                y == 0f ? 1f : 20,
                z == 0f ? 1f : 20);
        glut.glutSolidCube(0.05f);
        gl.glPopMatrix();

    }

    /**
     * Drawing hierarchy example.
     *
     * This method draws an "arm" which can be animated using the sliders in the
     * RobotRace interface. The A and B sliders rotate the different joints of
     * the arm, while the C, D and E sliders set the R, G and B components of
     * the color of the arm respectively.
     *
     * The way that the "arm" is drawn (by calling {@link #drawSecond()}, which
     * in turn calls {@link #drawThird()} imposes the following hierarchy:
     *
     * {@link #drawHierarchy()} -> {@link #drawSecond()} -> {@link #drawThird()}
     */
    private void drawHierarchy() {
        gl.glColor3d(gs.sliderC, gs.sliderD, gs.sliderE);
        gl.glPushMatrix();
            gl.glTranslated(1, 0, 0);
            gl.glScaled(2, 1, 1);
            glut.glutSolidCube(1);
            gl.glScaled(0.5, 1, 1);
            gl.glTranslated(1, 0, 0);
            gl.glRotated(gs.sliderA * -90.0, 0, 1, 0);
            drawSecond();
        gl.glPopMatrix();
    }

    private void drawSecond() {
        gl.glTranslated(1, 0, 0);
        gl.glScaled(2, 1, 1);
        glut.glutSolidCube(1);
        gl.glScaled(0.5, 1, 1);
        gl.glTranslated(1, 0, 0);
        gl.glRotated(gs.sliderB * -90.0, 0, 1, 0);
        drawThird();
    }

    private void drawThird() {
        gl.glTranslated(1, 0, 0);
        gl.glScaled(2, 1, 1);
        glut.glutSolidCube(1);
    }


    /**
     * Main program execution body, delegates to an instance of
     * the RobotRace implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    }
}
