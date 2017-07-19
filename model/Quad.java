package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.media.opengl.GL;

public class Quad extends Primitive{
	ColorMap colorHash;

	public Quad(Color color, ArrayList<Point3> points){
		super(color, points);
	}
	/*
	public static Quad makeQuad(String str, Color passColor){
    	ArrayList<Point3> points = new ArrayList<Point3>();
    	String strarray[];
    	strarray = str.split("\\s");

    	for(int i = 2; i < strarray.length - 1;){
    		double valx = Double.parseDouble(strarray[i]);
    		i++;
    		double valy = Double.parseDouble(strarray[i]);
    		i++;
    		double valz = Double.parseDouble(strarray[i]);
    		i++;
    		Point3 p3 = new Point3(valx,valy,valz);
    		points.add(p3);
    	}
    	return new Quad(passColor, points);
	}*/

	//requires: str has exactly 11 tokens with space delimiter
	//ensures: this.getPoints.size() == 3
	public static Quad makeQuad(String str, Color passColor){
		ArrayList<Point3> points = new ArrayList<Point3>();

    	StringTokenizer stTokenizer = new StringTokenizer(str);
    	stTokenizer.nextToken();//type - ignored
    	stTokenizer.nextToken();//color - ignored

    	for(int i=0; i<4; i++){
	    	double x = Double.parseDouble(stTokenizer.nextToken());
	    	double y = Double.parseDouble(stTokenizer.nextToken());
	    	double z = Double.parseDouble(stTokenizer.nextToken());
	    	Point3 point = new Point3(x,y,z);
	    	points.add(point);
    	}

    	return new Quad(passColor, points);
	}

	@Override
	public void draw(GL gl) {
		gl.glBegin(GL.GL_QUADS);
		drawPoints(gl);
		gl.glEnd();
	}
}
