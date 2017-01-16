package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.sqrt;

import javax.media.opengl.GL;

/**
 * Represents the terrain, to be implemented according to the Assignments.
 */
class Terrain {


    public Terrain() {

    }

    /**
     * Draws the terrain.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, Lighting lighting) {
        terrainLoadFromImage("textures/3dtech0.jpg", 1);
        terrainScale(0, 40);
        terrainCreateDL(gl, glu, glut, 0, -50f, 0, lighting);
    }

    public static int terrainGridWidth, terrainGridLength;
    public static float[] terrainHeights;
    public static int[] terrainColors;
    public static float[] terrainNormals;


    public float[] terrainCrossProduct(int x1, int z1, int x2, int z2, int x3, int z3) {

        float[] auxNormal = new float[3], v1 = new float[3], v2 = new float[3];

        v1[0] = x2 - x1;
        v1[1] = -terrainHeights[z1 * terrainGridWidth + x1]
                + terrainHeights[z2 * terrainGridWidth + x2];
        v1[2] = z2 - z1;


        v2[0] = x3 - x1;
        v2[1] = -terrainHeights[z1 * terrainGridWidth + x1]
                + terrainHeights[z3 * terrainGridWidth + x3];
        v2[2] = z3 - z1;


        auxNormal[2] = v1[0] * v2[1] - v1[1] * v2[0];
        auxNormal[0] = v1[1] * v2[2] - v1[2] * v2[1];
        auxNormal[1] = v1[2] * v2[0] - v1[0] * v2[2];

        return (auxNormal);
    }

    void terrainNormalize(float[] v) {

        double d, x, y, z;

        x = v[0];
        y = v[1];
        z = v[2];

        d = sqrt((x * x) + (y * y) + (z * z));

        v[0] = (float) (x / d);
        v[1] = (float) (y / d);
        v[2] = (float) (z / d);
    }

    void terrainAddVector(float[] a, float[] b) {

        a[0] += b[0];
        a[1] += b[1];
        a[2] += b[2];
    }

    void terrainComputeNormals() {

        float[] norm1, norm2 = new float[3], norm3 = new float[3], norm4 = new float[3];
        int i, j, k;

        if (terrainNormals == null)
            return;

        for (i = 0; i < terrainGridLength; i++)
            for (j = 0; j < terrainGridWidth; j++) {

                // normals for the four corners
                if (i == 0 && j == 0) {
                    norm1 = terrainCrossProduct(0, 0, 0, 1, 1, 0);
                    terrainNormalize(norm1);
                } else if (j == terrainGridWidth - 1 && i == terrainGridLength - 1) {
                    norm1 = terrainCrossProduct(i, j, j, i - 1, j - 1, i);
                    terrainNormalize(norm1);
                } else if (j == 0 && i == terrainGridLength - 1) {
                    norm1 = terrainCrossProduct(i, j, j, i - 1, j + 1, i);
                    terrainNormalize(norm1);
                } else if (j == terrainGridWidth - 1 && i == 0) {
                    norm1 = terrainCrossProduct(i, j, j, i + 1, j - 1, i);
                    terrainNormalize(norm1);
                }

                // normals for the borders
                else if (i == 0) {
                    norm1 = terrainCrossProduct(j, 0, j - 1, 0, j, 1);
                    terrainNormalize(norm1);
                    norm2 = terrainCrossProduct(j, 0, j, 1, j + 1, 0);
                    terrainNormalize(norm2);
                } else if (j == 0) {
                    norm1 = terrainCrossProduct(0, i, 1, i, 0, i - 1);
                    terrainNormalize(norm1);
                    norm2 = terrainCrossProduct(0, i, 0, i + 1, 1, i);
                    terrainNormalize(norm2);
                } else if (i == terrainGridLength) {
                    norm1 = terrainCrossProduct(j, i, j, i - 1, j + 1, i);
                    terrainNormalize(norm1);
                    norm2 = terrainCrossProduct(j, i, j + 1, i, j, i - 1);
                    terrainNormalize(norm2);
                } else if (j == terrainGridWidth) {
                    norm1 = terrainCrossProduct(j, i, j, i - 1, j - 1, i);
                    terrainNormalize(norm1);
                    norm2 = terrainCrossProduct(j, i, j - 1, i, j, i + 1);
                    terrainNormalize(norm2);
                }

                // normals for the interior
                else {
                    norm1 = terrainCrossProduct(j, i, j - 1, i, j, i + 1);
                    terrainNormalize(norm1);
                    norm2 = terrainCrossProduct(j, i, j, i + 1, j + 1, i);
                    terrainNormalize(norm2);
                    norm3 = terrainCrossProduct(j, i, j + 1, i, j, i - 1);
                    terrainNormalize(norm3);
                    norm4 = terrainCrossProduct(j, i, j, i - 1, j - 1, i);
                    terrainNormalize(norm4);
                }
                if (norm2 != null) {
                    terrainAddVector(norm1, norm2);
                }
                if (norm3 != null) {
                    terrainAddVector(norm1, norm3);
                }
                if (norm4 != null) {
                    terrainAddVector(norm1, norm4);
                }
                terrainNormalize(norm1);
                norm1[2] = -norm1[2];
                for (k = 0; k < 3; k++)
                    terrainNormals[3 * (i * terrainGridWidth + j) + k] = norm1[k];
            }

    }


    int terrainLoadFromImage(String filename, int normals) {
        int mode;
        float pointHeight;
        BufferedImage height = null;
        BufferedImage color = null;
        try {
            height = ImageIO.read(new File(getClass().getResource(filename).toURI()));
            color = ImageIO.read(new File(getClass().getResource("textures/terrain.png").toURI()));

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

// set the width and height of the terrain
        if (height != null) {
            terrainGridWidth = height.getWidth();
            terrainGridLength = height.getHeight();

        }

        terrainNormals = null;
        terrainHeights = new float[terrainGridWidth * terrainGridLength];
        terrainColors = new int[terrainGridWidth * terrainGridLength];
// fill arrays
        for (int i = 0; i < terrainGridLength; i++)
            for (int j = 0; j < terrainGridWidth; j++) {
                float kappa = height.getRGB(j, i);
                int kappa1 = (int) kappa & 255;
                double kappa2 = (float) kappa1 / 255.0;
                terrainHeights[i * terrainGridWidth + j] = (float) ((height.getRGB(j, i) & 255) / 255.0);
                int kappa3 = (color.getRGB((int) (color.getWidth() * ((height.getRGB(j, i) & 255) / 255.0)), 0));
                terrainColors[i * terrainGridWidth + j] = color.getRGB((int) (color.getWidth() * ((height.getRGB(j, i) & 255) / 255.0)), 0);
            }
// if we want normals then compute them
        terrainComputeNormals();
// free the image's memory

        return 1;
    }

    int terrainScale(float min, float max) {

        float amp, aux, min1, max1, height;
        int total, i;

        if (min > max) {
            aux = min;
            min = max;
            max = aux;
        }

        amp = max - min;
        total = terrainGridWidth * terrainGridLength;

        min1 = terrainHeights[0];
        max1 = terrainHeights[0];
        for (i = 1; i < total; i++) {
            if (terrainHeights[i] > max1)
                max1 = terrainHeights[i];
            if (terrainHeights[i] < min1)
                min1 = terrainHeights[i];
        }
        for (i = 0; i < total; i++) {
            height = (terrainHeights[i] - min1) / (max1 - min1);
            terrainHeights[i] = height * amp - min;
        }
        if (terrainNormals != null)
            terrainComputeNormals();
        return 1;
    }


    public int terrainCreateDL(GL2 gl, GLU glu, GLUT glut, float xOffset, float yOffset, float zOffset, Lighting lighting) {

        int terrainDL;
        float startW, startL;
        int i, j;

        startW = (float) (terrainGridWidth / 2.0 - terrainGridWidth);
        startL = (float) (-terrainGridLength / 2.0 + terrainGridLength);

        terrainDL = gl.glGenLists(1);

        //glNewList(terrainDL, GL_COMPILE);
        /*if (terrainNormals != null && terrainColors != null) {
            glColorMaterial(GL_FRONT, GL_DIFFUSE);
            glEnable(GL_COLOR_MATERIAL);
        }*/
        Color color;
        for (i = 0; i < terrainGridLength - 1; i++) {
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for (j = 0; j < terrainGridWidth; j++) {

                /*if (terrainColors != null)
                    glColor3f(terrainColors[3 * ((i + 1) * terrainGridWidth + j)],
                            terrainColors[3 * ((i + 1) * terrainGridWidth + j) + 1],
                            terrainColors[3 * ((i + 1) * terrainGridWidth + j) + 2]);*/
                color = new Color(terrainColors[(i + 1) * terrainGridWidth + (j)]);
                //lighting.setColor(gl, color.getRed()/255, color.getGreen()/255, color.getBlue()/255, 1f);
                gl.glColor3f(color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f);
                if (terrainNormals != null) {
                    gl.glNormal3f(terrainNormals[3 * ((i + 1) * terrainGridWidth + j)],
                            terrainNormals[3 * ((i + 1) * terrainGridWidth + j) + 1],
                            terrainNormals[3 * ((i + 1) * terrainGridWidth + j) + 2]);
                }
                gl.glVertex3f(
                        startW + j + xOffset,
                        startL - (i + 1) + zOffset,
                        terrainHeights[(i + 1) * terrainGridWidth + (j)] + yOffset);
                

                /*if (terrainColors != null)
                    glColor3f(terrainColors[3 * (i * terrainGridWidth + j)],
                            terrainColors[3 * (i * terrainGridWidth + j) + 1],
                            terrainColors[3 * (i * terrainGridWidth + j) + 2]);*/
                color = new Color(terrainColors[(i + 1) * terrainGridWidth + (j)]);
                //lighting.setColor(gl, color.getRed()/255, color.getGreen()/255, color.getBlue()/255, 1f);
                gl.glColor3f(color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f);
                if (terrainNormals != null) {
                    gl.glNormal3f(terrainNormals[3 * (i * terrainGridWidth + j)],
                            terrainNormals[3 * (i * terrainGridWidth + j) + 1],
                            terrainNormals[3 * (i * terrainGridWidth + j) + 2]);
                }
                gl.glVertex3f(
                        startW + j + xOffset,
                        startL - i + zOffset,
                        terrainHeights[(i) * terrainGridWidth + j] + yOffset);
            }
            gl.glEnd();
        }
        return (terrainDL);
    }

    float terrainGetHeight(int x, int z) {

        int xt, zt;

        if (terrainHeights == null)
            return (0.0f);

        xt = x + terrainGridWidth / 2;
        zt = terrainGridWidth - (z + terrainGridLength / 2);

        if ((xt > terrainGridWidth) || (zt > terrainGridLength) || (xt < 0) || (zt < 0))
            return (0.0f);

        return (terrainHeights[zt * terrainGridWidth + xt]);
    }

    public void reportError(GL gl, GLU glu, String prefix) {
        // Report OpenGL errors.
        int errorCode = gl.glGetError();
        while (errorCode != GL.GL_NO_ERROR) {
            System.err.format("%s:%d: %s\n", prefix, errorCode, glu.gluErrorString(errorCode));
            errorCode = gl.glGetError();
        }
    }


}
