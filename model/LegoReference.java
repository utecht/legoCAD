package model;

import java.awt.Color;

public class LegoReference {
	String partname;
	Color color;
	SquareMatrix matrix;

	public LegoReference(String part, Color c, SquareMatrix m){
		partname=part;
		color=c;
		matrix=m;
	}

	public int hashCode(){
		return partname.hashCode();
	}
	public boolean equals(Object o){
		LegoReference lego;
		if(o instanceof LegoReference){
			lego=(LegoReference)o;
			return partname.equals(lego.partname);
		}
		return false;
	}

	public String getPartName(){
		return partname;
	}
	public Color getColor(){
		return color;
	}
	public SquareMatrix getSquareMatrix(){
		return matrix;
	}

}
