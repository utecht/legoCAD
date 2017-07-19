package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TheoryPopulator {
	private HashMap<String, File> map;

	//Requires: user has created a default directory file, which contains the pathname for
	//the directory which contains the lego parts
	//Ensures: that a hashmap mapping part names to files is created
	private HashMap<String, File> populate(){
		File init=new File("default/init.txt");
		try {
			Scanner in = new Scanner(init);
			in.nextLine();
			File directory= new File(in.nextLine());
			File[] allChildren= directory.listFiles();
			for(int i=0; i<allChildren.length;i++){
				String partNum="";
				partNum = allChildren[i].getName();
				map.put(partNum,allChildren[i]);
			}
			return map;
		} catch (FileNotFoundException e) {
			System.out.println("User must initialize path file");
			e.printStackTrace();
		}
		return null;
	}
}
