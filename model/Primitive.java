package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

public abstract class Primitive {
	protected ArrayList<Point3> points;
	protected Color color;
	public final static int XAXIS = Point3.XAXIS;
	public final static int YAXIS = Point3.YAXIS;
	public final static int ZAXIS = Point3.ZAXIS;

	public Primitive(Color color, ArrayList<Point3> points){
		this.points = (ArrayList<Point3>) points.clone();
		this.color = color;

	}

	public Color getColor(){
		return color;
	}

	public void setColor(Color newColor){
		color = newColor;
	}

	public ArrayList<Point3> getPoints(){
		return points;
	}



	public abstract void draw(GL gl);


	public final void drawPoints(GL gl)
	{
		gl.glColor3f((float)color.getRed() / 255, (float)color.getGreen() / 255, (float)color.getBlue() / 255);

		for(int i = 0; i < points.size(); i++)
		{
			Point3 p = points.get(i);
			gl.glVertex3f((float)p.x(), (float)p.y(), (float)p.z());
		}
		gl.glEnd();
	}

	//ensures that each point in points is changed to the point calculated by
	//multiplying it by 'matrix'
	public void matrixMult(SquareMatrix matrix){
		for(int i = 0; i < points.size(); i++){
			points.set(i, matrix.multiplyPoint(points.get(i)));
		}
	}

	//ensures that every point in this primitive is shifted in the x axis by 'dist'
	public void moveX(double dist){
		for(Point3 point : points) point.setX(point.x() + dist);
	}
	public void setX(double x)
	{
		for(Point3 point: points) point.setX(x);
	}
	public void setY(double y)
	{
		for(Point3 point: points) point.setY(y);
	}
	public void setZ(double z)
	{
		for(Point3 point: points) point.setZ(z);
	}
	public void setXYZ(double x, double y, double z)
	{
		for(Point3 point: points)
		{
			point.setX(x);
			point.setY(y);
			point.setZ(z);
		}
	}
	//ensures that every point in this primitive is shifted in the y axis by 'dist'
	public void moveY(double dist){
		for(Point3 point : points) point.setY(point.y() + dist);
	}

	//ensures that every point in this primitive is shifted in the z axis by 'dist'
	public void moveZ(double dist){
		for(Point3 point : points) point.setZ(point.z() + dist);
	}

	//requires !points.empty()
	//ensures each point in points becomes rotated about the
	//	indicated axis at the point center by degree degrees.
	public void rotate(int axis, Point3 center, double degree){
		for(Point3 point : points){
			point.translate(center.x(), center.y(), center.z());
			point.rotate(axis, degree);
			point.translate(-center.x(), -center.y(), -center.z());
		}
	}

	public String toString(){
		String ret = "";
		for(Point3 p: points){
			ret += p.toString() + " ";
		}
		return ret;
	}

	public double minX()
	{
		double x = Double.POSITIVE_INFINITY;
		for(Point3 p: points)
		{
			x = Math.min(x, p.x());
		}

		return x;
	}

	public double maxX()
	{
		double x = Double.NEGATIVE_INFINITY;
		for(Point3 p: points)
		{
			x = Math.max(x, p.x());
		}

		return x;
	}

	public double minY()
	{
		double y = Double.POSITIVE_INFINITY;
		for(Point3 p: points)
		{
			y = Math.min(y, p.y());
		}

		return y;
	}
	public double maxY()
	{
		double y = Double.NEGATIVE_INFINITY;
		for(Point3 p: points)
		{
			y = Math.max(y, p.y());
		}

		return y;
	}
	public double minZ()
	{
		double z = Double.POSITIVE_INFINITY;
		for(Point3 p: points)
		{
			z = Math.min(z, p.z());
		}

		return z;
	}
	public double maxZ()
	{
		double z = Double.NEGATIVE_INFINITY;
		for(Point3 p: points)
		{
			z = Math.max(z, p.z());
		}

		return z;
	}
}
