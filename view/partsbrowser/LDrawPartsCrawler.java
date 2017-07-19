package view.partsbrowser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.corba.se.impl.ior.ByteBuffer;


//invariant: categorySetMap is not null
public class LDrawPartsCrawler {
	//categorySetMap contains Sets as children
	//each child set represents the different category of ldraw parts according the description of the part
	HashMap<String, List<LDrawFileInfo>> categorySetMap = new HashMap<String, List<LDrawFileInfo>>();

	//map to dat file to path sting
	HashMap<String,String> datfileMap =new HashMap<String,String>();

	//stores the path to the LDraw library root
	File baseDir;

	//pattern for searching dighits: used for checking keywords is meanigful or just digits
	Pattern digitPattern = Pattern.compile("[0-9]");

	//used to transform some keyword such as ~wheel to wheel
	static HashSet<String> dirtyPrefixSet = new HashSet<String>();
	static{
		dirtyPrefixSet.add("~");
		dirtyPrefixSet.add("_");
	}

	//constructor
	//requires: baseDir is not null
	public LDrawPartsCrawler(File baseDir){
		this.baseDir = baseDir;
	}

	//create a set that contains LDrawFileInfo for specific categories
	//requires: keyword is not null
	//ensures: categorySetMap contains the pair <keyword, HashSet associated with keyword>
	public List<LDrawFileInfo> createList(String keyword){

		keyword = cleanKeyword(keyword);

		List<LDrawFileInfo> partList = new ArrayList<LDrawFileInfo>();
		categorySetMap.put(keyword, partList);

		//System.out.println("Set is created");
		return partList;
	}

	//crawl into LDraw base directory and basically map filename->category and filename->filepath
	//require: baseDir != null
	//ensures: categorySetMap is populated with Sets and each set will have at least one child
	public void crawlLDrawRoot(){
		findAllPartsUnderThisDirectory(baseDir,true);
    }

	//requires: curDirectory is not null.
	//			it does not contain *.dat/*.ldr which are not in ldraw format
	//ensures: categorySetMap is filled with parts' information
	private void findAllPartsUnderThisDirectory(File curDirectory, boolean isLDrawRoot){
		//The list of files can also be retrieved as File objects
	    File[] files = curDirectory.listFiles();
		if (files == null) {
	        // Either dir does not exist or is not a directory
	    } else {
	        for (int i=0; i<files.length; i++) {

	        	if(files[i].isDirectory()){
	        		findAllPartsUnderThisDirectory(files[i],false);
	        	}else{
	        		// Get filename
	        		String filename = files[i].getName();
	        		String filenameLowerCase = filename.toLowerCase();
	        		if(filenameLowerCase.endsWith(".dat")||(filenameLowerCase.endsWith("*.ldr"))){
	        			processFileContent(files[i]);
	        			datfileMap.put(filename, files[i].getAbsolutePath());
	        		}else{
	        			//if(!isLDrawRoot) return;
	        		}
	        	}
	        }
	    }

	}

	//grab the first line of the file to find the part's description
	//require: file is not null and file is in LDraw format and its first line is not empty
	//			and its first line starts with 0 command
	//ensures: corresponding file name and file path are mapped in categorySetMap
	//			keyword associated with file description is in categorySetMap's keys
	//			keyword will be stored as categorySetMap in lowercase
	//			it will be trimmed if it has ~ or _ characters as prefix
	public void processFileContent(File file){
		try {
			FileInputStream fstream;
			fstream = new FileInputStream(file);
			// Get the object of DataInputStream
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));

		    //store the part of the first line of ldraw file
		    String strLine;

		    //read the very first line
		    if ((strLine = br.readLine()) != null) {
		    	//use the space as the delimiter by default
		    	StringTokenizer st = new StringTokenizer(strLine);

		    	//the second token and after is where the information is
		    	int interestedTokenNum = 2;
		    	if(st.countTokens()>interestedTokenNum){
		    		String firstToken = st.nextToken();
		    		String remainerStr = strLine.substring(firstToken.length()).trim();

	    			//usually describe what kind of part is. eg. wheel, motor, etc
		    		String keyword = st.nextToken().toLowerCase();

		    		keyword = cleanKeyword(keyword);
		    	    Matcher m = digitPattern.matcher(keyword);

		    		if(m.find()){
		    			keyword = st.nextToken().toLowerCase();
		    		}

		    		List<LDrawFileInfo> partList = categorySetMap.get(keyword);
					if(partList==null){
						partList = createList(keyword);
		    		}

					partList.add(new LDrawFileInfo(file, remainerStr));

		    	}
		    }
		    //Close the input stream
		    in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//clean keyword such as ~part or _part to part
	public String cleanKeyword(String keyword){
		if(dirtyPrefixSet.contains(keyword.substring(0, 1))){
			keyword = keyword.substring(1);
		}
		return keyword;
	}


	public HashMap<String,String> getDatFileMap(){
		return datfileMap;
	}

	private Object[] getPartsArrForKeyWord(String keyword){
		List<LDrawFileInfo> partList = categorySetMap.get(keyword);

		//Object[] objArr =  hashSet.toArray();
		//List list = Arrays.asList(objArr);
		Collections.sort(partList);
		Object[] objArr = partList.toArray();
		return objArr;
	}

	//to be called from outside
	//if client of this class want to have particular node to be populated
	public LDrawFileInfoTreeNode populateThisTreeNode(LDrawFileInfoTreeNode root){

		Set<String> categKeySet =  categorySetMap.keySet();
		List<String> list = new ArrayList<String>(categKeySet);
		Collections.sort(list);

		Iterator it = list.iterator();
		while(it.hasNext()){
			String categStr = (String)it.next();
			LDrawFileInfoTreeNode dmtNode = new LDrawFileInfoTreeNode(categStr);
			populateCategory(categStr,dmtNode);
			root.add(dmtNode);
		}
		return root;
	}

	//append corresponding child nodes to root
	//requires: root is not null and keyword is not null
	//ensures: root has the childern whose name is in categorySetMap.get(keyword)
	private LDrawFileInfoTreeNode populateCategory(String keyword, LDrawFileInfoTreeNode root){

		Object[] objArr = this.getPartsArrForKeyWord(keyword);

	    for(int i=0; i<objArr.length; i++) {
		      Object childNode = objArr[i];

		      if(childNode instanceof LDrawFileInfo){
		    	  LDrawFileInfo ldfInfo = (LDrawFileInfo)childNode;
		    	  LDrawFileInfoTreeNode child = new LDrawFileInfoTreeNode(ldfInfo);
		        root.add(child);
		      }
	    }
	    return root;
	}


	//only for debuggin purpose
	public void printThisSet(String keyword){
		List<LDrawFileInfo> hashSet = categorySetMap.get(keyword);

		Iterator<LDrawFileInfo> it = hashSet.iterator();
		while(it.hasNext()){
			LDrawFileInfo info = it.next();
			System.out.println(info.getFile().getName()+"\t"+info.getDescription());
		}
	}

	//only for debugging purpose
	public static void main(String[] args){
		long lDateTime = new Date().getTime();

		File dir = new File("LDRAW/");
	   	LDrawPartsCrawler partCrawler = new LDrawPartsCrawler(dir);
		//File dir = new File("LDRAW/unofficial/p/48/");
		partCrawler.crawlLDrawRoot();

		long lDateTime2 = new Date().getTime();
		System.out.println("Date() - Time in milliseconds: " + (lDateTime2-lDateTime));
		partCrawler.printThisSet("technic");
	}
}
