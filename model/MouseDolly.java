package model;

import gnu.gleem.ExaminerViewer;
import gnu.gleem.MouseState;
import gnu.gleem.linalg.Vec3f;

import java.awt.event.MouseEvent;
import java.util.HashSet;


public class MouseDolly implements MouseState
{

	private static final int CTRLALT = (MouseEvent.CTRL_MASK | MouseEvent.ALT_MASK);

	public boolean matches(HashSet<Integer> buttons, int modifiers)
	{

		String mods = MouseEvent.getMouseModifiersText(modifiers);
		return mods.startsWith("Alt") && (buttons.size() == 1) && buttons.contains(MouseEvent.BUTTON1);
	}

	public void performModification(MouseEvent e, ExaminerViewer view, int dx, int dy)
	{
		Vec3f localDiff = new Vec3f(0, 0, view.getDollySpeed() * -1.0f * dy / 100.0f);
		// Rotate this by camera's orientation
		Vec3f worldDiff = view.getRotation().rotateVector(localDiff);
		Vec3f position = view.getPosition();
		position.add(worldDiff);
		// Subtract off the dolly amount from the focal distance
		float focalDiff = (float) worldDiff.length();
		if (dy > 0)
			focalDiff *= -1.0f;
		float focalDist = view.getFocalDist();
		focalDist += focalDiff;
		if (focalDist < view.getMinFocalDist())
			focalDist = view.getMinFocalDist();
	}
}
