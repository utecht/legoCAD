package view;


import gnu.gleem.BSphere;
import gnu.gleem.BSphereProvider;
import gnu.gleem.CameraParameters;
import gnu.gleem.ExaminerViewer;
import gnu.gleem.ManipManager;
import gnu.gleem.Translate1Manip;
import gnu.gleem.linalg.Rotf;
import gnu.gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import model.Lego;
import model.MouseDolly;
import model.MouseRotate;
import model.MouseTranslate;
import model.Point3;
import model.Shape;
import model.Shape.CubeBounds;


//http://www.opengl.org/documentation/specs/man_pages/hardcopy/GL/html/
//The above link is the OpenGL API, description of all gl methods used here should be described there.

public class Scene implements GLEventListener {

	private static final float AXIS_SCALE = 0.1f;
	private static Map<GLAutoDrawable, CameraData> paramMap;
	private GLAutoDrawable selectedCanvas;


	private boolean useCenter = true;
	private Point3 center = Point3.ZERO();

	private GLU glu = new GLU();


	private float drawDistance = 2000;
	private float h;

	private List<Lego> legos;

	private Translate1Manip yManip = new Translate1Manip();
	private Translate1Manip zManip = new Translate1Manip();
	private Translate1Manip xManip = new Translate1Manip();
	//private RotateManip xRotate = new RotateManip();


	private static Translate1Manip xAxis = new Translate1Manip();
	private static Translate1Manip yAxis = new Translate1Manip();
	private static Translate1Manip zAxis = new Translate1Manip();
	//private RotateManip yRotate = new RotateManip();
	private Lego selectedLego;
	private CubeBounds bounds;
	private boolean manipsAdded = false;
	private ArrayList<LegoSelectionListener> selectionListeners;
	private static ManipManager manager = ManipManager.getManipManager();
	static {
		paramMap = new HashMap<GLAutoDrawable, CameraData>();


		xAxis.setColor(new Vec3f(0, 0, 1));
		xAxis.setAxis(Vec3f.X_AXIS);
		xAxis.setScale(new Vec3f(AXIS_SCALE, AXIS_SCALE, AXIS_SCALE));
		yAxis.setColor(new Vec3f(1, 0, 0));
		yAxis.setAxis(Vec3f.Y_AXIS);
		yAxis.setScale(new Vec3f(AXIS_SCALE, AXIS_SCALE, AXIS_SCALE));
		zAxis.setColor(new Vec3f(0, 1, 0));
		zAxis.setAxis(Vec3f.Z_AXIS);
		zAxis.setScale(new Vec3f(AXIS_SCALE, AXIS_SCALE, AXIS_SCALE));
		xAxis.setPickable(false);
		yAxis.setPickable(false);
		zAxis.setPickable(false);


	}

	public Scene() {
		selectionListeners = new ArrayList<LegoSelectionListener>();
		legos = new ArrayList<Lego>();
		yManip.setScale(new Vec3f(60, 15, 15));
		yManip.setColor(new Vec3f(1, 0, 0));
		yManip.setAxis(Vec3f.Y_AXIS);
		zManip.setScale(new Vec3f(30, 15, 15));
		zManip.setColor(new Vec3f(0, 1, 0));
		zManip.setAxis(Vec3f.Z_AXIS);
		xManip.setScale(new Vec3f(30, 15, 15));
		xManip.setColor(new Vec3f(0, 0, 1));
		//xManip.setAxis(Vec3f.X_AXIS);
		//xRotate.setScale(new Vec3f(50, 50, 50));
		//xRotate.setAxis(Vec3f.X_AXIS.copy());
		//yRotate.setScale(new Vec3f(50, 50, 50));
		//yRotate.setAxis(Vec3f.Y_AXIS);
		selectedLego = null;
	}
	public void addSelectionListener(LegoSelectionListener l)
	{
		selectionListeners.add(l);
	}
	public void removeSelectionListeners(LegoSelectionListener l)
	{
		selectionListeners.remove(l);
	}
	public void addLego(Lego lego)
	{
		legos.add(lego);

		resolveDependancies(lego);
	}
	public void removeLego(Lego lego)
	{
		legos.remove(lego);
	}
	private void resolveDependancies(Lego lego)
	{
		if(!lego.isReady())
		{
			for(int i = 0; i< legos.size(); i++)
			{
				Lego child = lego.getLegos().get(i);
				if(!child.isReady())
				{
					Lego reference = Lego.getReferenceMap().get(child.getPartName());

					Lego refLego = new Lego(reference);
					lego.getLegos().set(i, refLego);
					lego.setReady(true);
					Shape legoShape = refLego.getShape();
					legoShape.matrixMult(lego.getMatrix());
					lego.getShape().add(legoShape);
				}
			}
			lego.setReady(true);
		}
	}

	public void newLego(Lego lego){

		resolveDependancies(lego);
	}
	public void setCenter(Point3 cent)
	{
		center = cent;
	}
	public void useSetCenter(boolean use){
		useCenter = use;
	}



	private void drawLegos(GL gl, int mode){
		for(Lego l: getLegos()){
			//if(l.hasShape()) l.getShape().draw(gl, mode, l.getID());
			l.render(gl);
		}
	}

	public List<Lego> getLegos(){
		return legos;
	}

	// This method is called each frame, it will update everything and then display it
	public void display(GLAutoDrawable canvas) {
		final GL gl = canvas.getGL();
		CameraData data = paramMap.get(canvas);
		if(selectedLego != null && selectedLego.isVisible())
		{

			if(data.getDisplayY()) manager.showManipInWindow(yManip, canvas);
			if(data.getDisplayZ()) manager.showManipInWindow(zManip, canvas);
			if(data.getDisplayX()) manager.showManipInWindow(xManip, canvas);
			//manager.showManipInWindow(xRotate, canvas);
			//manager.showManipInWindow(yRotate, canvas);

			float x = xManip.getTranslation().x();
			float y = yManip.getTranslation().y();
			float z = zManip.getTranslation().z();

			//float xRot = xRotate.getRotation().get(Vec3f.X_AXIS);
			//float yRot = yRotate.getRotation().get(Vec3f.Y_AXIS);

			bounds = selectedLego.getBounds();

			selectedLego.setPosition(x, y, z);
			//selectedLego.getShape().setYRotation(center, -Math.toDegrees(xRot));
			//selectedLego.getShape().setXRotation(center, Math.toDegrees(yRot));

			float zScale = (float)(bounds.front - bounds.back);
			float yScale = (float)(bounds.top - bounds.bottom);
			float xScale = (float)(bounds.left - bounds.right);


			zManip.setTranslation(new Vec3f(x, y, z));
			zManip.setScale(new Vec3f(zScale, 30, 30));
			yManip.setTranslation(new Vec3f(x, y, z));
			yManip.setScale(new Vec3f(yScale, 30, 30));
			xManip.setTranslation(new Vec3f(x, y, z));
			xManip.setScale(new Vec3f(xScale, 30, 30));
			//xRotate.setTranslation(new Vec3f(x, y, z));
			//yRotate.setTranslation(new Vec3f(x, y, z));
			manipsAdded = true;
		} else if(manipsAdded)
		{
			manager.removeManipFromWindow(yManip, canvas);
			manager.removeManipFromWindow(zManip, canvas);
			manager.removeManipFromWindow(xManip, canvas);
			//manager.removeManipFromWindow(xRotate, canvas);
			//manager.removeManipFromWindow(yRotate, canvas);
		}



		if(!paramMap.containsKey(canvas))
		{
			manager.registerWindow(canvas);
			CameraData data2 = new CameraData(false, new Point3(0, 0, 0), new Rotf());
			ExaminerViewer exv = data.getView();
			exv.viewAll(gl);
			paramMap.put(canvas, data2);
		}


		ExaminerViewer ex = data.getView();
		int width = canvas.getWidth();
		int height = canvas.getHeight();



		CameraParameters params = ex.getCameraParameters();
		Vec3f forward = params.getForwardDirection();
		Vec3f up = params.getUpDirection();
		Vec3f right = up.cross(forward);
		Vec3f axisPosition = params.getPosition().plus(forward.times(2f));
		axisPosition = axisPosition.minus(up.times(0.7f));
		axisPosition = axisPosition.plus(right.times(0.5f));

		if(data.getDisplayX())xAxis.setTranslation(axisPosition);
		if(data.getDisplayY())yAxis.setTranslation(axisPosition);
		if(data.getDisplayZ())zAxis.setTranslation(axisPosition);
		// Clears the screen and makes it ready for drawing


		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glViewport(0, 0, width, height);
		gl.glLoadIdentity();


		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		if(data.isOrthogonal())
		{
			gl.glOrtho(0.0, (double)width, 0.0, (double)height, 1.0, (double)drawDistance);
		} else
		{
			float aspect, theta;
			aspect = (float) width / (float) height;
			if (width >= height)
				theta = 45;
			else
				theta = (float) Math.toDegrees(Math.atan(1/ aspect));

			glu.gluPerspective(theta, h, 1.0, drawDistance);
		}
		ex.update(gl);
		ManipManager.getManipManager().updateCameraParameters(canvas, params);
		ManipManager.getManipManager().render(canvas, gl);
		gl.glMatrixMode(GL.GL_MODELVIEW);


		drawLegos(gl, GL.GL_RENDER);
		//this.drawFloor(gl);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}





	// This is entirely to keep jogl from causing the program to crash.
	// It doesn't actually do anything, but if this line isn't there, jogl kills everything.
	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) { }


	// Initializes the scene
	public void init(GLAutoDrawable canvas) {
		//CameraParameters params = new CameraParameters();
		GL gl = canvas.getGL();
		//gl.glShadeModel(GL.GL_SMOOTH);              // Enable Smooth Shading
		gl.glClearColor(0.6f, 0.6f, 0.6f, 0.5f);    // Black Background
		gl.glClearDepth(1.0f);                      // Depth Buffer Setup
		gl.glEnable(GL.GL_DEPTH_TEST);							// Enables Depth Testing
		gl.glDepthMask(true);
		gl.glDepthFunc(GL.GL_LEQUAL);								// The Type Of Depth Testing To Do
		//gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);	// Really Nice Perspective Calculations
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST );
		//gl.glCullFace(GL.GL_BACK);
		//gl.glEnable(GL.GL_CULL_FACE);


		//float[] lightPosition = new float[] {0, -200, -200, 0};
		//float[] ambient       = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		//float[] diffuse       = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		/*gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT,  ambient, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,  diffuse, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition, 0);

		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);*/
		if(!paramMap.containsKey(canvas))
		{
			ManipManager manager = ManipManager.getManipManager();
			manager.registerWindow(canvas);
			CameraData data = new CameraData(false, Point3.ZERO(), new Rotf());
			ExaminerViewer exv = data.getView();
			exv.attach(canvas, new Translate1BSphereProvider(yManip));
			//exv.viewAll(gl);
			paramMap.put(canvas, data);
		}

		ManipManager manager = ManipManager.getManipManager();
		manager.registerWindow(canvas);

		CameraData data = paramMap.get(canvas);
		ExaminerViewer viewer = data.getView();
		viewer.dollyFaster();
		viewer.dollyFaster();

		viewer.attach(canvas, new Translate1BSphereProvider(yAxis));
		Vec3f position = data.getInitPosition();
		Rotf rotation = data.getInitRotation();
		//viewer.viewAll(gl);

		if(position != null) viewer.setPosition(position);
		if(rotation != null) viewer.setRotation(rotation);

		viewer.addCameraModifer(new MouseTranslate());
		if(!data.isOrthogonal()) viewer.addCameraModifer(new MouseRotate());
		viewer.addCameraModifer(new MouseDolly());



		paramMap.put(canvas, data);
		manager.showManipInWindow(xAxis, canvas);
		manager.showManipInWindow(yAxis, canvas);
		manager.showManipInWindow(zAxis, canvas);
	}

	public void reshape(GLAutoDrawable canvas, int x, int y, int width, int height) {

		final GL gl = canvas.getGL();
		if(!paramMap.containsKey(canvas))
		{
			ManipManager manager = ManipManager.getManipManager();
			manager.registerWindow(canvas);
			CameraData data = new CameraData(false, Point3.ZERO(), new Rotf());
			ExaminerViewer exv = data.getView();
			exv.attach(canvas, new Translate1BSphereProvider(yManip));
			exv.viewAll(gl);
			paramMap.put(canvas, data);
		}

		CameraData data = paramMap.get(canvas);
		ExaminerViewer ex = data.getView();
		CameraParameters cp = ex.getCameraParameters();


		float aspect, theta;
		aspect = (float) width / (float) height;
		if (width >= height)
			theta = 45;
		else
			theta = (float) Math.toDegrees(Math.atan(1/ aspect));
		cp.setVertFOV((float) Math.toRadians(theta) / 2.0f);
		cp.setImagePlaneAspectRatio(aspect);
		cp.setXSize(width);
		cp.setYSize(height);


		if (height <= 0) // avoid a divide by zero error!
		height = 1;
		h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		//http://www.opengl.org/documentation/specs/man_pages/hardcopy/GL/html/glu/perspective.html


		glu.gluPerspective(theta, h, 1.0, drawDistance);
		//glu.gluPerspective(45.0, h, aspect, drawDistance);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}


	public void setSelectedCanvas(GLAutoDrawable canvas)
	{
		selectedCanvas = canvas;
	}
	public GLAutoDrawable getSelectedCanvas()
	{
		return selectedCanvas;
	}
	public void clearScene()
	{
		legos.clear();

	}
	public CameraData getCameraData(GLAutoDrawable canvas)
	{
		return paramMap.get(canvas);
	}
	public void selectLego(Lego l)
	{
		selectedLego = l;
		if(selectedLego != null)
		{
			double x = l.getShape().getSQM().getX();
			double y = l.getShape().getSQM().getY();
			double z = l.getShape().getSQM().getZ();

			yManip.setTranslation(new Vec3f((float)x, (float)y, (float)z));
			xManip.setTranslation(new Vec3f((float)x, (float)y, (float)z));
			zManip.setTranslation(new Vec3f((float)x, (float)y, (float)z));
			bounds = selectedLego.getBounds();

			for(LegoSelectionListener listener: selectionListeners)
			{
				listener.legoSelected(l);
			}
		}

	}

	public void registerCanvas(GLAutoDrawable canvas, CameraData view)
	{
		paramMap.put(canvas, view);
	}
	static class Translate1BSphereProvider implements BSphereProvider {
		private Translate1Manip manip;

		private Translate1BSphereProvider(Translate1Manip manip) {
			this.manip = manip;
		}

		public BSphere getBoundingSphere() {
			BSphere bsph = new BSphere();
			bsph.setCenter(manip.getTranslation());
			Vec3f scale0 = manip.getScale();
			Vec3f scale = new Vec3f();
			scale.setX(2.0f * scale0.x() );
			scale.setY(2.0f * scale0.y());
			scale.setZ(2.0f * scale0.z() );
			bsph.setRadius(scale.length());
			return bsph;
		}
	}
	public void moveSelectedLego(double x, double y, double z)
	{
		if(selectedLego != null)
		{
			selectedLego.getShape().changeX(x);
			selectedLego.getShape().changeY(y);
			selectedLego.getShape().changeZ(z);

			float manipx = (float)x + xManip.getTranslation().x();
			float manipy = (float)y + yManip.getTranslation().y();
			float manipz = (float)z + zManip.getTranslation().z();

			xManip.setTranslation(new Vec3f(manipx, manipy, manipz));
			yManip.setTranslation(new Vec3f(manipx, manipy, manipz));
			zManip.setTranslation(new Vec3f(manipx, manipy, manipz));
		}

	}
	public void rotateSelectedLego(double x, double y, double z)
	{
		if(selectedLego != null)
		{
			selectedLego.getShape().rotate(Shape.XAXIS, center, x);
			selectedLego.getShape().rotate(Shape.YAXIS, center, y);
			selectedLego.getShape().rotate(Shape.ZAXIS, center, z);
		}



	}
	public interface LegoSelectionListener
	{
		void legoSelected(Lego l);
	}
}
