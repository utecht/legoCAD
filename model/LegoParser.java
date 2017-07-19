package model;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class LegoParser {
	private static int step=0;
	private static int ID;
	static boolean firstFile=true;

	public static Lego parse(Lego l) {
		Lego headFile = null;
		File file = l.getFile();
		ID=l.getID();
		ArrayList<Lego> legArray= new ArrayList();
		ArrayList<String> input;
		try {
			input = allLines(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		if(input == null) System.err.println("Problem with library");
		if(input.isEmpty()) System.err.println("Problem with input" + file);
		String nameStr = input.get(0);

		l.setName(nameStr.substring(2));


		int p=0;
		while(p<input.size()){
			String str=input.get(p).trim();
			p++;
			//this next confusing lump of code checks what color we will be using (pulls 2nd value in str (ie the LDRAW color ref) and find it in our colorHash)
			//handles refrences as well (this is the 16, see http://www.ldraw.org/Article218.html#colours for more info)
			String[] strArray=str.split("(\\s)+");
			HashMap<Integer, Color> colors = ColorMap.getColorHash();
			Color passColor = colors.get(16);
			if(!strArray[0].equals("0") && strArray.length==1)
			{
				continue;
			}
			if(!strArray[0].equals("0") && !strArray[1].equals("24")){
				int colorNum = Integer.parseInt(strArray[1]);
				if(colors.containsKey(colorNum)) passColor = colors.get(colorNum);
				else passColor = new Color(255, 0, 255);
				colors.put(16, passColor);
			}

			switch (str.charAt(0)){
			//case 0 is METAs, only one implemented is "~Moved to" (more info on METAs http://www.ldraw.org/Article218.html#meta)
			case('0'):
				if (str.contains("~Moved to"))
				{
					//alters this loop to have a new input and resets the p index to 0 (we change parts (and thus input files) midstream)
					File newFile=(Lego.findFile(str));
					if(newFile==null)break;



					Lego temp =new Lego(newFile, l.getMatrix(), Lego.getPartMap(), l.getID());
					parse(temp);
					return temp;
					//l.addChild(parse(temp));
//					l.file=newFile;
//					try {
//						input=allLines(l.getFile());
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						return null;
//					}
//					p=0;
//					nameStr = input.get(0);
//					l.setName(nameStr.substring(2));
				}
				else if(str.contains("STEP"))
				{
					l.setStep(step);
					step++;
				}
				else if(str.startsWith("0 NOFILE"))
				{
					if(firstFile){
						l=finalizeLego(l, false);
						firstFile=false;
						headFile=l;
					}else{
						l=finalizeLego(l,false);
						l.setPartName(l.getName());
						headFile.addPartReference(l.getPartName().toLowerCase(), l);
					}

				}
				else if (str.startsWith("0 FILE"))
				{
					if(firstFile){
						break;
					}
					l =new Lego(null, SquareMatrix.IdentityMatrix(), Lego.getPartMap(), l.getID());
					String[] spaceDelim = str.split(" ");
					l.setName(spaceDelim[2]);

					step=0;
//					l=finalizeLego(l);
//					legArray.add(l);
//					int tempid=l.getID()+1;
//					Map tempmap=l.getPartMap();
//					l=new Lego(file,SquareMatrix.IdentityMatrix(), tempmap, tempid);
//					l.setName(nameStr.substring(p+2));
				}/* else if(str.startsWith("0 BFC"))
				{
					//String[] spaceDelim = str.split(" ");
					System.out.println(str);
				}*/



			break;
			//case 1 is refrenced parts
			case('1'):
				// create a lego reference and recursivly loads it
				double[] matPoints = new double[SquareMatrix.MATRIX_SIZE];

				//sets up matrix for our temp refed lego
				for(int i = 0; i < matPoints.length; i++){
					matPoints[i] = Double.parseDouble(strArray[i + 2]);
				}

				File ldFile = Lego.findFile(strArray[strArray.length-1]);
				if(ldFile==null){
					Lego temp =new Lego(null, new SquareMatrix(matPoints), Lego.getPartMap(), l.getID());
					temp.setPartName(l.fileName(strArray[strArray.length-1]));

					l.addChild(temp);
					break;
				}

				Lego temp =new Lego(ldFile, new SquareMatrix(matPoints), Lego.getPartMap(), l.getID());
				//temp.setReady(true);
				//l.addChild(parse(temp)[0]);
				Lego child = parse(temp);
				if(child !=null)l.addChild(child);


			break;
			case('2'):
				// make line prim
				l.addPrimitive(Line.makeLine(str));
			break;
			case('3'):
				// make triangle prim
				l.addPrimitive(Triangle.makeTriangle(str, passColor));
			break;
			case('4'):
				// make quad prim
				l.addPrimitive(Quad.makeQuad(str, passColor));
			break;
			case('5'):
				// make optional line thingy
				break;
			default:
				// Print this to the screen somewhere
				System.err.println("LDRAW format error");
			}
		}

		/*// add the parsed primitives into the shape
		l.getShape().add(new Shape(l.getPrimitives(), l.getMatrix()));
		// perform the matrix operation on the shape
		l.getShape().matrixMult(l.getMatrix());
		// adds each shape from the lower legos to the top level after running it through the matrix
		for(Lego ls: l.getChildren()){
			ls.getShape().matrixMult(l.getMatrix());
			l.getShape().add(ls.getShape());
		}
		// sets the lego to ready so it can be rendered
		l.setReady(true);*/
		if (firstFile){
			l=finalizeLego(l, true);
			headFile=l;
		}
		if(headFile == null) {
			l = finalizeLego(l, true);
			return l;
		}
		return headFile;
	}

	private static Lego finalizeLego(Lego l, boolean isReady){
        //add the parsed primitives into the shape
		l.getShape().add(new Shape(l.getPrimitives(), l.getMatrix()));
		// perform the matrix operation on the shape
		l.getShape().matrixMult(l.getMatrix());
		// adds each shape from the lower legos to the top level after running it through the matrix
		for(Lego ls: l.getChildren()){
			ls.getShape().matrixMult(l.getMatrix());
			l.getShape().add(ls.getShape());
		}
		// sets the lego to ready so it can be rendered
		l.setReady(isReady);
		return l;
	}

	private static ArrayList<String> allLines(File file) throws FileNotFoundException
	{
		//if(file == null) return null;

		ArrayList<String> ret = new ArrayList<String>();
		Scanner in = new Scanner(file);
		String line;
		while(in.hasNextLine()){
			line = in.nextLine();
			if(line.length() > 0){
				//WE WANT 0s RARARAR	if(line.charAt(0) != '0')
					ret.add(line);
			}
		}
		in.close();
		return ret;

	}

}
