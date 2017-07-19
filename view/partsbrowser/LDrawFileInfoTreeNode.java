package view.partsbrowser;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class LDrawFileInfoTreeNode extends DefaultMutableTreeNode {

	LDrawFileInfo ldfInfo = null;

	//requires: ldfInfo is not null
	public LDrawFileInfoTreeNode(LDrawFileInfo ldfInfo){
		super(ldfInfo.getDescription());

		this.ldfInfo = ldfInfo;
	}

	public LDrawFileInfoTreeNode(String description){
		super(description);

		this.ldfInfo = new LDrawFileInfo(null,description);
	}

	public LDrawFileInfo getLDrawFileInfo(){
		return ldfInfo;
	}

	public String toString(){
		return ldfInfo.getDescription();
	}

}
