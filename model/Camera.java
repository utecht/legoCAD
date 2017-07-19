package model;

import gnu.gleem.linalg.Vec3f;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class Camera
{
	private static final double PI_DIV180 = Math.PI/180.0;
	private Point3 viewDir;
	private Point3 rightVector;
	private Point3 upVector;
	private Point3 position;

	double rotX, rotY, rotZ;


	private boolean lockRotation = false;
	public Camera()
	{
		position = new Point3(0.0, 0.0, 0.0);
		rightVector = new Point3(1.0, 0.0,  0.0);
		upVector    = new Point3(0.0, 1.0,  0.0);
		viewDir     = new Point3(0, 0, -1);

	}

	public void orientCamera(GL gl, GLU glu)
	{
		Point3 viewPoint = position.plus(viewDir);
		glu.gluLookAt(position.x(), position.y(), position.z(), viewPoint.x(), viewPoint.y(), viewPoint.z(), upVector.x(), upVector.y(), upVector.z());

	}




	public void rotateY(double angle)
	{
		if(!lockRotation)
		{
			double rad = Math.sqrt(viewDir.z()*viewDir.z() + viewDir.x()*viewDir.x());
			double curAngle = Math.atan2(viewDir.x(), viewDir.z());
			curAngle += Math.toRadians(angle);

			viewDir.setZ(Math.cos(curAngle)*rad);
			viewDir.setX(Math.sin(curAngle)*rad);

		}

	}



	public void rotateX(double angle)
	{
		if(!lockRotation)
		{
			double rad = Math.sqrt(viewDir.y()*viewDir.y() + viewDir.z()*viewDir.z());
			double curAngle = Math.atan2(viewDir.y(), viewDir.z());
			curAngle += Math.toRadians(angle);

			viewDir.setZ(Math.cos(curAngle)*rad);
			viewDir.setY(Math.sin(curAngle)*rad);


		}



	}

	public void rotateZ(double angle)
	{
		if(!lockRotation)
		{
			double rad = Math.sqrt(viewDir.x()*viewDir.x() + viewDir.y()*viewDir.y());
			double curAngle = Math.atan2(viewDir.y(), viewDir.x());
			curAngle += Math.toRadians(angle);

			viewDir.setX(Math.cos(curAngle)*rad);
			viewDir.setY(Math.sin(curAngle)*rad);

		}



	}

	public void moveForward(double distance)
	{
		viewDir.normalize();

		position = position.plus(viewDir.mult(-distance));
	}

	public void moveRight(double distance)
	{
		position = position.plus(rightVector.mult(-distance));
	}

	public void moveUp(double distance)
	{
		position = position.plus(upVector.mult(-distance));
	}

	public void moveTo(Point3 pos)
	{
		position = pos;
	}
	@Override
	public String toString()
	{
		return viewDir.toString();
	}

	public void setRotationLock(boolean b)
	{
		lockRotation = b;
	}
	public boolean getRotationLock()
	{
		return lockRotation;
	}

	public Point3 getPosition()
	{

		return position;
	}

	public Point3 getViewDirection()
	{
		// TODO Auto-generated method stub
		return viewDir;
	}

}
