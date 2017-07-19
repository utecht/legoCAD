package gnu.gleem;

import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;


public interface MouseState
{

	boolean matches(HashSet<Integer> buttons, int modifiers);


	void performModification(MouseEvent e, ExaminerViewer view, int dx, int dy);
}
