package model;

public class SquareMatrix {
	private double[][] matrix;
	private double determinant;
	public static int MATRIX_SIZE = 12;

	//requires exactly 12 values in the order
	//x,y,z,a,b,c,d,e,f,g,h,i
	public SquareMatrix(double[] values){
		matrix = new double[4][4];
		for(int i = 0; i < 3; i++) matrix[i][3] = values[i];
		int k = 3;
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				matrix[i][j] = values[k];
				k++;
			}
		}
		for(int i = 0; i < 3; i++) matrix[3][i] = 0;
		matrix[3][3] = 1;
		calculateDeterminant();
		//unScale();
	}
	public SquareMatrix mult(SquareMatrix s)
	{
		double[][] mat = new double[4][4];

		for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                for (int k = 0; k < 4; k++)
                    mat[i][j] += this.matrix[i][k] * s.matrix[k][j];


		SquareMatrix sm = SquareMatrix.IdentityMatrix();
		sm.matrix = mat;

		return sm;
	}
	//requires that the SquareMatrix be created from a proper LDraw file
	//returns the determinant of the 3x3 matrix that represents the orientation and scaling
	//    of a Lego loaded from a proper LDraw file
	public void calculateDeterminant(){
		double mid = (matrix[1][1] * matrix[2][2]) - (matrix[1][2] * matrix[2][1]);
		double right = (matrix[1][2] * matrix[2][0]) - (matrix[1][0] * matrix[2][2]);
		double left = (matrix[1][0] * matrix[2][1]) - (matrix[1][1] * matrix[2][0]);

		double one = matrix[0][0] * mid;
		double two = matrix[0][1] * right;
		double three = matrix[0][2] * left;

		determinant = one - two + three;
	}

	public void printCoords(){
		System.out.println("X: " + matrix[0][3] + " Y: " + matrix[1][3] + " Z: " + matrix[2][3]);
	}

	//removes the scaling of the matrix
	//Should only be called when building a lego
	public void unScale(){
		if(determinant != 0){
			for(int i = 0; i <= matrix.length; i ++){
				int first = i / 4;
				int second = i % 4;
				matrix[first][second] = matrix[first][second] / Math.pow(determinant, (1.0/3.0));
			}
		}
	}

	public double getDeterminant(){return determinant;}

	//Treats this point as if it were a 4x1 with matrix with the value of 1,4 being zero. It ensures that the
	//return value is the first three values of the resulting 4x1 matrix after multiplying the point by this
	//matrix
	public Point3 multiplyPoint(Point3 point){
		double x = (matrix[0][0] * point.x() + matrix[0][1] * point.y() + matrix[0][2] * point.z()) + matrix[0][3];
		double y = (matrix[1][0] * point.x() + matrix[1][1] * point.y() + matrix[1][2] * point.z()) + matrix[1][3];
		double z = (matrix[2][0] * point.x() + matrix[2][1] * point.y() + matrix[2][2] * point.z()) + matrix[2][3];
		return new Point3(x, y, z);
	}

	// Returns an identity matrix
	public static SquareMatrix IdentityMatrix(){
		return new SquareMatrix(new double[]{0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1});
	}

	public String toString(){
		return new String("[" + matrix[0][0] + " " + matrix[0][1] + ' ' + matrix[0][2] + ' ' + matrix[0][3] + '\n' +
								matrix[1][0] + ' ' + matrix[1][1] + ' ' + matrix[1][2] + ' ' + matrix[1][3] + '\n' +
								matrix[2][0] + ' ' + matrix[2][1] + ' ' + matrix[2][2] + ' ' + matrix[2][3] + '\n' +
								matrix[3][0] + ' ' + matrix[3][1] + ' ' + matrix[3][2] + ' ' + matrix[3][3] + "]");
	}

	public String printToSave(){
		return (int)matrix[0][3] + " " + (int)matrix[1][3] + " " + (int)matrix[2][3] +
				" " + (int)matrix[0][0] + " " + (int)matrix[0][1] + " " + (int)matrix[0][2] +
				" " + (int)matrix[1][0] + " " + (int)matrix[1][1] + " " + (int)matrix[1][2] +
				" " + (int)matrix[2][0] + " " + (int)matrix[2][1] + " " + (int)matrix[2][2];
	}

	public void moveX(double dist) {
		matrix[0][3] += dist;
	}
	public double getX()
	{
		return matrix[0][3];
	}
	public void setX(double x)
	{
		matrix[0][3] = x;
	}
	public void moveY(double dist) {
		matrix[1][3] += dist;
	}
	public double getY()
	{
		return matrix[1][3];
	}
	public void setY(double y)
	{
		matrix[1][3] = y;
	}
	public void moveZ(double dist) {
		matrix[2][3] += dist;
	}
	public double getZ()
	{
		return matrix[2][3];
	}
	public void setZ(double z)
	{
		matrix[2][3] = z;
	}
	public void setTranslation(double x, double y, double z)
	{
		setX(x);
		setY(y);
		setZ(z);
	}

}
