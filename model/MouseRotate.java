package model;

import gnu.gleem.ExaminerViewer;
import gnu.gleem.MouseState;
import gnu.gleem.linalg.Rotf;
import gnu.gleem.linalg.Vec3f;

import java.awt.event.MouseEvent;
import java.util.HashSet;


public class MouseRotate implements MouseState
{

	public boolean matches(HashSet<Integer> buttons, int modifiers)
	{
		String mods = MouseEvent.getMouseModifiersText(modifiers);
		return mods.startsWith("Meta+Ctrl") && (buttons.size() == 1) && buttons.contains(MouseEvent.BUTTON3);
	}

	public void performModification(MouseEvent e, ExaminerViewer view, int dx, int dy)
	{
		float xRads = (float) Math.PI * -1.0f * dy * view.getRotateSpeed() / 1000.0f;
		float yRads = (float) Math.PI * -1.0f * dx * view.getRotateSpeed() / 1000.0f;
		Vec3f fw = new Vec3f(0, 0, -1);
		Vec3f diff = new Vec3f(view.getRotation().rotateVector(fw));
		diff.normalize();
		diff.scale(view.getFocalDist());
		// Update by this rotation
		Rotf xRot = new Rotf(Vec3f.X_AXIS, xRads);
		Rotf yRot = new Rotf(Vec3f.Y_AXIS, yRads);
		Rotf newRot = yRot.times(xRot);
		Rotf orientation = view.getRotation().times(newRot);
		// Now update the camera's position. Rotate about the focal point.
		Vec3f backOut = orientation.rotateVector(fw);
		backOut.normalize();
		backOut.scale(-1.0f * view.getFocalDist());
		Vec3f position = view.getPosition().plus(backOut).plus(diff);

		view.setPosition(position);
		view.setRotation(orientation);
	}

}
