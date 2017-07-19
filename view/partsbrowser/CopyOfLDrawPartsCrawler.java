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


import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;


import com.sun.corba.se.impl.ior.ByteBuffer;

public class CopyOfLDrawPartsCrawler {

	public void PartsCrawler(){
	}

	public void findAllPartsUnderThisDirectory(File curDirectory, boolean isLDrawRoot){

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
	        		if(filename.endsWith(".dat")||(filename.endsWith("*.ldr"))){
	        			try {
							processFileContent(files[i]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
	        		}else{
	        			if(!isLDrawRoot)return;
	        		}
	        		//System.out.println(filename);
	        	}
	        }
	    }
	}

	public void processFileContent(File file) throws IOException{
		int SIZE=16;
		byte[] b=new byte[SIZE];
		FileInputStream fstream = new FileInputStream(file);
	    // Get the object of DataInputStream

		int len = (SIZE<fstream.available())?SIZE:fstream.available();

		fstream.read(b,0,len);

		//String str = new String(b);

		//System.out.println(str);
	}
	public void processFileContent4(File file) throws IOException{
		FileInputStream fstream = new FileInputStream(file);
	    // Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    //Read File Line By Line
	    if ((strLine = br.readLine()) != null)   {
	      // Print the content on the console
	      //System.out.println (strLine);
	    }
	    //Close the input stream
	    in.close();
	}

	public void processFileContent2(File file) throws IOException{
		Scanner in = new Scanner(file);
		String line;

		int wordcount=0;
		String keyword = "";
		while(in.hasNextLine()&&wordcount<2){
			//System.out.println(in.next());
			keyword = in.next();
			wordcount++;
		}
		//System.out.println(keyword);
	}

	public static void main(String[] args){

		long lDateTime = new Date().getTime();


	   	CopyOfLDrawPartsCrawler partCrawler = new CopyOfLDrawPartsCrawler();
		//File dir = new File("LDRAW/unofficial/p/48/");
		File dir = new File("LDRAW/");

		partCrawler.findAllPartsUnderThisDirectory(dir, true);


		long lDateTime2 = new Date().getTime();
		System.out.println("Date() - Time in milliseconds: " + (lDateTime2-lDateTime));

	}

	public static final int SIZE = 1024;
	  public void processFileContent3(File file) throws IOException{
		FileInputStream f = new FileInputStream( file );

		FileChannel ch = f.getChannel( );
		MappedByteBuffer mb = ch.map( FileChannel.MapMode.READ_ONLY,0L, ch.size( ) );
		byte[] barray = new byte[SIZE];
		long checkSum = 0L;
		int nGet;
		if( mb.hasRemaining( ) )
		{
		    nGet = Math.min( mb.remaining( ), SIZE );
		    mb.get( barray, 0, nGet );
		    for ( int i=0; i<nGet; i++ ){
		        checkSum += barray[i];
		    }
		}
	}

}
