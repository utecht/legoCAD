package view;


import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import model.Lego;



public class JCheckTree extends JTree
{

	public JCheckTree(TreeModel model)
	{
		super();
		setModel(model);
		setEditable(true);
		CheckPanelRenderer renderer = new CheckPanelRenderer();
		setCellRenderer(renderer);
		setCellEditor(new CheckPanelEditor(this, renderer));


	}



	class CheckPanelEditor extends AbstractCellEditor implements TreeCellEditor, ActionListener, TreeSelectionListener {
		private Lego lego;
		JTree tree;
		CheckPanelRenderer renderer;
		JLabel label;
		EditorPanel panel;
		JCheckBox checkBox;
		CheckStore checkStore;
		protected transient int offset;
		protected int clickCountToStart = 1;

		public CheckPanelEditor(JTree tree, CheckPanelRenderer cpr) {
			this.tree = tree;
			renderer = cpr;
			tree.addTreeSelectionListener(this);
			label = new JLabel();
			checkBox = new JCheckBox();
			checkBox.setBorder(null);
			checkBox.setBackground(renderer.getBackgroundNonSelectionColor());
			checkBox.addActionListener(this);
			checkBox.setRequestFocusEnabled(false);
			panel = new EditorPanel();
			panel.setBorder(null);
			panel.setBackground(renderer.getBackgroundNonSelectionColor());
			panel.add(label);
			panel.add(checkBox);
		}

		public Component getTreeCellEditorComponent(JTree tree,
				Object value,
				boolean isSelected,
				boolean expanded,
				boolean leaf,
				int row) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			checkStore = (CheckStore)node.getUserObject();
			label.setText(checkStore.toString());
			checkBox.setSelected(checkStore.getState());

			if(leaf) {
				label.setIcon(renderer.getLeafIcon());
			} else if(expanded) {
				label.setIcon(renderer.getOpenIcon());
			} else {
				label.setIcon(renderer.getClosedIcon());
			}
			return panel;
		}

		public Object getCellEditorValue() {
			//checkStore.text = label.getText();
			checkStore.state = checkBox.isSelected();
			return checkStore;
		}

		public boolean isCellEditable(EventObject anEvent) {
			if(anEvent != null && anEvent instanceof MouseEvent &&
					((MouseEvent)anEvent).getClickCount() >= clickCountToStart) {
				TreePath path = tree.getPathForLocation(
						((MouseEvent)anEvent).getX(),
						((MouseEvent)anEvent).getY());
				int row = tree.getRowForPath(path);
				Object value = path.getLastPathComponent();
				boolean isSelected = tree.isRowSelected(row);
				boolean expanded = tree.isExpanded(path);
				boolean leaf = tree.getModel().isLeaf(value);
				determineOffset(tree, value, isSelected, expanded, leaf, row);
				Rectangle pb = tree.getPathBounds(path);
				Rectangle target = new Rectangle(offset, pb.y,
						pb.x+pb.width-offset,
						pb.height);
				Point p = ((MouseEvent)anEvent).getPoint();
				if(target.contains(p)) {
					tree.startEditingAtPath(path);
					return true;
				}
			}
			return false;
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if(source instanceof JCheckBox && lego != null)
			{
				JCheckBox check = (JCheckBox)source;
				lego.setVisible(check.isSelected());
			}

			super.stopCellEditing();
		}

		protected void determineOffset(JTree tree, Object value,
				boolean isSelected, boolean expanded,
				boolean leaf, int row) {
			int x0 = tree.getPathBounds(tree.getPathForRow(row)).x;
			int hgap = renderer.getHgap();
			offset = x0 + hgap;
			Icon editingIcon = null;
			if(leaf)
				editingIcon = renderer.getLeafIcon();
			else if(expanded)
				editingIcon = renderer.getOpenIcon();
			else
				editingIcon = renderer.getClosedIcon();
			if(editingIcon != null)
				offset += editingIcon.getIconWidth() +
				renderer.getIconTextGap();
			String stringValue = tree.convertValueToText(value, isSelected,
					expanded, leaf, row, false);
			Font font = tree.getFont();
			FontRenderContext frc = new FontRenderContext(null, false, false);
			int width = (int)font.getStringBounds(stringValue, frc).getWidth();
			offset += width + hgap;
		}

		public void valueChanged(TreeSelectionEvent e)
		{
			TreePath path = e.getNewLeadSelectionPath();
			if(path != null)
			{
				DefaultMutableTreeNode last = (DefaultMutableTreeNode)path.getLastPathComponent();
				JCheckTree.CheckStore check = (JCheckTree.CheckStore)last.getUserObject();
				lego = (Lego)check.getLego();

			}
		}


		private class EditorPanel extends JPanel {
			Color treeBGColor;
			Color focusBGColor;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Color bColor = renderer.getBackgroundSelectionColor();

				int imageOffset = -1;
				if(bColor != null) {
					Icon currentI = label.getIcon();

					imageOffset = renderer.getLabelStart();
					g.setColor(bColor);
					g.fillRect(imageOffset, 0, getWidth() - imageOffset,
							getHeight());
				}

				if(renderer.drawsFocusBorderAroundIcon) {
					imageOffset = 0;
				} else if(imageOffset == -1) {
					imageOffset = renderer.getLabelStart();
				}
				paintFocus(g, imageOffset, 0, getWidth() - imageOffset,
						getHeight(), bColor);
			}

			private void paintFocus(Graphics g, int x, int y,
					int w, int h, Color notColor) {
				Color bsColor = renderer.getBorderSelectionColor();

				if (bsColor != null) {
					g.setColor(bsColor);
					g.drawRect(x, y, w - 1, h - 1);
				}
				if (renderer.drawDashedFocusIndicator && notColor != null) {
					if (treeBGColor != notColor) {
						treeBGColor = notColor;
						focusBGColor = new Color(~notColor.getRGB());
					}
					g.setColor(focusBGColor);
					BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
				}
			}
		}

	}

	class CheckPanelRenderer extends JPanel implements TreeCellRenderer {
		JLabel label;
		JCheckBox checkBox;
		protected boolean selected;
		protected boolean hasFocus;
		public boolean drawsFocusBorderAroundIcon;
		public boolean drawDashedFocusIndicator;
		private Color treeBGColor;
		private Color focusBGColor;
		transient protected Icon closedIcon;
		transient protected Icon leafIcon;
		transient protected Icon openIcon;
		protected Color backgroundSelectionColor;
		protected Color backgroundNonSelectionColor;
		protected Color borderSelectionColor;

		public CheckPanelRenderer() {
			loadDefaults();
			label = new JLabel();
			checkBox = new JCheckBox();
			checkBox.setBorder(null);
			checkBox.setBackground(getBackgroundNonSelectionColor());
			setBorder(null);
			setBackground(getBackgroundNonSelectionColor());
			add(label);
			add(checkBox);
		}

		private void loadDefaults() {
			setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
			setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
			setOpenIcon(UIManager.getIcon("Tree.openIcon"));
			setBackgroundSelectionColor(UIManager.getColor(
			"Tree.selectionBackground"));
			setBackgroundNonSelectionColor(UIManager.getColor(
			"Tree.textBackground"));
			setBorderSelectionColor(UIManager.getColor(
			"Tree.selectionBorderColor"));
			Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
			drawsFocusBorderAroundIcon =
				(value != null && ((Boolean)value).booleanValue());
			value = UIManager.get("Tree.drawDashedFocusIndicator");
			drawDashedFocusIndicator =
				(value != null && ((Boolean)value).booleanValue());
		}

		public void setClosedIcon(Icon newIcon) {
			closedIcon = newIcon;
		}

		public Icon getClosedIcon() { return closedIcon; }

		public void setLeafIcon(Icon newIcon) {
			leafIcon = newIcon;
		}

		public Icon getLeafIcon() {	return leafIcon; }

		public void setOpenIcon(Icon newIcon) {
			openIcon = newIcon;
		}

		public Icon getOpenIcon() { return openIcon; }

		public void setBackgroundSelectionColor(Color newColor) {
			backgroundSelectionColor = newColor;
		}

		public Color getBackgroundSelectionColor() {
			return backgroundSelectionColor;
		}

		public void setBackgroundNonSelectionColor(Color newColor) {
			backgroundNonSelectionColor = newColor;
			treeBGColor = newColor;
		}

		public Color getBackgroundNonSelectionColor() {
			return backgroundNonSelectionColor;
		}

		public void setBorderSelectionColor(Color newColor) {
			borderSelectionColor = newColor;
		}

		public Color getBorderSelectionColor() {
			return borderSelectionColor;
		}

		public Component getTreeCellRendererComponent(JTree tree,
				Object value,
				boolean selected,
				boolean expanded,
				boolean leaf,
				int row,
				boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			CheckStore cs = (CheckStore)node.getUserObject();
			label.setText(cs.toString());
			checkBox.setSelected(cs.getState());

			this.selected = selected;
			this.hasFocus = hasFocus;
			if(leaf) {
				label.setIcon(getLeafIcon());
			} else if(expanded) {
				label.setIcon(getOpenIcon());
			} else {
				label.setIcon(getClosedIcon());
			}
			return this;
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Color bColor;
			if(selected) {
				bColor = getBackgroundSelectionColor();
			} else {
				bColor = getBackgroundNonSelectionColor();
				if (bColor == null) {
					bColor = getBackground();
				}
			}

			int imageOffset = -1;
			if(bColor != null) {
				Icon currentI = label.getIcon();

				imageOffset = getLabelStart();
				g.setColor(bColor);
				g.fillRect(imageOffset, 0, getWidth() - imageOffset,
						getHeight());
			}

			if(hasFocus) {
				if(drawsFocusBorderAroundIcon) {
					imageOffset = 0;
				} else if(imageOffset == -1) {
					imageOffset = getLabelStart();
				}
				paintFocus(g, imageOffset, 0, getWidth() - imageOffset,
						getHeight(), bColor);
			}
		}

		private void paintFocus(Graphics g, int x, int y,
				int w, int h, Color notColor) {
			Color bsColor = getBorderSelectionColor();

			if (bsColor != null && (selected || !drawDashedFocusIndicator)) {
				g.setColor(bsColor);
				g.drawRect(x, y, w - 1, h - 1);
			}
			if (drawDashedFocusIndicator && notColor != null) {
				if (treeBGColor != notColor) {
					treeBGColor = notColor;
					focusBGColor = new Color(~notColor.getRGB());
				}
				g.setColor(focusBGColor);
				BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
			}
		}

		public int getLabelStart() {
			Icon currentI = label.getIcon();
			if(currentI != null && label.getText() != null) {
				return currentI.getIconWidth() +
				Math.max(0, label.getIconTextGap() - 1) + getHgap();
			}
			return 0;
		}

		public int getIconTextGap() {
			return label.getIconTextGap();
		}

		public int getHgap() {
			return ((FlowLayout)getLayout()).getHgap();
		}
	}

	public static class CheckStore {
		private Lego lego;
		boolean state;

		public CheckStore(Lego l, boolean state) {
			this.lego = l;
			this.state = state;
		}

		public boolean getState() { return state; }

		public String toString()
		{
			if(lego == null)
			{
				return "";
			} else
			{
				return lego.getName() + " Show:";
			}
		}
		public Lego getLego()
		{
			return lego;
		}
	}
}
