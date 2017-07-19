package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


import model.Lego;
import model.LegoMover;
import model.Primitive;


public class BlocksList extends JPanel implements TreeSelectionListener, KeyListener
{
	private JCheckTree tree;
	private DefaultTreeModel model;
	private LegoMover mover;
	private Scene renderer;
	public BlocksList(LegoMover m, Scene s)
	{
		mover = m;
		renderer = s;
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		setLayout(new BorderLayout());
	}
	public void setLegos(Lego[] legos)
	{
		this.removeAll();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new JCheckTree.CheckStore(null, false));



		model = new DefaultTreeModel(root);
		//tree.setModel(model);
		tree = new JCheckTree(model);

		tree.addKeyListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);


		for(Lego l: legos)
		{
			makeChild(root, l);
		}
		JScrollPane scrollPane = new JScrollPane(tree);
		this.add(scrollPane);
		tree.addTreeSelectionListener(this);
		tree.expandRow(0);
		tree.setRootVisible(false);
		revalidate();
		repaint();
	}
	private void makeChild(DefaultMutableTreeNode parent, Lego l)
	{
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new JCheckTree.CheckStore(l, l.isVisible()));
		model.insertNodeInto(childNode, parent, parent.getChildCount());

		for(Lego lego: l.getLegos())
		{
			makeChild(childNode, lego);
		}
	}
	public void valueChanged(TreeSelectionEvent e)
	{
		TreePath path = e.getNewLeadSelectionPath();
		if(path != null)
		{
			DefaultMutableTreeNode last = (DefaultMutableTreeNode)path.getLastPathComponent();
			JCheckTree.CheckStore check = (JCheckTree.CheckStore)last.getUserObject();
			Lego lego = check.getLego();
			mover.setLego(lego);


		}


	}
	public void keyPressed(KeyEvent e) {
		TreePath selectionPath = tree.getSelectionPath();

		if(e.getKeyCode() == KeyEvent.VK_DELETE && selectionPath != null)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectionPath.getLastPathComponent();
			JCheckTree.CheckStore check = (JCheckTree.CheckStore)node.getUserObject();
			Lego lego = check.getLego();
			renderer.removeLego(lego);
			model.removeNodeFromParent(node);
			renderer.selectLego(null);
			tree.clearSelection();
		}
	}
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	public void keyTyped(KeyEvent e) {
	}



}
