package view.partsbrowser;

import java.io.File;

public class LDrawFileInfo implements Comparable{
	private File file;
	private String description;

	public LDrawFileInfo(File file, String description){
		this.file = file;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}

	public int compareTo(Object o) {
		LDrawFileInfo other = (LDrawFileInfo)o;
		return (description.compareTo(other.getDescription()));
	}
}
