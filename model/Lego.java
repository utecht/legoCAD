package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;

import model.Primitive;
import model.Shape;
import model.SquareMatrix;
import model.Shape.CubeBounds;


public class Lego {
	private ArrayList<Lego> legos;		// All of the lower level legos in the recursive tree
	private ArrayList<Primitive> prims; // All of the primitives at this level
	private SquareMatrix matrix;			// The SquareMatrix that defines it's position
	private Shape shape;				// The shape for transforming and rendering
	public File file;					// The file this lego was loaded from
	private static Map<String, String> mapNameToFile;	// The map of partName->filePathofPart, good for loading other legos
	private boolean ready;		// indicates that it is fully loaded and ready to render
	private int ID;
	private int step;
	private static ColorMap colors;
	private String name;
	private static Map<String, Lego> mapReferenceToPiece;//the map of Partname->A reference with lego's name, position, and color.
	private ArrayList<String> list;
	private String partName;
	private boolean visible = true;

	//we only need this once for all eternity
	static{
		colors = new ColorMap();
		mapReferenceToPiece=new HashMap<String, Lego>();
	}

	// A lego is all the data needed for rendering, saving and loading a lego from the LDRAW format
	// Upon construction a Lego is nothing more than a reference to the file that needs to be loaded
	// This will be called when a lego is selected, either from the interface or recursively by another lego
	public Lego(File file, SquareMatrix m, Map<String, String> map, int IDin){
		this.file = file;
		this.matrix = m;
		this.mapNameToFile = map;
		prims = new ArrayList<Primitive>();
		legos = new ArrayList<Lego>();
		shape = new Shape(new ArrayList<Primitive>(), m);
		//movers = new
		list= new ArrayList<String>();
		ready = false;
		ID = IDin;
	}
	public Lego(Lego l)
	{
		this.file = l.file;
		this.matrix = l.matrix;
		this.name = l.name;
		this.partName = l.partName;
		this.mapNameToFile = l.mapNameToFile;
		this.prims = l.prims;
		this.legos = new ArrayList<Lego>();

		shape = l.shape;

		for(Lego child: l.legos)
		{
			if(!child.ready)
			{

				Lego mappedLego = mapReferenceToPiece.get(child.partName);
				Lego lego = new Lego(mappedLego);
				lego.name = mappedLego.name;
				lego.shape.matrixMult(matrix);
				addChild(lego);
				shape.add(lego.getShape());

			} else
			{

				Lego legoChild = new Lego(child);

				addChild(legoChild);
			}
		}

		//shape.matrixMult(matrix);
		ready = false;
		ID = l.ID;
	}
	public static Map<String, Lego> getReferenceMap()
	{
		return mapReferenceToPiece;
	}
	public void render(GL gl)
	{
		if(visible)
		{
			shape.draw(gl);
			//for(Lego child: legos)
			//{
				//child.render(gl);
			//}
		}

	}
	public void setPosition(double x, double y, double z)
	{
		shape.setPosition(x, y, z);
		//for(Lego child: legos)
		//{
			//child.setPosition(x, y, z);

		//}
	}

	//here: should only be called in the event of a moved file or refrenced file
	//req: a line from an LDRAW file wherein a refrence or move occurs, will parse out the last section of the line (****.dat)
	//ens: it (this last section) has ".dat" on the end, then return a file based on the filepath the .dat is keyed to
	public static File findFile(String line){

		line=fileName(line);
		if(mapNameToFile.containsKey(line)){
			return new File(mapNameToFile.get(line));
		}else {
			// This should be replaced with a on-screen warning that a fake reference was made
			//System.err.println(line + " not found");
			return null;
		}
	}

	public static String fileName(String line){
		line = line.toLowerCase();

		String[] datExtract = (line.split("(\\s)+"));
		line = datExtract[datExtract.length-1];
		if (!line.contains("."))line+=".dat";
		if (line.contains("\\"))
		{
			String [] slashExtract=line.split("\\\\");
			line=slashExtract[slashExtract.length-1];
		}
		return line;
	}



	// requires hasShape()
	public Shape getShape(){
		return shape;
	}

	//requires hasLegos
	public ArrayList<Lego> getLegos(){
		return legos;
	}

	public boolean hasShape() {
		return ready;
	}
	public void addPartReference(String LR, Lego l){
		mapReferenceToPiece.put(LR, l);
	}

	public boolean hasLegos(){
		return legos != null;
	}

	public void setShape(boolean isSet){
		ready = isSet;
	}

	public int getID(){return ID;}

	public SquareMatrix getMatrix(){
		return shape.getSQM();
	}

	public File getFile(){
		return file;
	}
	public String getPartName()
	{
		return partName;
	}
	public void setPartName(String pn){
		partName=pn;
	}
	@Override
	public String toString()
	{

		return name;
	}
	public void setName(String n)
	{
		name = n;
	}
	public String getName(){
		return name;
	}
	public void setVisible(boolean b)
	{
		visible = b;
	}
	public boolean isVisible()
	{
		return visible;
	}
	public static Map<String,String> getPartMap()
	{
		return mapNameToFile;
	}
	public void addChild(Lego l)
	{
		legos.add(l);

	}
	public void addPrimitive(Primitive p)
	{
		prims.add(p);
	}
	public ArrayList<Primitive> getPrimitives()
	{
		return prims;
	}
	public ArrayList<Lego> getChildren()
	{
		return legos;
	}
	public void setReady(boolean b)
	{
		ready = b;
	}
	public boolean isReady()
	{
		return ready;
	}
	public void setStep(int s){
		step=s;
	}
	public CubeBounds getBounds()
	{
		CubeBounds bounds = shape.getBounds();
		/*for(Lego child: legos)
		{
			CubeBounds childBounds = child.shape.getBounds();
			bounds.top = Math.min(bounds.top, childBounds.top);
			bounds.bottom = Math.max(bounds.bottom, childBounds.bottom);
			bounds.left = Math.min(bounds.left, childBounds.left);
			bounds.right = Math.max(bounds.right, childBounds.right);
			bounds.front = Math.min(bounds.front, childBounds.front);
			bounds.back = Math.max(bounds.back, childBounds.back);
		}*/


		return shape.getBounds();
	}
	public int getMaxStep()
	{
		int cur = Integer.MIN_VALUE;
		for(Lego child: legos)
		{
			cur = Math.min(cur, child.step);
		}
		return cur;
	}



}