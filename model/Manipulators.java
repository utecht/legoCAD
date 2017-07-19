package model;

import javax.media.opengl.GLAutoDrawable;

import gnu.gleem.ManipManager;
import gnu.gleem.Translate2Manip;
import gnu.gleem.linalg.Vec3f;


public class Manipulators
{
	public Manipulators(GLAutoDrawable drawable, Point3 origin, Point3 normal)
	{
		ManipManager manager = ManipManager.getManipManager();
	      manager.registerWindow(drawable);

	      // Instantiate a Translate2Manip
	      Translate2Manip manip = new Translate2Manip();
	      manip.setTranslation(new Vec3f((float)origin.x(), (float)origin.y(), (float)origin.z()));
	      manip.setNormal(new Vec3f((float)normal.x(), (float)normal.y(), (float)normal.z()));
	      manager.showManipInWindow(manip, drawable);
	}
}
