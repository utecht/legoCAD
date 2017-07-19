/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.awt.*;
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import gnu.gleem.linalg.*;

/** Tests the Translate2 Manip. */

public class TestTranslate2 {
  private static final int X_SIZE = 400;
  private static final int Y_SIZE = 400;

  static class Listener implements GLEventListener {
    private GL gl;
    private GLU glu;
    private CameraParameters params = new CameraParameters();

    public void init(GLAutoDrawable drawable) {
      gl = drawable.getGL();
      glu = new GLU();

      gl.glClearColor(0, 0, 0, 0);
      float[] lightPosition = new float[] {1, 1, 1, 0};
      float[] ambient       = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
      float[] diffuse       = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
      gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT,  ambient, 0);
      gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,  diffuse, 0);
      gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition, 0);

      gl.glEnable(GL.GL_LIGHTING);
      gl.glEnable(GL.GL_LIGHT0);
      gl.glEnable(GL.GL_DEPTH_TEST);

      params.setPosition(new Vec3f(0, 0, 0));
      params.setForwardDirection(Vec3f.NEG_Z_AXIS);
      params.setUpDirection(Vec3f.Y_AXIS);
      params.setVertFOV((float) (Math.PI / 8.0));
      params.setImagePlaneAspectRatio(1);
      params.xSize = X_SIZE;
      params.ySize = Y_SIZE;

      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glLoadIdentity();
      glu.gluPerspective(45, 1, 1, 100);
      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glLoadIdentity();

      // Register the window with the ManipManager
      ManipManager manager = ManipManager.getManipManager();
      manager.registerWindow(drawable);

      // Instantiate a Translate2Manip
      Translate2Manip manip = new Translate2Manip();
      manip.setTranslation(new Vec3f(0, 0, -5));
      manip.setNormal(new Vec3f(0, 0, 1));
      manager.showManipInWindow(manip, drawable);
    }

    public void display(GLAutoDrawable drawable) {
      gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
      ManipManager.getManipManager().updateCameraParameters(drawable, params);
      ManipManager.getManipManager().render(drawable, gl);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
      float aspect, theta;
      aspect = (float) w / (float) h;
      if (w >= h)
        theta = 45;
      else
        theta = (float) Math.toDegrees(Math.atan(1 / aspect));
      params.setVertFOV((float) Math.toRadians(theta) / 2.0f);
      params.setImagePlaneAspectRatio(aspect);
      params.setXSize(w);
      params.setYSize(h);
      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glLoadIdentity();
      glu.gluPerspective(theta, aspect, 1, 100);
      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glLoadIdentity();
    }

    // Unused routines
    public void preDisplay(GLDrawable drawable) {}
    public void postDisplay(GLDrawable drawable) {}
    public void cleanup(GLDrawable drawable) {}

	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2)
	{
		// TODO Auto-generated method stub

	}
  }

  public static void main(String[] args) {
    Frame frame = new Frame("Translate2 Test");
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
    frame.setLayout(new BorderLayout());
    GLCanvas canvas = new GLCanvas(new GLCapabilities());
    canvas.setSize(500, 500);
    canvas.addGLEventListener(new Listener());
    frame.add(canvas, BorderLayout.CENTER);
    frame.pack();
    frame.show();
  }
}
