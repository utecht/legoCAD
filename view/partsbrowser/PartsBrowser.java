package view.partsbrowser;

import gnu.gleem.linalg.Rotf;
import gnu.gleem.linalg.Vec3f;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import view.CameraData;
import model.Lego;
import model.LegoParser;
import model.Point3;
import model.SquareMatrix;

import view.GLDisplay;
import view.InputHandler;
import view.Scene;

public class PartsBrowser extends JPanel implements TreeSelectionListener, TreeWillExpandListener, TreeExpansionListener{

	private JPanel modelViewerPanel;
	private JPanel partListPanel;
	private JList partCatalogList;
	private JButton selectButton;

	//private JTree partsTree;
	JTree ldrawPartTree;
	LDrawFileInfoTreeNode ldfTreeRootNode;

	private Scene scene;

	GLCanvas canvas;

	private int legoID=1;
	private int cur =0;
	private HashMap<String, String> datfileMap;

	//ensure: subclass of JPanel is created with super() call
	//		and all GUI components on it are created and initialized
	//		and ldrawPartTree is also created BUT not containing any useful data

	public PartsBrowser() throws HeadlessException {
		super();
		//setTitle("Parts Browser");
		this.setPreferredSize(new Dimension(180,180));
		setMinimumSize(new Dimension(180, 180));
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setResizable(true);

		try {
			initGUIComponents();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	//ensures: init GUI elements
	private void initGUIComponents() throws IOException{

		this.scene = new Scene();

		modelViewerPanel = new JPanel();
		//modelViewerPanel.setLayout(new BorderLayout());
		modelViewerPanel.add(createCanvas("persp", new Point3(0, -300, 300), new Rotf(Vec3f.X_AXIS, (float)Math.toRadians((45))), false));
		//modelViewerPanel.add(createCanvas("yCamera", new Point3(0, -300, 0), new Rotf(Vec3f.X_AXIS, (float)Math.toRadians(90)), true));
		partCatalogList = new JList();
		selectButton = new JButton("Add Lego");
		partListPanel = new JPanel();
		partListPanel.setPreferredSize(new Dimension(180,400));

		this.setLayout(new BorderLayout());

		//this.add(modelViewerPanel, BorderLayout.CENTER);
		this.add(selectButton, BorderLayout.NORTH);
		this.add(partListPanel, BorderLayout.WEST);
		//this.add(new JButton("East"), BorderLayout.EAST);
		//this.add(new JButton("West"), BorderLayout.WEST);

		partListPanel.setLayout(new BoxLayout(partListPanel,BoxLayout.Y_AXIS));
		partListPanel.add(initPartsTree());
		partListPanel.add(partCatalogList);
		partListPanel.add(modelViewerPanel);
	}

	//ensure: create and return a scroll panel containing JTree
	//  created JTree's root node is assigned to ldfTreeRootNode(instance variable)
	private JScrollPane initPartsTree(){
		//File dir = new File("LDRAW/");
		//LDrawPartsCrawler partCrawler = new LDrawPartsCrawler(dir);
		//partCrawler.crawlLDrawRoot();

		//datfileMap = partCrawler.getDatFileMap();

		ldfTreeRootNode = new LDrawFileInfoTreeNode("Parts");
		//partCrawler.populateThisTreeNode(ldfTreeRootNode);

	    ldrawPartTree = new JTree(ldfTreeRootNode);
	    ldrawPartTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

	    ldrawPartTree.addTreeSelectionListener(this);
	    ldrawPartTree.addTreeExpansionListener(this);
        ldrawPartTree.addTreeWillExpandListener(this);

	    JScrollPane jsPane = new JScrollPane(ldrawPartTree);
	    jsPane.setPreferredSize(new Dimension(800,400));

	    return jsPane;
	}

	//start populate the root tree node
	//ensures: previous childern of ldrawTreeRootNode is cleaned up and
	//		ldfTreeRootNode is populated with children
	public void populateLDrawParts(File dir){
		LDrawPartsCrawler partCrawler = new LDrawPartsCrawler(dir);
		partCrawler.crawlLDrawRoot();

		datfileMap = partCrawler.getDatFileMap();

		ldfTreeRootNode.removeAllChildren();
		partCrawler.populateThisTreeNode(ldfTreeRootNode);

		ldrawPartTree.expandPath(new TreePath(ldfTreeRootNode));
		ldrawPartTree.repaint();

		//partListPanel.revalidate();
	}

	private JPanel createCanvas(String name, Point3 loc, Rotf rotation, boolean lockRot) throws IOException
	{
		JPanel canvasP;

		GLDisplay display = GLDisplay.createGLDisplay(name);
		InputHandler inputHandler = new InputHandler(scene, display);
		display.addGLEventListener(scene);
		GLCanvas canvas = display.getCanvas();

		canvasP = new JPanel();
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		//canvasP.setBorder(BorderFactory.createTitledBorder(border, name));
		//canvasP.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		canvasP.setLayout(new BorderLayout());
		canvasP.add(canvas, BorderLayout.CENTER);

		Rotf rotateZ = new Rotf(Vec3f.Z_AXIS, (float)Math.toRadians(180));
		Rotf rot = new Rotf();

		rot.mul(rotation, rotateZ);
		CameraData data = new CameraData(true, loc, rot);
		//data.setPosition(loc);
		//data.setRotation(rot);

		scene.registerCanvas(canvas, data);
		canvas.setMinimumSize(new Dimension(170, 170));
		canvas.setPreferredSize(new Dimension(170, 170));
		scene.setSelectedCanvas(canvas);
		//InputHandler handler = new InputHandler(scene, display);
		display.addGLEventListener(scene);

		//display.addKeyListener(mover);
		//display.addKeyListener(handler);
		display.start();
		//display.addMouseListener(this);
		//display.addMouseListener(handler);
		//display.addMouseMotionListener(handler);
		//canvasP.addMouseWheelListener(handler);
		return canvasP;

	}

	public Map<String,String> getDataFileMap(){
		return datfileMap;
	}

	//this can be used for adding listener from parent GUI
	public JButton getSelectionButton(){
		return selectButton;
	}

	//this can be used for adding listener from parent GUI
	public JTree getLDrawPartsTree(){
		return ldrawPartTree;
	}

	//requires: datfileMap is not null and sence is not null
	//	categorySetMap is popuated which means LDrawPartsCrawler.findAllPartsUnderThisDirectory is called before
	private void newLego(File legoFile) {

		Lego preLego = new Lego(legoFile, SquareMatrix.IdentityMatrix(), datfileMap, legoID);
		Lego legos = LegoParser.parse(preLego);
		scene.getLegos().add(legos);
		legoID++;
	}


	public static void main(String[] args){
		final PartsBrowser partsBrowser = new PartsBrowser();
		partsBrowser.setVisible(true);

		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {System.exit(0);}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}

		});
		frame.setSize(500,500);

		JButton populateBut = new JButton("Populate");
		populateBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				partsBrowser.populateLDrawParts(new File("LDRAW/"));
			}
		});

		frame.setLayout(new BorderLayout());
		frame.add(partsBrowser, BorderLayout.WEST);

		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(new Color(0,0,0));

		frame.add(centerPanel, BorderLayout.CENTER);

		frame.add(populateBut, BorderLayout.NORTH);

		frame.setVisible(true);
		//frame.pack();
	}

	//TreeWillExpandListener methods
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {}
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {}
	//TreeExpansionListener methods
	public void treeCollapsed(TreeExpansionEvent event) {}
	public void treeExpanded(TreeExpansionEvent event) {}

	//TreeSelectionEvent listener class
	//requires: ldrawPartTree is not null and sence is not null;
	//ensures: new lego corresponding to last selected LDrawFileInfoTreeNode is created in the sence
	public void valueChanged(TreeSelectionEvent e) {
		File file = getLastSelectedPartFile();
		if(file!=null){

			scene.clearScene();
			cur = 0;

			newLego(file);
		}
	}

	//requires: ldrawPartTree is not null and categorySetMap is populated
	//ensures: return the file associated with the last selected LDrawFileInfoTreeNode from the parts tree
	public File getLastSelectedPartFile(){
		Object lastSelectedComponent = ldrawPartTree.getLastSelectedPathComponent();
		File file = null;
		if(lastSelectedComponent!=null){
			if(lastSelectedComponent instanceof LDrawFileInfoTreeNode){
				LDrawFileInfoTreeNode ldfTreeNode = (LDrawFileInfoTreeNode)lastSelectedComponent;
				LDrawFileInfo ldfInfo = ldfTreeNode.getLDrawFileInfo();
				if(ldfInfo!=null){
					file = ldfInfo.getFile();
				}
			}
		}

		return file;
	}


}
