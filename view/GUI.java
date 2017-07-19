package view;

import gnu.gleem.ExaminerViewer;
import gnu.gleem.MouseButtonHelper;
import gnu.gleem.linalg.Rotf;
import gnu.gleem.linalg.Vec3f;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.partsbrowser.PartsBrowser;

import model.Lego;
import model.LegoMover;
import model.LegoParser;
import model.Point3;
import model.Primitive;
import model.SquareMatrix;

public class GUI implements ActionListener, ChangeListener, MouseListener, WindowStateListener {

	private static final int BOTTOM_SIDE_WINDOW = 2;
	private static final int MID_SIDE_WINDOW = 1;
	private static final int TOP_SIDE_WINDOW = 0;
	private static final String PERSP = "persp";
	private static boolean fullscreen = false;
	private static final int BUTTON_WIDTH = 80;
	private static final int BUTTON_HEIGHT = 30;
	private static final String TOP = "Top", MIDDLE = "Middle", BOTTOM = "Bottom";
	private static String[] STANDARD_VIEWS = { "Left", "Right", "Top", "Bottom", "Front", "Back" , "Add View"};


	private File lib;
	private JMenu file;
	private JMenu options;
	private JMenuItem loadLib;
	private JMenuItem exit;
	private JMenuItem open;
	private JMenuItem saveMenu;


	private JMenuItem setCenter;
	private JMenuItem changeCenter;
	private JRadioButton useCenter;
	private JRadioButton displayCenter;
	private boolean permDispCenter = false;
	private boolean changing = false;

	//canvas Panel components
	private JMenuBar mb1;
	private JMenuBar mb2;
	private JMenuBar mb3;
	private JMenu view1;
	private JMenu view2;
	private JMenu view3;

	private JMenuItem left;
	private JMenuItem right;
	private JMenuItem top;
	private JMenuItem bottom;
	private JMenuItem front;
	private JMenuItem back;
	private JMenuItem new1;
	private JMenuItem[] menuItems = {left, right,top, bottom, front, back, new1};
	private JMenuItem left2;
	private JMenuItem right2;
	private JMenuItem top2;
	private JMenuItem bottom2;
	private JMenuItem front2;
	private JMenuItem back2;
	private JMenuItem new2;
	private JMenuItem[] menuItems2 = {left2, right2, top2, bottom2, front2, back2, new2};
	private JMenuItem left3;
	private JMenuItem right3;
	private JMenuItem top3;
	private JMenuItem bottom3;
	private JMenuItem front3;
	private JMenuItem back3;
	private JMenuItem new3;
	private JMenuItem[] menuItems3 = {left3, right3, top3, bottom3, front3, back3, new3};
	private JPanel perspCamera;
	private HashSet<String> views;
	private HashMap<String, JMenuItem> extraMenuItems;
	private HashMap<String, JPanel> cameras;
	private HashMap<String, GLAutoDrawable> canvases;
	private String[] currentViews;
	private JPanel cam1;
	private JPanel cam2;
	private JPanel cam3;
	private JPanel sidePanel;

	private JButton moveUp;
	private JButton moveDown;
	private JButton moveLeft;
	private JButton moveRight;
	private JButton moveToward;
	private JButton moveAway;
	private JSlider moveSpeed;

	private JButton rotUp;
	private JButton rotDown;
	private JButton rotLeft;
	private JButton rotRight;
	private JButton rotToward;
	private JButton rotAway;
	private JSlider rotSpeed;

	private JFrame tools;
	private JPanel buttons;
	private JPanel topMoveGrid;
	private JPanel botMoveGrid;
	private JPanel topRotGrid;
	private JPanel botRotGrid;

	private JMenuBar menu;
	private Scene scene;

	private int nextLegoID;
	private Lego center;
	private int cur;
	private Map<String, String> map;

	public static String libdir = ".";
	private LegoMover mover;
	private BlocksList blockList;
	private PartsBrowser partsBrowser;
	private JButton addLegoButton;
	private JSplitPane split;
	private JCheckBoxMenuItem fullscreenMenu;
	public static void main(String[] args) throws IOException {
		new GUI();
	}


	public GUI(){

		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		cur = 0;
		nextLegoID = 1;
		this.scene = new Scene();
		loadCenterMarker();
		this.map = new HashMap<String, String>();
		//parent = frame;
		menu = new JMenuBar();
		file = new JMenu("File");
		//windows = new JMenu("Windows");
		options = new JMenu("Options");

		fullscreenMenu = new JCheckBoxMenuItem("fullscreen");
		fullscreenMenu.setSelected(fullscreen);
		fullscreenMenu.addActionListener(this);
		options.add(fullscreenMenu);

		menu.add(file);
		//menu.add(windows);
		menu.add(options);
		loadLib = new JMenuItem("Load Library...");
		loadLib.addActionListener(this);
		file.add(loadLib);
		open = new JMenuItem("Open...");
		open.addActionListener(this);
		file.add(open);
		saveMenu = new JMenuItem("Save");
		saveMenu.addActionListener(this);
		file.add(saveMenu);
		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		file.add(exit);

		changeCenter = new JMenuItem("Change Center of Rotation");
		changeCenter.addActionListener(this);
		options.add(changeCenter);
		setCenter = new JMenuItem("Set Center of Rotation");
		setCenter.addActionListener(this);
		options.add(setCenter);
		useCenter = new JRadioButton("Use Center of Rotation", false);
		useCenter.addActionListener(this);
		options.add(useCenter);
		displayCenter = new JRadioButton("Always Display Center of Rotation", false);
		displayCenter.addActionListener(this);
		options.add(displayCenter);
		moveUp = new JButton("up");
		moveUp.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		moveUp.addActionListener(this);
		moveDown = new JButton("down");
		moveDown.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		moveDown.addActionListener(this);
		moveLeft = new JButton("left");
		moveLeft.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		moveLeft.addActionListener(this);
		moveRight = new JButton("right");
		moveRight.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		moveRight.addActionListener(this);
		moveToward = new JButton("toward");
		moveToward.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		moveToward.addActionListener(this);
		moveAway = new JButton("away");
		moveAway.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		moveAway.addActionListener(this);
		rotUp = new JButton("up");
		rotUp.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		rotUp.addActionListener(this);
		rotDown = new JButton("down");
		rotDown.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		rotDown.addActionListener(this);
		rotLeft = new JButton("left");
		rotLeft.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		rotLeft.addActionListener(this);
		rotRight = new JButton("right");
		rotRight.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		rotRight.addActionListener(this);
		rotToward = new JButton("toward");
		rotToward.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		rotToward.addActionListener(this);
		rotAway = new JButton("away");
		rotAway.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		rotAway.addActionListener(this);

		//Menu items for the 3 changeable views
		initViewMenuItems(menuItems, TOP);
		initViewMenuItems(menuItems2, MIDDLE);
		initViewMenuItems(menuItems3, BOTTOM);
		extraMenuItems = new HashMap<String, JMenuItem>(15);
		cameras = new HashMap<String, JPanel>(15);
		canvases = new HashMap<String, GLAutoDrawable>(15);
		currentViews = new String[3];

		//Menus for the 3 changeable views
		mb1 = new JMenuBar();
		mb2 = new JMenuBar();
		mb3 = new JMenuBar();

		views = new HashSet<String>();
		for(int i = 0; i <STANDARD_VIEWS.length; i++){
			views.add(STANDARD_VIEWS[i]);
		}

		view1 = new JMenu("View");
		mb1.add(view1);
		populateMenu(view1, menuItems);

		view2 = new JMenu("View");
		mb2.add(view2);
		populateMenu(view2, menuItems2);

		view3 = new JMenu("View");
		mb3.add(view3);
		populateMenu(view3, menuItems3);

		tools = new JFrame("LegoCAD");
		tools.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tools.setJMenuBar(menu);
		//tools.setSize(800, 600);
		/*tools.setLocation(
				((screenSize.width - frame.getWidth()) / 2) - frame.getWidth() / 4,
				((screenSize.height - frame.getHeight()) / 2)
		);*/

		//tools.setLayout(new GridLayout(3,1));
		tools.setLayout(new BorderLayout());
		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		tools.add(sidePane, BorderLayout.WEST);
		//makeButtons(sidePane);
		JPanel canvasPanel = null;
		mover = new LegoMover(scene);
		try
		{
			canvasPanel = createCanvasPanel();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tools.add(canvasPanel, BorderLayout.CENTER);

		if(fullscreen) GLDisplay.makeJFrameFullscreen(tools, 800, 600);
		tools.setVisible(true);

		tools.pack();

		blockList = new BlocksList(mover, scene);

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);

		partsBrowser = new PartsBrowser();
		partsBrowser.getLDrawPartsTree().addMouseListener(this);
		addLegoButton = partsBrowser.getSelectionButton();
		addLegoButton.addActionListener(this);
		map = partsBrowser.getDataFileMap();
		tabs.addTab("Part Library", partsBrowser);
		tabs.addTab("Block List", blockList);
		tabs.setMinimumSize(new Dimension(200, 200));
		//sidePane.add(new JScrollPane(blockList));
		sidePane.add(tabs, BorderLayout.CENTER);

		tools.addWindowStateListener(this);



	}
	private void loadCenterMarker(){
		center = new Lego(new File("model/centerMarker.dat"), SquareMatrix.IdentityMatrix(), null, -1);
		//scene.getLegos().add(center);
		center = LegoParser.parse(center);
		for(Primitive x: center.getShape().getPrims()){x.setColor(Color.red);}
		center.setShape(false);
		//scene.newLego(center);
	}

	private JPanel createCanvasPanel() throws IOException
	{
		JPanel canvasPanel = new JPanel();

		cam1 = new JPanel(new BorderLayout());
		cam2 = new JPanel(new BorderLayout());
		cam3 = new JPanel(new BorderLayout());

		cameras.put("Top", createCanvas("Top", new Point3(0, -300, 0), new Rotf(Vec3f.X_AXIS, (float)Math.toRadians(90)), true, true, false, true));
		cameras.put("Bottom", createCanvas("Bottom", new Point3(0, 300, 0), new Rotf(Vec3f.X_AXIS, (float)Math.toRadians(-90)), true, true, false, true));
		cam1.add(cameras.get("Top"), BorderLayout.CENTER);
		currentViews[TOP_SIDE_WINDOW] = "Top";
		cam1.add(mb1, BorderLayout.NORTH);

		cameras.put("Front", createCanvas("Front", new Point3(0, 0, 300), new Rotf(), true, true, true, false));
		cameras.put("Back", createCanvas("Back", new Point3(0, 0, -300), new Rotf(Vec3f.Y_AXIS, (float)Math.toRadians(180)), true, true, true, false));
		cam2.add(cameras.get("Front"), BorderLayout.CENTER);
		currentViews[MID_SIDE_WINDOW] = "Front";
		cam2.add(mb2, BorderLayout.NORTH);

		cameras.put("Left", createCanvas("Left", new Point3(200, 0, 0), new Rotf(Vec3f.Y_AXIS, (float)Math.toRadians(90)), true, false, true, true));
		cameras.put("Right", createCanvas("Right", new Point3(-200, 0, 0), new Rotf(Vec3f.Y_AXIS, (float)Math.toRadians(-90)), true, false, true, true));
		cam3.add(cameras.get("Left"), BorderLayout.CENTER);
		currentViews[BOTTOM_SIDE_WINDOW] = "Left";
		cam3.add(mb3, BorderLayout.NORTH);

		perspCamera = createCanvas(PERSP, new Point3(0, -1000, 1000), new Rotf(Vec3f.X_AXIS, (float)Math.toRadians(45)), false, true, true, true);
		canvasPanel.setLayout(new BorderLayout());

		canvasPanel.add(perspCamera, BorderLayout.CENTER);
		sidePanel = new JPanel();
		sidePanel.add(cam1);
		sidePanel.add(cam2);
		sidePanel.add(cam3);
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		canvasPanel.add(sidePanel, BorderLayout.EAST);
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, perspCamera, sidePanel);

		canvasPanel.add(split, BorderLayout.CENTER);
		perspCamera.setPreferredSize(new Dimension(800, 500));
		split.setDividerLocation(0.9);

		return canvasPanel;
	}

	private JPanel createCanvas(String name, Point3 loc, Rotf rotation, boolean ortho, boolean showX, boolean showY, boolean showZ) throws IOException
	{
		JPanel canvasP;

		GLDisplay display = GLDisplay.createGLDisplay(name);
		//InputHandler inputHandler = new InputHandler(scene, display);
		display.addGLEventListener(scene);
		GLCanvas canvas = display.getCanvas();

		canvasP = new JPanel();
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		canvasP.setBorder(BorderFactory.createTitledBorder(border, name));
		//canvasP.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		canvasP.setLayout(new BorderLayout());
		canvasP.add(canvas, BorderLayout.CENTER);


		Rotf rotateZ = new Rotf(Vec3f.Z_AXIS, (float)Math.toRadians(180));
		Rotf rot = new Rotf();
		rot.mul(rotation, rotateZ);
		CameraData data = new CameraData(ortho, loc, rot);
		data.setDisplayX(showX);
		data.setDisplayY(showY);
		data.setDisplayZ(showZ);
		scene.registerCanvas(canvas, data);
		canvas.setMinimumSize(new Dimension(200, 200));
		canvas.setPreferredSize(new Dimension(200, 200));
		scene.setSelectedCanvas(canvas);
		//InputHandler handler = new InputHandler(scene, display);
		display.addGLEventListener(scene);

		display.addKeyListener(mover);
		//display.addKeyListener(handler);
		display.start();
		display.addMouseListener(this);
		//display.addMouseListener(handler);
		//display.addMouseMotionListener(handler);
		//canvasP.addMouseWheelListener(handler);
		canvases.put(name, canvas);
		return canvasP;
	}

	//the last statement is an else containing a method for resolving the tools Panel button
	//action events.  All other events should be placed before this or defined within
	//determineMovement(ActionEvent e)
	public void actionPerformed(ActionEvent e) {

		String aCom = e.getActionCommand();
		String command= "";
		String panel = "";
		String[] com;

		com = aCom.split(" ");

		for(int i=0;i<com.length;i++){

				if(i == com.length-1){
					panel = com[i];
				}else{
					if(command != ""){
						command += " ";
					}
					command += com[i];
				}
		}

		if(views.contains(command)){

			if(!command.equals(currentViews[TOP_SIDE_WINDOW]) &&
			   !command.equals(currentViews[MID_SIDE_WINDOW]) &&
			   !command.equals(currentViews[BOTTOM_SIDE_WINDOW])){
				changeView(command, panel);
			}

		}


		if(e.getSource() == exit){
			int selectedOption = JOptionPane.showOptionDialog(
					tools,
					"Are you sure you want to exit?",
					"Confirm",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					new Object[]{"Yes", "No"},
					"Windowed" );
			if(selectedOption == 0) System.exit(0);

		} else if(e.getSource() == changeCenter){
			//legoCAD.changeCur(-1);
			Lego center = getCenterLego();
			center.setShape(true);
			changing = true;

		} else if(e.getSource() == setCenter){
			Lego center = getCenterLego();
			if(!permDispCenter){center.setShape(false);}
			changing = false;
			//legoCAD.changeCur(legoCAD.getLegos().size());

		} else if(e.getSource() == displayCenter){
			permDispCenter = displayCenter.isSelected();
			Lego center = getCenterLego();
			if(!changing){center.setShape(permDispCenter);}

		} else if(e.getSource() == useCenter){
			boolean use = useCenter.isSelected();
			scene.useSetCenter(use);
			if(use){
				scene.setCenter(getCenterLego().getShape().center());
			}
			Lego center = getCenterLego();
			if(use){
				center.setShape(permDispCenter);
				scene.setCenter(center.getShape().center());
			}else{
				//legoCAD.changeCur(legoCAD.getLegos().size() - 1);
				scene.setCenter(getLego().getShape().center());
			}

		} else if(e.getSource() == open){

			JFileChooser chooser = new JFileChooser(libdir);
			int returnVal = chooser.showDialog(tools, "Open");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				addLego(chooser.getSelectedFile());
			}else if(returnVal == JFileChooser.CANCEL_OPTION){
			}else{
				System.out.println("File Load Error");
			}

		} else if(e.getSource() == loadLib){
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showDialog(tools, "Select Base Directory");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				//loadLibrary(chooser.getSelectedFile());
				libdir = chooser.getSelectedFile().getAbsolutePath();
				lib = chooser.getSelectedFile();
				//blocks.setLibrary(lib);
				//inTakeFiles(lib);
				partsBrowser.populateLDrawParts(lib);
				map = partsBrowser.getDataFileMap();


				//System.out.println((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0*1024.0) + " Megabytes of memory used.");
			}else if(returnVal == JFileChooser.CANCEL_OPTION){
			}else{
				System.out.println("File Load Error");
			}

		} else if(e.getSource() == saveMenu){
			JFileChooser chooser = new JFileChooser(libdir);
			int returnVal = chooser.showDialog(tools, "Save As");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				save(chooser.getSelectedFile());
			}else if(returnVal == JFileChooser.CANCEL_OPTION){
			}else{
				System.out.println("File Save Error");
			}

		} else if(e.getSource() == addLegoButton){

			 File file = partsBrowser.getLastSelectedPartFile();

			addLego(file);
		} else if(e.getSource() == fullscreenMenu)
		{
			fullscreen = fullscreenMenu.getState();
			if(fullscreen) GLDisplay.makeJFrameFullscreen(tools, 800, 600);
			else
			{
				GLDisplay.unfullscreen(tools);
			}
		}
	}
	public int save(File file){
		//File file = new File("TANK.dat");
		FileOutputStream fs;
		try {
			fs = new FileOutputStream(file);
		}catch(IOException e){
			e.printStackTrace();
			return -1;
		}
		String str = "";
		str += "0 Saved by Software Engineering Program\n";
		for(int i = 0; i < scene.getLegos().size(); i++){
			str += "1 24 " + scene.getLegos().get(i).getMatrix().printToSave()+ " " + scene.getLegos().get(i).getFile().getName() + "\n";
		}
		try{
			fs.write(str.getBytes());
		}catch(IOException e){
			e.printStackTrace();
			return -1;
		}
		try{
			fs.close();
		}catch(IOException g){
			g.printStackTrace();
			return -1;
		}
		return 0;

	}
    //	req: the base directory (in fact, this should only be called when the base directory is set)
	//ens: moves through all directories in LDraw specified by makeFileStubs, adds their contents to map as <partName, filePath>
	public void inTakeFiles(File file){
		if (file!=null){
			ArrayList<String> stubs=makeFileStubs();
			File currentFile=file;

			while(!currentFile.getAbsolutePath().endsWith(File.separator+"LDRAW")){
				currentFile=new File (currentFile.getParent());
			}

			for(int j=0; j<stubs.size();j++){
				populate(new File(currentFile.getAbsolutePath()+stubs.get(j)));
			}
		}
	}
	//	req: called from inTakeFiles, requires nothing really...
	//ens: make an arrayList of file name stubs (ie "/PARTS/P") for later use in building the hashMap
	//currently does not factor in upper/lower case or the unofficial subdirectory of the offical LDraw
	private ArrayList<String> makeFileStubs(){
		ArrayList<String> dirNames=new ArrayList<String>();

		String partsDir= File.separator+"PARTS"+File.separator;
		String sDir= File.separator+"PARTS"+File.separator+"S"+File.separator;
		String pDir= File.separator+"P"+File.separator;
		String fourEightDir= File.separator+"P"+File.separator+"48"+File.separator;

		dirNames.add(partsDir);
		dirNames.add(pDir);
		dirNames.add(sDir);
		dirNames.add(fourEightDir);

		return dirNames;
	}
	private void populate(File currentFile){
		if (!currentFile.isDirectory()){
			currentFile=currentFile.getParentFile();
			}
		File[] allChildren= currentFile.listFiles();
		for(int i=0; i<allChildren.length;i++){
			String line ="";
			String partNum="";
			line=allChildren[i].getAbsolutePath();
			partNum = allChildren[i].getName();
			//System.err.println(partNum);
			//String[] datExtract = (line.split(File.separator));
			//partNum = datExtract[datExtract.length-1];
			if (!partNum.contains(".dat")){
				partNum+=".dat";
			}
			System.out.println(partNum +"   "+ line);
			map.put(partNum.toLowerCase(),line);

		}
	}

	public Lego getCenterLego(){
		return center;
	}
	public void addLego(File legoFile)
	{
		Lego lego = new Lego(legoFile, SquareMatrix.IdentityMatrix(), map, nextLegoID);
		long startTime = System.currentTimeMillis();
		Lego parsedLego = LegoParser.parse(lego);
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime + " milliseconds to load " + parsedLego.getName());
		scene.addLego(parsedLego);
		blockList.setLegos(scene.getLegos().toArray(new Lego[0]));
		nextLegoID++;
	}
	public void newLego(File legoFile) {
		scene.getLegos().add(new Lego(legoFile, SquareMatrix.IdentityMatrix(), map, nextLegoID));
		nextLegoID++;
		Lego lego = scene.getLegos().get(cur);
		Lego legos = LegoParser.parse(lego);
		scene.newLego(legos);

		cur++;
		blockList.setLegos(scene.getLegos().toArray(new Lego[0]));
	}
	public Lego getLego(){
		return scene.getLegos().get(cur);
	}
	//Pre: one of the speed sliders has been moved
	//Post: call a method in input handler to adjust the rate
	//      of movement or rotation
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == moveSpeed){
			moveSpeed.getValue();  //the current value of the slider
		}else if(e.getSource() == rotSpeed){

		}
	}

	public void mouseClicked(MouseEvent e) {
		Object source = e.getSource();
		if(source == partsBrowser.getLDrawPartsTree())
		{
			 if(e.getClickCount()==2){
				 File file = partsBrowser.getLastSelectedPartFile();
				 addLego(file);
			 }
		}
	}



	//Pre: Items have been added to the JMenuItem[] in the same order as they appear
	//	   in STANDARD_VIEWS.  This method should only be called from the constructor.
	//     cur and STANDARD_VIEWS have the same length.
	//Post: Ensures the creation of MenuItems with listeners and the proper actionCommands
	//		for each member of the JMenuItem[].
	private void initViewMenuItems(JMenuItem[] cur, String panel){

		if(cur.length == STANDARD_VIEWS.length){
			for(int i = 0; i < cur.length; i++){
				cur[i] = new JMenuItem(STANDARD_VIEWS[i]);
				cur[i].addActionListener(this);
				cur[i].setActionCommand(STANDARD_VIEWS[i] + " " + panel);
			}
		}
	}

	//Post: Adds each member of views to the cur JMenu
	private void populateMenu(JMenu cur, JMenuItem[] views){

		for(int i=0; i<views.length; i++){
			cur.add(views[i]);
		}

	}

	//Post: Ensures that the new view has been added to the proper panel
	private void addView(JMenu menu, JPanel curCam, JMenuBar mb, String sideWindow){
		String name = "";
		while(name.equals("")){
			name = JOptionPane.showInputDialog("Please enter a name for the new view.");
		}
		CameraData cameraData = scene.getCameraData(canvases.get(PERSP));
		try {
			Rotf rotation = cameraData.getRotation();
			Rotf rotateZ = new Rotf(Vec3f.Z_AXIS, (float)Math.toRadians(180));
			Rotf rot = new Rotf();
			rot.mul(rotation, rotateZ);
			cameras.put(name, createCanvas(name, cameraData.getPosition(), rot, false, true, true, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		extraMenuItems.put(name, new JMenuItem(name));
		extraMenuItems.get(name).addActionListener(this);
		menu.add(extraMenuItems.get(name));
		extraMenuItems.get(name).setActionCommand(name + " " + sideWindow);
		System.out.println(name + " " + sideWindow);
		views.add(name);
		curCam.removeAll();
		curCam.add(cameras.get(name), BorderLayout.CENTER);
		curCam.add(mb, BorderLayout.NORTH);
	}

	//Post: Changes the view of the panel to the view that has been selected
	private void changeView(String view, String panel){
		String name = view;
		int curView = -1;

		if(panel.equals("Top")){

			if(view.equals("Add View")){
				addView(view1, cam1, mb1, panel);
			}else{
				cam1.removeAll();
				cam1.add(cameras.get(view), BorderLayout.CENTER);
				cam1.add(mb1, BorderLayout.NORTH);
			}
			curView = TOP_SIDE_WINDOW;
		}else if(panel.equals("Middle")){

			if(view.equals("Add View")){
				addView(view2, cam2, mb2, panel);
			}else{
				cam2.removeAll();
				cam2.add(cameras.get(view), BorderLayout.CENTER);
				cam2.add(mb2, BorderLayout.NORTH);
			}
			curView = MID_SIDE_WINDOW;
		}else if(panel.equals("Bottom")){

			if(view.equals("Add View")){
				addView(view3, cam3, mb3, panel);
			}else{
				cam3.removeAll();
				cam3.add(cameras.get(view), BorderLayout.CENTER);
				cam3.add(mb3, BorderLayout.NORTH);
			}
			curView = BOTTOM_SIDE_WINDOW;
		}
		currentViews[curView] = name;
		sidePanel.revalidate();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}


	public void windowStateChanged(WindowEvent e)
	{
		if(split != null) split.setDividerLocation(0.6);
		//split.revalidate();
	}
}


