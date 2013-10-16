package co.edu.unal.colswe.CommitSummarizer.core.git;

import java.io.File;

@SuppressWarnings("rawtypes")
public class ChangedFile implements Comparable {
	
	
	private String path;
	private String changeType;
	private String absolutePath;
	private String name;
	private TypeChange typeChange;
	
	public ChangedFile() {
		// TODO Auto-generated constructor stub
	}
	
	public ChangedFile(String path, String changeType, String rootPath) {
		super();
		this.path = path;
		this.changeType = changeType;
		this.name = new File(path).getName();
		this.absolutePath = rootPath + System.getProperty("file.separator") + (new File(path).getPath());
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getChangeType() {
		return changeType;
	}


	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	/**
     * Type changes supported by git
     * 
     * @author Beat Fluri
     */
    public static enum TypeChange {
    	ADDED("ADDED"), MODIFIED("MODIFIED"), UNTRACKED("UNTRACKED"), REMOVED("REMOVED"), ADDED_INDEX_DIFF("ADDED_INDEX_DIFF"), REMOVED_NOT_STAGED("REMOVED_NOT_STAGED"), REMOVED_UNTRACKED("REMOVED_UNTRACKED");
    	
    	private TypeChange(String type) { 
        	
        }
    }

	@Override
	public String toString() {
		//return "ChangedFile [path=" + path + ", changeType=" + changeType + "]";
		return "" + changeType + " - " + path;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	@Override
	public int compareTo(Object o2) {
		return this.getPath().compareTo(((ChangedFile) o2).getPath());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeChange getTypeChange() {
		return typeChange;
	}

	public void setTypeChange(TypeChange typeChange) {
		this.typeChange = typeChange;
	}
    
    

}
