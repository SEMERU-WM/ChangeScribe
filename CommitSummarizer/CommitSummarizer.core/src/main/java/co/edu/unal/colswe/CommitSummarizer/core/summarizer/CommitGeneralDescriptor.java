package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.CommitSummarizer.core.util.Utils;

public class CommitGeneralDescriptor {

	private Git git; 
	private ChangedFile[] differences;
	private SortedMap<String, ChangedFile> newModules;
	private LinkedList<ChangedFile> newProperties;
	private LinkedList<ChangedFile> renames;
	private boolean isInitialCommit;
	
	public CommitGeneralDescriptor() {
		
	}
	
	public String describe() {
		extractNewModules();
		extractInternationalization();
		extractNewFeatures();
		extractRenames();
		
		StringBuilder descriptionBuilder = new StringBuilder(" This commit ");
		
		if(!isInitialCommit && newModules != null && newModules.size() > 0) {
			describeNewModules(descriptionBuilder);
			descriptionBuilder.append(", and ");
		}
		if(newProperties != null && newProperties.size() > 0) {
			describeProperties(descriptionBuilder);
		}
		if(renames != null && renames.size() > 0) {
			describeRenamed(descriptionBuilder);
		}
		return descriptionBuilder.toString();
	}
	
	/**
	 * Extract newed or removed modules
	 * 
	 */
	public void extractNewModules() {
		Status repositoryStatus = null;
		try {
			repositoryStatus = git.status().call();
		} catch (NoWorkTreeException | GitAPIException e) {
			e.printStackTrace();
		}
		newModules = new TreeMap<String, ChangedFile>();		
		for (String string : repositoryStatus.getUntrackedFolders()) {
			for(ChangedFile file : differences) {
				if(file.getPath().substring(0, file.getPath().lastIndexOf("/")).equals(string) && !newModules.containsKey(string)) {
					ChangedFile changedFile = new ChangedFile(string, TypeChange.UNTRACKED_FOLDERS.name(), git.getRepository().getWorkTree().getAbsolutePath());
					newModules.put(string, changedFile);
					break;
				}
			}
		}
	}
	
	/**
	 * Generate description for new modules added.
	 * 
	 * @param descriptionBuilder
	 */
	public void describeNewModules(StringBuilder descriptionBuilder) {
		descriptionBuilder.append("adds ");
		 
		if(newModules.entrySet().size() == 1) {
			descriptionBuilder.append("new package ");
		} else {
			descriptionBuilder.append("new packages ");
		}
		int i = 1;
		for(Entry<String, ChangedFile> entry : newModules.entrySet()) {
			descriptionBuilder.append(entry.getValue().getName());
			if(newModules.entrySet().size() > 1 && i < newModules.entrySet().size() - 2) {
				descriptionBuilder.append(", ");
			} else if(newModules.entrySet().size() > 1 && i == newModules.entrySet().size() - 1) {
				descriptionBuilder.append(" and ");
			}
			i++;
		}
	}
	
	/**
	 * Describe internationalization files added or modified.
	 * 
	 * @param descriptionBuilder
	 */
	public void describeProperties(StringBuilder descriptionBuilder) {
		if(newProperties == null && newProperties.size() == 0) {
			return;
		}
		StringBuilder propsBuilder = new StringBuilder();
		
		int i = 0;
		for(ChangedFile file : newProperties) {
			if(!propsBuilder.toString().contains(file.getChangeTypeToShow(true).toLowerCase())) {
				if(i > 0) {
					propsBuilder.append(", ");
				}
				propsBuilder.append(file.getChangeTypeToShow(true).toLowerCase());
				i++;
			}
		}
		descriptionBuilder.append(" " + propsBuilder.toString());
		descriptionBuilder.append(" internationalization, properties or configuration files");
		descriptionBuilder.append(". ");
		
	}
	
	/**
	 * Extract if new internationalization files were added.
	 * 
	 */
	public void extractInternationalization() {
		newProperties = new LinkedList<>();
		
		for(ChangedFile changedFile : differences) {
			if(changedFile.getName().endsWith(".xml") || changedFile.getName().endsWith(".properties")
					|| changedFile.getName().endsWith(".cfg")
					|| changedFile.getName().startsWith(".")) {
				newProperties.add(changedFile);
			}
		}
	}
	
	/**
	 * Extract if any files were renamed.
	 * 
	 */
	public void extractRenames() {
		renames = new LinkedList<>();
		
		for(ChangedFile file : differences) {
			if(file.getTypeChange() == TypeChange.MODIFIED) {
				try {
					Utils.compareModified(file, git);
				} catch(IllegalStateException ex) {
					renames.add(file);
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Describe renamed files.
	 * 
	 * @param descriptionBuilder
	 */
	public void describeRenamed(StringBuilder descriptionBuilder) {
		descriptionBuilder.append(" Rename some files.");
	}
	
	public void extractNewFeatures() {
		
	}
	
	public Git getGit() {
		return git;
	}

	public void setGit(Git git) {
		this.git = git;
	}

	public ChangedFile[] getDifferences() {
		return differences;
	}

	public void setDifferences(ChangedFile[] differences) {
		this.differences = differences;
	}

	public LinkedList<ChangedFile> getRenames() {
		return renames;
	}

	public void setRenames(LinkedList<ChangedFile> renames) {
		this.renames = renames;
	}

	public boolean isInitialCommit() {
		return isInitialCommit;
	}

	public void setInitialCommit(boolean isInitialCommit) {
		this.isInitialCommit = isInitialCommit;
	}
}
