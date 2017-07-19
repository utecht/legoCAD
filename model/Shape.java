package model;


import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.media.opengl.GL;

import model.Primitive;
import model.SquareMatrix;

public class Shape {

	private ArrayList<Primitive> sides;
	public static final int XAXIS = Primitive.XAXIS;
	public static final int YAXIS = Primitive.YAXIS;
	public static final int ZAXIS = Primitive.ZAXIS;
	private SquareMatrix squam;

	private double xRot = 0;
	private double yRot = 0;
	private double zRot = 0;

	public Shape(ArrayList<Primitive> prims, SquareMatrix sqm){
		this.sides = prims;
		squam = sqm;
	}

	public void draw(GL gl){
		for(Primitive p: sides){
			p.draw(gl);
		}
	}

	//ensures that each Primitive in sides becomes the Primitive
	//calculated by calling its matrixMult method on 'matrix'
	public void matrixMult(SquareMatrix matrix){
		for(Primitive p : sides){
			p.matrixMult(matrix);
		}
	}

	//requires !sides.EMPTY()
	//ensures the point return is the center of this shape, as determined by taking the
	//midpoint between the two most extreme points on each axis.
	public Point3 center(){
		//Point3 start = sides.get(0).points.get(0);
		double maxx, minx, maxy, miny, maxz, minz;
		//maxx = minx = start.x();
		//maxy = miny = start.y();
		//maxz = minz = start.z();
		maxx = maxy = maxz = Double.POSITIVE_INFINITY;
		minx = miny = minz = Double.NEGATIVE_INFINITY;

		for(Primitive p : sides){
			for(Point3 point : p.points){
				if(point.x() < minx) minx = point.x();
				if(point.x() > maxx) maxx = point.x();
				if(point.y() < miny) miny = point.y();
				if(point.y() > maxy) maxy = point.y();
				if(point.z() < minz) minz = point.z();
				if(point.z() > maxz) maxz = point.z();
			}
		}
		return new Point3((minx + maxx)/2, (miny + maxy)/2, (minz + maxz)/2);
	}

	public SquareMatrix getSQM(){
		return squam;
	}

	//ensures every primitive in this shape is shifted by 'dist' in the x axis
	public void changeX(double dist){
		for(Primitive p: sides){
			p.moveX(dist);
		}
		squam.moveX(dist);

	}

	//ensures every primitive in this shape is shifted by 'dist' in the y axis
	public void changeY(double dist){
		for(Primitive p: sides){
			p.moveY(dist);
		}
		squam.moveY(dist);
	}

	//ensures every primitive in this shape is shifted by 'dist' in the z axis
	public void changeZ(double dist){
		for(Primitive p: sides){
			p.moveZ(dist);
		}
		squam.moveZ(dist);
	}
	public void setX(double x)
	{
		double dist = squam.getX();
		squam.setX(x);
		for(Primitive p: sides)
		{
			p.moveX(x - dist);
		}

	}
	public void setY(double y)
	{
		double dist = squam.getY();
		squam.setY(y);
		for(Primitive p: sides)
		{
			p.moveY(y - dist);
		}
	}
	public void setZ(double z)
	{
		double dist = squam.getZ();
		squam.setZ(z);
		for(Primitive p: sides)
		{
			p.moveZ(z - dist);
		}
	}

	public void setPosition(double x, double y, double z)
	{
		setZ(z);
		setX(x);
		setY(y);
		//double zPos = squam.getZ();
		//double yPos = squam.getY();
		//double xPos = squam.getZ();

		//squam.setTranslation(x, y, z);
		/*for(Primitive p: sides)
		{
			p.moveX(x - xPos);
			p.moveY(y - yPos);
			p.moveZ(z - zPos);

		}*/
	}
	//requires 'axis' be an int as defined above
	//ensures that every primitive in this shape is rotated by 'degree' degrees around the point 'center'
	//on the axis 'axis'
	public void rotate(int axis, Point3 center, double degree){
		Point3 c = new Point3(squam.getX(), squam.getY(), squam.getZ());
		if(axis == XAXIS) xRot += degree;
		else if(axis == YAXIS) yRot += degree;
		else if(axis == ZAXIS) yRot += degree;
		for(Primitive p: sides){
			p.rotate(axis, c, degree);
		}
	}
	public void setXRotation(Point3 center, double degree)
	{
		rotate(XAXIS, center, degree - xRot);
	}
	public void setYRotation(Point3 center, double degree)
	{
		rotate(YAXIS, center, degree - yRot);
	}
	public void setZRotation(Point3 center, double degree)
	{
		rotate(ZAXIS, center, degree - zRot);
	}

	public ArrayList<Primitive> getPrims(){
		return sides;
	}

	public void add(Shape shape) {
		sides.addAll(shape.getPrims());
	}

	public CubeBounds getBounds()
	{
		double top = Double.POSITIVE_INFINITY; //miny
		double bottom = Double.NEGATIVE_INFINITY; //maxy
		double left = Double.POSITIVE_INFINITY; //minx;
		double right = Double.NEGATIVE_INFINITY; //maxx;
		double front = Double.POSITIVE_INFINITY; //minz;
		double back = Double.NEGATIVE_INFINITY; //maxz;
		for(Primitive p: sides)
		{
			top = Math.min(top, p.minX());
			bottom = Math.max(bottom, p.maxX());
			left = Math.min(left, p.minY());
			right = Math.max(right, p.maxY());
			front = Math.min(front, p.minZ());
			back = Math.max(back, p.maxZ());
		}



		return new CubeBounds(top, bottom, left, right, front, back);
	}
	public static class CubeBounds
	{
		public double top;
		public double bottom;
		public double left;
		public double right;
		public double front;
		public double back;

		public CubeBounds(double top, double bottom, double left, double right, double front, double back)
		{
			this.top = top;
			this.bottom = bottom;
			this.left = left;
			this.right = right;
			this.front = front;
			this.back = back;
		}

	}
}
