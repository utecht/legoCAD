package model;

import gnu.gleem.ExaminerViewer;
import gnu.gleem.MouseState;
import gnu.gleem.linalg.Rotf;
import gnu.gleem.linalg.Vec3f;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashSet;


public class MouseTranslate implements MouseState
{
	public boolean matches(HashSet<Integer> buttons, int modifiers)
	{
		String mods = MouseEvent.getMouseModifiersText(modifiers);
		return mods.startsWith("Ctrl") && (buttons.size() == 1) && buttons.contains(MouseEvent.BUTTON1);
	}

	public void performModification(MouseEvent e, ExaminerViewer view, int dx, int dy)
	{
		float dollySpeed = view.getDollySpeed();


//		 Translate functionality
        // Compute the local coordinate system's difference vector
        Vec3f localDiff = new Vec3f(dollySpeed * -1.0f * dx / 100.0f,
                                    dollySpeed * dy / 100.0f,
                                    0.0f);

        // Rotate this by camera's orientation
        Vec3f position = view.getPosition();
        Rotf orientation = view.getRotation();
        Vec3f worldDiff = orientation.rotateVector(localDiff);
        // Add on to position
        position.add(worldDiff);

        //view.setPosition(position);

	}

}
