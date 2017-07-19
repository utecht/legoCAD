/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998, 1999, 2002 Kenneth B. Russell (kbrussel@alum.mit.edu)
 * See the file LICENSE.txt in the doc/ directory for licensing terms.
 */

package gnu.gleem;

import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import gnu.gleem.linalg.*;

/** <P> This is an application-level class, not part of the
    manipulator hierarchy. It is an example of how you might integrate
    gleem with another application which uses the mouse. </P>

    <P> For the given GLDrawable, the ExaminerViewer takes over the
    setting of the view position. It passes along mouse events it is
    not interested in to the ManipManager's mouse routines. </P>

    <P> The ExaminerViewer's controls are similar to those of Open
    Inventor's Examiner Viewer. Alt + Left mouse button causes
    rotation about the focal point. Alt + Right mouse button causes
    translation parallel to the image plane. Alt + both mouse buttons,
    combined with up/down mouse motion, causes zooming out and in
    along the view vector. (On platforms with a "Meta" key, that key
    can be substituted in place of the Alt key.) </P>

    <P>NOTE: the current ExaminerViewer implementation assumes a
    minimum of two mouse buttons. For the Mac OS, the code needs to be
    adjusted to use e.g., the Control key as the "right" mouse
    button. </P> */

public class ExaminerViewer {
	private GLAutoDrawable window;
	/** Simple state machine for figuring out whether we are grabbing
      events */
	private boolean interactionUnderway;
	private boolean iOwnInteraction;
	private GL lastGL = null;
	/** Simple state machine for computing distance dragged */
	private boolean button1Down;
	private boolean button2Down;
	private int numMouseButtons;
	private int lastX;
	private int lastY;

	/** Camera parameters */
	Vec3f position;
	Rotf orientation;
	float focalDist;
	float minFocalDist;
	float rotateSpeed;
	float minRotateSpeed;
	float dollySpeed;
	float minDollySpeed;
	CameraParameters params;
	private Set<MouseState> cameraModifiers;

	/** Our bounding sphere provider (for viewAll()) */
	BSphereProvider provider = null;
	private HashSet<Integer> buttons = new HashSet<Integer>();
	private MouseMotionAdapter mouseMotionListener = new MouseMotionAdapter() {
		public void mouseDragged(MouseEvent e) {
			motionMethod(e, e.getX(), e.getY());
		}

		public void mouseMoved(MouseEvent e) {
			passiveMotionMethod(e);
		}
	};

	private MouseAdapter mouseListener = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			mouseMethod(e, e.getModifiers(), true, e.getX(), e.getY());
		}

		public void mouseReleased(MouseEvent e) {
			mouseMethod(e, e.getModifiers(), false, e.getX(), e.getY());
		}
	};

	private GLEventListener glListener = new GLEventListener() {
		public void init(GLDrawable drawable) {}
		public void reshape(GLDrawable drawable, int width, int height) {
			reshapeMethod(width, height);
		}
		public void display(GLAutoDrawable arg0)
		{
		}
		public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2)
		{
		}
		public void init(GLAutoDrawable arg0)
		{
		}
		public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4)
		{
		}
	};

	/** The constructor takes the number of mouse buttons on this system
      (couldn't figure out how to determine this internally) */
	public ExaminerViewer(int numMouseButtons) {
		this.numMouseButtons = numMouseButtons;

		focalDist = 10.0f;
		minFocalDist = 1.0f;
		rotateSpeed = 4.0f;
		minRotateSpeed = 0.0001f;
		dollySpeed = 2.0f;
		minDollySpeed = 0.0001f;
		position = new Vec3f();
		orientation = new Rotf();
		params = new CameraParameters();
		cameraModifiers = new HashSet<MouseState>();
	}

	/** <P> Attaches this ExaminerViewer to the given GLDrawable. This
      causes the ManipManager's mouse routines to be removed from the
      window (using ManipManager.removeMouseListeners) and the
      ExaminerViewer's to be installed. The GLDrawable should be
      registered with the ManipManager before the ExaminerViewer is
      attached to it. </P>

      <P> In order for the viewer to do anything useful, you need to
      provide a BSphereProvider to it to allow "view all"
      functionality. </P> */
	public void attach(GLAutoDrawable window, BSphereProvider provider) {
		this.window = window;
		this.provider = provider;
		init();
		setupListeners();
	}

	/** Detaches from the given window. This causes the ManipManager's
      mouse listeners to be reinstalled on the GLDrawable and the
      ExaminerViewer's to be removed. */
	public void detach() {
		removeListeners();
		this.window = null;
		this.provider = null;
	}

	/** Call this at the end of your display() method to cause the
      Modelview matrix to be recomputed for the next frame. */
	public void update(GL gl) {
		recalc(gl);
	}

	/** Call this from within your display() method to cause the
      ExaminerViewer to recompute its position based on the visible
      geometry. A BSphereProvider must have already been set or this
      method has no effect. */
	public void viewAll(GL gl) {
		if (provider == null) {
			return;
		}
		// Figure out how far to move
		float vertFOV, horizFOV, minFOV;
		vertFOV = 2.0f * params.getVertFOV();
		horizFOV = 2.0f * (float) Math.atan(params.getImagePlaneAspectRatio() *
				Math.tan(params.getVertFOV()));
		if (vertFOV < horizFOV)
			minFOV = vertFOV;
		else
			minFOV = horizFOV;
		if (minFOV == 0.0f) {
			throw new RuntimeException("Minimum field of view was zero");
		}
		BSphere bsph = provider.getBoundingSphere();
		float dist = bsph.getRadius() / (float) Math.sin(minFOV / 2.0f);

		// Now position the camera this far back from the scene's center
		// along the negative of the current forward direction
		Vec3f dir = orientation.rotateVector(Vec3f.Z_AXIS);
		dir.scale(dist / dir.length());
		position.add(dir, bsph.getCenter());
		focalDist = dist;
		recalc(gl);
	}

	/** Get the camera parameters out of this Examiner Viewer (for
      example, to pass to ManipManager.updateCameraParameters()) */
	public CameraParameters getCameraParameters() {
		return params;
	}

	/** These routines can be hooked into a GUI by calling them from
      ActionEvent listeners for buttons elsewhere in the application. */
	public void rotateFaster() {
		rotateSpeed *= 2.0f;
	}

	public void rotateSlower() {
		if (rotateSpeed < minRotateSpeed)
			return;
		else
			rotateSpeed /= 2.0f;
	}

	public void dollyFaster() {
		dollySpeed *= 2.0f;
	}

	public void dollySlower() {
		if (dollySpeed < minDollySpeed)
			return;
		else
			dollySpeed /= 2.0f;
	}
	public void addCameraModifer(MouseState s)
	{
		cameraModifiers.add(s);
	}
	public void removeCameraModifier(MouseState s)
	{
		cameraModifiers.remove(s);
	}
	//----------------------------------------------------------------------
	// Internals only below this point
	//

	private static final float EPSILON = 0.0001f;

	private void setupListeners() {
		ManipManager.getManipManager().removeMouseListeners(window);
		window.addMouseMotionListener(mouseMotionListener);
		window.addMouseListener(mouseListener);
		window.addGLEventListener(glListener);
	}

	private void removeListeners() {
		window.removeMouseMotionListener(mouseMotionListener);
		window.removeMouseListener(mouseListener);
		window.removeGLEventListener(glListener);
		ManipManager.getManipManager().setupMouseListeners(window);
	}

	private void passiveMotionMethod(MouseEvent e) {
		ManipManager.getManipManager().mouseMoved(e);
	}

	private static boolean modifiersMatch(MouseEvent e) {
		return true;
		//return ((e.isAltDown() || e.isMetaDown()) &&
		//(!e.isControlDown() && !e.isShiftDown()));
	}

	private void init() {
		focalDist = 10.0f;
		minFocalDist = 1.0f;
		rotateSpeed = 4.0f;
		minRotateSpeed = 0.0001f;
		dollySpeed = 2.0f;
		minDollySpeed = 0.0001f;
		interactionUnderway = false;
		iOwnInteraction = false;
		button1Down = false;
		button2Down = false;
		//provider = null;
		//position.set(0, 0, 0);

		Dimension size = new Dimension(window.getWidth(), window.getHeight());
		int xSize = size.width;
		int ySize = size.height;
		params.setPosition(position);
		params.setForwardDirection(Vec3f.NEG_Z_AXIS);
		params.setUpDirection(Vec3f.Y_AXIS);
		params.setVertFOV((float) Math.PI / 8.0f);
		params.setImagePlaneAspectRatio((float) xSize / (float) ySize);
		params.setXSize(xSize);
		params.setYSize(ySize);
	}

	private void motionMethod(MouseEvent e, int x, int y) {

		boolean somethingMatches = false;
		//if (interactionUnderway && !iOwnInteraction) {
			//ManipManager.getManipManager().mouseDragged(e);
		//} else {

		int dx = x - lastX;
		int dy = y - lastY;

		lastX = x;
		lastY = y;
		for(MouseState state: cameraModifiers)
		{
			if(state.matches(buttons, e.getModifiers()))
			{
				state.performModification(e, this, dx, dy);
				somethingMatches = true;
			}
		}
		if(!somethingMatches) ManipManager.getManipManager().mouseDragged(e);
		window.repaint();
		//}
	}

	private void mouseMethod(MouseEvent e, int mods, boolean press,
			int x, int y) {
		boolean somethingMatches = false;
		if(press)
		{
			buttons.add(e.getButton());
		} else
		{
			buttons.remove(e.getButton());
		}


		for(MouseState state: cameraModifiers)
		{
			if(state.matches(buttons, e.getModifiers()))
			{

				somethingMatches = true;
			}
		}
		if(!somethingMatches)
		{
			if(press) ManipManager.getManipManager().mousePressed(e);
			else ManipManager.getManipManager().mouseReleased(e);
		}


		lastX = x;
		lastY = y;


		// Force redraw if window will not do it automatically
		//if (!(window instanceof GLRunnable)) {
		window.repaint();
		//}
	}

	private void reshapeMethod(int w, int h) {
		float aspect, theta;
		aspect = (float) w / (float) h;
		if (w >= h)
			theta = 45;
		else
			theta = (float) Math.toDegrees(Math.atan(1 / aspect));
		params.setVertFOV((float) (Math.toRadians(theta) / 2.0));
		params.setImagePlaneAspectRatio(aspect);
		params.setXSize(w);
		params.setYSize(h);
	}

	private void recalc(GL gl) {
		lastGL = gl;
		GLU glu = new GLU();
		// Recompute position, forward and up vectors
		params.setPosition(position);
		Vec3f tmp = new Vec3f();
		orientation.rotateVector(Vec3f.NEG_Z_AXIS, tmp);
		params.setForwardDirection(tmp);
		orientation.rotateVector(Vec3f.Y_AXIS, tmp);
		params.setUpDirection(tmp);

		// Compute modelview matrix based on camera parameters, position and
		// orientation
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		float ang = orientation.get(tmp);
		if (tmp.lengthSquared() > EPSILON)
			gl.glRotatef((float) Math.toDegrees(ang), -tmp.x(), -tmp.y(), -tmp.z());
		gl.glTranslatef(-position.x(), -position.y(), -position.z());
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(Math.toDegrees(params.getVertFOV() * 2.0),
				params.getImagePlaneAspectRatio(),
				1, 2000);
	}
	public void setPosition(Vec3f origin)
	{
		position = origin;
	}

	public float getDollySpeed()
	{
		return dollySpeed;
	}

	public Vec3f getPosition()
	{
		return position;
	}

	public Rotf getRotation()
	{
		return orientation;
	}

	public float getRotateSpeed()
	{
		return rotateSpeed;
	}

	public float getFocalDist()
	{
		return focalDist;
	}

	public void setRotation(Rotf orient)
	{
		orientation = orient;
	}

	public float getMinFocalDist()
	{
		return minFocalDist;
	}
}
