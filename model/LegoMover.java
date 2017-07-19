package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

import view.Scene;


public class LegoMover implements ActionListener, KeyListener{
	private static float moveSpeed = 0.8f;

	public static final int MOVE_DELAY = 2;

	private boolean legoMoveUpX, legoMoveDownX, legoMoveUpY, legoMoveDownY, legoMoveUpZ, legoMoveDownZ;
    private boolean legoRotateUpX, legoRotateDownX, legoRotateUpY, legoRotateDownY, legoRotateUpZ, legoRotateDownZ;

    private Set<Character> pressedKeys;
    private Point3 center = Point3.ZERO();
	private Lego lego;

	Timer timer;
	Scene renderer;
	private boolean useCenter;
	public LegoMover(Scene s) {
		pressedKeys = new HashSet<Character>();
		timer = new Timer(MOVE_DELAY, this);
		renderer = s;
	}
	public void setLego(Lego l)
	{
		lego = l;
		renderer.selectLego(lego);
		if(lego != null)
		{
			timer.start();
		} else
		{
			timer.stop();
		}
	}
	public void actionPerformed(ActionEvent e) {
		if(lego == null) timer.stop();
		handleXYZMovement();
		double rotationSpeed = 2.0;//ALSO A HACK. This is the rotation speed; again, do something to get this from the slider.
		Point3 usedPoint = Point3.ZERO();
		if(useCenter){
        	usedPoint = center;
        }else if(lego.hasShape()){
        	usedPoint = lego.getShape().center();
        }
		handleRotation(usedPoint, rotationSpeed);
	}

	private void handleXYZMovement(){
		if(lego != null && lego.hasShape())
		{
			double x = 0;
			double y = 0;
			double z = 0;

			if(legoMoveUpX) x = -moveSpeed;
			if(legoMoveDownX) x = moveSpeed;
			if(legoMoveUpY) y = -moveSpeed;
			if(legoMoveDownY) y = moveSpeed;
			if(legoMoveUpZ) z = -moveSpeed;
			if(legoMoveDownZ) z = moveSpeed;

			renderer.moveSelectedLego(x, y, z);

			/*if(legoMoveUpX) lego.getShape().changeX(-moveSpeed);
	        if(legoMoveDownX) lego.getShape().changeX(moveSpeed);
	        if(legoMoveUpY) lego.getShape().changeY(-moveSpeed);
	        if(legoMoveDownY) lego.getShape().changeY(moveSpeed);
	        if(legoMoveUpZ) lego.getShape().changeZ(-moveSpeed);
	        if(legoMoveDownZ) lego.getShape().changeZ(moveSpeed);
			 */
		}

    }

    private void handleRotation(Point3 center, double rot){
    	if(lego != null && lego.hasShape())
    	{
    		double x = 0;
    		double y = 0;
    		double z = 0;

    		if(legoRotateUpX) x = -rot;
    		if(legoRotateDownX) x = rot;
    		if(legoRotateUpY) y = -rot;
    		if(legoRotateDownY) y = rot;
    		if(legoRotateUpZ) z = -rot;
    		if(legoRotateDownZ) z = rot;

    		/*if(legoRotateDownX) lego.getShape().rotate(Shape.XAXIS, center, rot);
    		if(legoRotateUpX) lego.getShape().rotate(Shape.XAXIS, center, -rot);
    		if(legoRotateDownY) lego.getShape().rotate(Shape.YAXIS, center, rot);
    		if(legoRotateUpY) lego.getShape().rotate(Shape.YAXIS, center, -rot);
    		if(legoRotateDownZ) lego.getShape().rotate(Shape.ZAXIS, center, rot);
    		if(legoRotateUpZ) lego.getShape().rotate(Shape.ZAXIS, center, -rot);*/

    		renderer.rotateSelectedLego(x, y, z);
    	}

    }
	public void keyPressed(KeyEvent e) {
		if(lego != null)
		{
			processKeyEvent(e, true);

			if(pressedKeys.size() == 0) timer.start();
			pressedKeys.add(e.getKeyChar());

		}


	}

	public void keyReleased(KeyEvent e) {
		if(lego != null)
		{
			processKeyEvent(e, false);
			pressedKeys.remove(e);
			if(pressedKeys.size() == 0) timer.stop();
		}

	}
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	private void processKeyEvent(KeyEvent e, boolean pressed)
	{
		if(e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK){
			legoMoveDownY = false;
			legoMoveDownX = false;
			legoMoveDownZ = false;
			legoMoveUpX = false;
			legoMoveUpY = false;
			legoMoveUpZ = false;
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A: legoRotateDownX = pressed; break;
			case KeyEvent.VK_D: legoRotateUpX = pressed; break;
			case KeyEvent.VK_W:	legoRotateUpY = pressed; break;
			case KeyEvent.VK_S: legoRotateDownY = pressed; break;
			case KeyEvent.VK_Q:	legoRotateDownZ = pressed; break;
			case KeyEvent.VK_E:	legoRotateUpZ = pressed; break;
			}
		} else {
			legoRotateDownY = false;
			legoRotateDownX = false;
			legoRotateDownZ = false;
			legoRotateUpX = false;
			legoRotateUpY = false;
			legoRotateUpZ = false;
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A: legoMoveDownX = pressed; break;
			case KeyEvent.VK_D: legoMoveUpX = pressed; break;
			case KeyEvent.VK_W:	legoMoveUpY = pressed; break;
			case KeyEvent.VK_S:	legoMoveDownY = pressed; break;
			case KeyEvent.VK_Q: legoMoveDownZ = pressed; break;
			case KeyEvent.VK_E:	legoMoveUpZ = pressed; break;
			}
		}

	}
}
