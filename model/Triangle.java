package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.media.opengl.GL;

public class Triangle extends Primitive{

	public Triangle(Color color, ArrayList<Point3> points){
		super(color, points);
	}
	/*
	public static Triangle makeTriangle(String str, Color passColor){
    	ArrayList<Point3> points = new ArrayList<Point3>();

    	String strarray[];
    	strarray = str.split("\\s");

    	for(int i = 2; i < strarray.length;){
    		double valx = Double.parseDouble(strarray[i]);
    		i++;
    		double valy = Double.parseDouble(strarray[i]);
    		i++;
    		double valz = Double.parseDouble(strarray[i]);
    		i++;
    		Point3 p3 = new Point3(valx,valy,valz);
    		points.add(p3);
    	}
    	return new Triangle(passColor, points);
	}*/

	//requires: str has exactly 11 tokens with space delimiter
	//ensures: this.getPoints.size() == 3
	public static Triangle makeTriangle(String str, Color passColor){
		ArrayList<Point3> points = new ArrayList<Point3>();

    	StringTokenizer stTokenizer = new StringTokenizer(str);
    	stTokenizer.nextToken();//type - ignored
    	stTokenizer.nextToken();//color - ignored

    	for(int i=0; i<3; i++){
	    	double x = Double.parseDouble(stTokenizer.nextToken());
	    	double y = Double.parseDouble(stTokenizer.nextToken());
	    	double z = Double.parseDouble(stTokenizer.nextToken());
	    	Point3 point = new Point3(x,y,z);
	    	points.add(point);
    	}

    	return new Triangle(passColor, points);
	}

	@Override
	public void draw(GL gl) {
		gl.glBegin(GL.GL_TRIANGLES);
		this.drawPoints(gl);
		gl.glEnd();
	}
}
