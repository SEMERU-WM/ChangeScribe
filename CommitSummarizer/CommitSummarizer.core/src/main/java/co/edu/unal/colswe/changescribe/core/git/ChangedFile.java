package co.edu.unal.colswe.changescribe.core.git;

import java.io.File;
import java.util.List;

import co.edu.unal.colswe.changescribe.core.Constants;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;

@SuppressWarnings("rawtypes")
public class ChangedFile implements Comparable {
	private String path;
	private String changeType;
	private String absolutePath;
	private String name;
	private TypeChange typeChange;
	private boolean isRenamed;
	private String renamedPath;
	private List<StructureEntityVersion> modifiedMethods;
	
	public ChangedFile() {
		// TODO Auto-generated constructor stub
	}
	
	public ChangedFile(String path, String changeType, String rootPath) {
		super();
		this.path = path;
		this.changeType = changeType;
		this.name = new File(path).getName();
		
		if(null == this.name || name.equals("null")) {
			this.name = path.substring(path.lastIndexOf(System.getProperty("file.separator")) + 1);
		}
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
	
	public String getChangeTypeToShow(boolean thirdPerson) {
		String type = getChangeType();
		if(!thirdPerson && changeType.equals(TypeChange.UNTRACKED_FOLDERS.toString()) || !thirdPerson && changeType.equals(TypeChange.ADDED.toString()) || !thirdPerson && changeType.equals(TypeChange.UNTRACKED.toString())) {
			type = Constants.ADD;
		} else if(thirdPerson && changeType.equals(TypeChange.UNTRACKED_FOLDERS.toString()) || thirdPerson && changeType.equals(TypeChange.ADDED.toString())  || changeType.equals(TypeChange.UNTRACKED.toString())) { 
			type = Constants.ADDS;
		} else if(!thirdPerson && changeType.equals(TypeChange.MODIFIED.toString())) {
			type = Constants.MODIFY;
		} else if(thirdPerson && changeType.equals(TypeChange.MODIFIED.toString())) {
			type = Constants.MODIFIES;
		} else if(!thirdPerson && changeType.equals(TypeChange.REMOVED.toString())) {
			type = Constants.REMOVE;
		} else if(thirdPerson && changeType.equals(TypeChange.REMOVED.toString())) {
			type = Constants.REMOVES;
		}
		return type;
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
    	ADDED("ADDED"), MODIFIED("MODIFIED"), UNTRACKED("UNTRACKED"), REMOVED("REMOVED"), 
    	ADDED_INDEX_DIFF("ADDED_INDEX_DIFF"), REMOVED_NOT_STAGED("REMOVED_NOT_STAGED"), 
    	REMOVED_UNTRACKED("REMOVED_UNTRACKED"), UNTRACKED_FOLDERS("UNTRACKED_FOLDERS");
    	
    	private TypeChange(String type) { 
        	
        }
    }

	@Override
	public String toString() {
		return Constants.EMPTY_STRING + changeType + " - " + path;
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
		if(null == this.name || name.equals("null")) {
			this.name = path.substring(path.lastIndexOf(System.getProperty("file.separator")) + 1);
		}
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

	public boolean isRenamed() {
		return isRenamed;
	}

	public void setRenamed(boolean isRenamed) {
		this.isRenamed = isRenamed;
	}

	public String getRenamedPath() {
		return renamedPath;
	}

	public void setRenamedPath(String renamedPath) {
		this.renamedPath = renamedPath;
	}

	public List<StructureEntityVersion> getModifiedMethods() {
		return modifiedMethods;
	}

	public void setModifiedMethods(List<StructureEntityVersion> modifiedMethods) {
		this.modifiedMethods = modifiedMethods;
	}
    
}
