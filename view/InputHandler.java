package view;

//this class is currently implemented only for debugging use
//it may provide a basis for end-user controls, but will
//require more development
import gnu.gleem.CameraParameters;
import gnu.gleem.ExaminerViewer;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.Timer;


/* Note that Dr. Ferrer is more interested in intuitive GUI button controls than keyboard controls.
 * For the third incriment, I would focus on cleaing up the button controls instead of fixing these
 * admittedly borked keyboard controls. The final product would ideally have both available, but
 * intuitive images to indicate the functions of the GUI buttons would be a better first investment.
 * --Josh
 */

public class InputHandler extends KeyAdapter implements MouseWheelListener, MouseMotionListener, MouseListener, ActionListener
{
	private static int CAMERA_DELAY = 5;
	private Scene renderer;

	private boolean increaseX;
    private boolean decreaseX;
    private boolean increaseY;
    private boolean decreaseY;

    private Set<Character> pressedKeys;
    private boolean zoomIn, zoomOut;

    private boolean moveLeft, moveRight, moveUp, moveDown;

    private float moveSpeed = 2.0f;
    private float moveDist = 1.0f;


	private boolean ctrlPressed = false;
	private int mouseStartX = 0;
	private int mouseStartY = 0;
	private int mouseEndX = 0;
	private int mouseEndY = 0;
	private boolean mouseDown = false;

	Timer timer;
	public InputHandler(Scene renderer, GLDisplay glDisplay) {
		pressedKeys = new HashSet<Character>();
		this.renderer = renderer;
		timer = new Timer(CAMERA_DELAY, this);
	}

	public void keyPressed(KeyEvent e) {

		if(pressedKeys.isEmpty()) timer.start();
		pressedKeys.add(e.getKeyChar());
		processKeyEvent(e, true);
	}

	//DOES NOT APPEAR TO BE DOING ANYTHING RIGHT NOW
	public void keyReleased(KeyEvent e) {
		pressedKeys.remove(e.getKeyChar());
		if(pressedKeys.isEmpty()) timer.stop();

		processKeyEvent(e, false);
	}

	private void processKeyEvent(KeyEvent e, boolean pressed) {
		ctrlPressed = (e.getModifiers() == KeyEvent.CTRL_MASK);
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_NUMPAD9: moveDown = pressed; break;
		case KeyEvent.VK_NUMPAD3: moveUp = pressed; break;
		case KeyEvent.VK_DOWN: increaseX = pressed; break;
		case KeyEvent.VK_UP: decreaseX = pressed; break;
		case KeyEvent.VK_RIGHT: increaseY = pressed; break;
		case KeyEvent.VK_LEFT: decreaseY = pressed; break;
		case KeyEvent.VK_NUMPAD2: zoomOut = pressed; break;
		case KeyEvent.VK_NUMPAD8: zoomIn = pressed; break;
		case KeyEvent.VK_NUMPAD4: moveRight = pressed; break;
		case KeyEvent.VK_NUMPAD6: moveLeft = pressed; break;
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		/*Camera c = renderer.getSelectedCamera();
		int amount = e.getScrollAmount();
		int type = e.getWheelRotation();
		if(amount > 0) c.moveForward(moveDist*amount*type*10);
		else if(amount < 0) c.moveForward(-moveDist*amount*type*10);

*/
	}

	public void actionPerformed(ActionEvent e)
	{
		/*Camera c = renderer.getSelectedCamera();
		GLAutoDrawable canvas = renderer.getSelectedCanvas();
		double width = canvas.getWidth();
		double height = canvas.getHeight();
		if(increaseX) c.rotateX(-moveSpeed);
    	if(decreaseX) c.rotateX(moveSpeed);
    	if(increaseY) c.rotateY(moveSpeed);
    	if(decreaseY) c.rotateY(-moveSpeed);
    	if(zoomIn) c.moveForward(-moveDist);
    	if(zoomOut) c.moveForward(moveDist);
    	if(moveLeft) c.moveRight(moveDist);
    	if(moveRight) c.moveRight(-moveDist);
    	if(moveDown) c.moveUp(moveDist);
    	if(moveUp) c.moveUp(-moveDist);

    	if(mouseDown && ctrlPressed)
    	{
    		double deltaX = (mouseEndX - mouseStartX) / width;
    		double deltaY = (mouseEndY - mouseStartY) / height;
    		c.rotateY(deltaX * moveSpeed);
    		c.rotateX(-deltaY * moveSpeed);
    	}*/
	}

	public void mouseDragged(MouseEvent e)
	{
		mouseEndX = e.getX();
		mouseEndY = e.getY();
	}

	public void mouseMoved(MouseEvent e){}

	public void mouseClicked(MouseEvent e){}

	public void mouseEntered(MouseEvent e){}

	public void mouseExited(MouseEvent e){}

	public void mousePressed(MouseEvent e)
	{
		mouseDown = true;
		mouseStartX = e.getX();
		mouseStartY = e.getY();
		mouseEndX = mouseStartX;
		mouseEndY = mouseStartY;
	}

	public void mouseReleased(MouseEvent e)
	{
		mouseDown = false;
		mouseStartX = 0;
		mouseStartY = 0;
		mouseEndX = 0;
		mouseEndY = 0;

	}
}
