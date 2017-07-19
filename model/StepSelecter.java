package model;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;

import view.Scene;

public class StepSelecter extends JPanel implements Scene.LegoSelectionListener{

	private Scene renderer;
	private JList list;
	public StepSelecter(Scene s)
	{
		renderer = s;
		list = new JList();
	}
	public void legoSelected(Lego l) {
		list.removeAll();

		int maxStep = l.getMaxStep();
		for(int i = 0; i < maxStep; i++)
		{
			DefaultListModel model = (DefaultListModel)list.getModel();
			model.addElement("Step: " + i);
		}
	}

}
