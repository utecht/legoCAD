package model;

public class Point3 {
	private double x, y, z;
	public static final int XAXIS = 0;
	public static final int YAXIS = 1;
	public static final int ZAXIS = 2;

	public Point3(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double x(){
		return x;
	}

	public double y(){
		return y;
	}

	public double z(){
		return z;
	}

	public void setX(double x){
		this.x = x;
	}

	public void setY(double y){
		this.y = y;
	}

	public void setZ(double z){
		this.z = z;
	}

	//ensures that this point moves by the values x, y, and z
	public void translate(double x, double y, double z){
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}

	//requires axis be a valid axis as defined in this class
	//ensures that this point is rotated by 'degree' degrees around the origin on the 'axis' axis
	public void rotate(int axis, double degrees){
		double l1 = 0, l2 = 0;
		switch(axis){
		case(XAXIS):
			l1 = y;
			l2 = z;
			break;
		case(YAXIS):
			l1 = z;
			l2 = x;
			break;
		case(ZAXIS):
			l1 = x;
			l2 = y;
			break;
		}
		double len = Math.sqrt(Math.pow(l1, 2) + Math.pow(l2, 2));
		double rot = Math.atan2(l1, l2);
		rot += Math.toRadians(degrees);
		l2 = (Math.cos(rot) * len);
		l1 = (Math.sin(rot) * len);
		switch(axis){
		case(XAXIS):
			setY(l1);
			setZ(l2);
			break;
		case(YAXIS):
			setZ(l1);
			setX(l2);
			break;
		case(ZAXIS):
			setX(l1);
			setY(l2);
			break;
		}

	}

	public String toString(){
		double tolerance = 0.00000001;

		double xn = x;
		double yn = y;
		double zn = z;
		if(Math.abs(xn) < tolerance) xn = 0;
		if(Math.abs(yn) < tolerance) yn = 0;
		if(Math.abs(zn) < tolerance) zn = 0;

		return xn + " " + yn + " " + zn;
	}

	// Returns a zeroed out point
	public static Point3 ZERO(){
		return new Point3(0, 0, 0);
	}

	public Point3 mult(double d)
	{

		return new Point3(this.x*d, this.y*d, this.z*d);
	}

	public Point3 plus(Point3 that)
	{
		return new Point3(this.x+that.x, this.y+that.y, this.z+that.z);
	}
	public Point3 cross(Point3 v)
	{
		double x = this.y()*v.z() - this.z()*v.y();
		double y = this.z()*v.x() - this.x()*v.z();
		double z = this.x()*v.y() - this.y()*v.x();
		return new Point3(x, y, z);
	}
	public Point3 normalize()
	{

		double len = this.getLength();
		if(len == 0) return Point3.ZERO();

		double x = this.x / len;
		double y = this.y / len;
		double z = this.z / len;

		return new Point3(x, y, z);
	}
	public Point3 minus(Point3 that)
	{
		return this.plus(that.mult(-1));
	}
	public double getLength()
	{
		return Math.sqrt(x*x + y*y + z*z);
	}
}
