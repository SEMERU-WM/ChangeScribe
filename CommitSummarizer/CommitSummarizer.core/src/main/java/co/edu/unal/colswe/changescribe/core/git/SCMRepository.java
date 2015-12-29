package co.edu.unal.colswe.changescribe.core.git;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

import co.edu.unal.colswe.changescribe.core.ast.ProjectInformation;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile.TypeChange;

public class SCMRepository {
	
	private Git git;
	private Repository repository;
	private String projectPath;

	public SCMRepository(String projectPath) throws RuntimeException {
		super();
		this.projectPath = projectPath;
		
		IResource project = null;
		try {
			 project = ProjectInformation.getSelectedProject();
		} catch(NoClassDefFoundError e) {
			System.out.println("you did not select a java project");
		}
		if(project !=  null) {
			openRepository(ProjectInformation.getSelectedProject()
					.getProject().getLocationURI().getPath().toString());
		} else {
			if(null != projectPath && !projectPath.isEmpty()) {
				openRepository(projectPath);
			} else {
				throw new RuntimeException("You did not select a Java project");
			}
		}
	}
	
	public void checkout(String versionID) {
		if(null != git) {
			try {
				if(!versionID.contains("HEAD")) { 
					git.checkout().setName(versionID).call();
				} else {
					git.checkout().setStartPoint(versionID).call();
				}
			} catch (GitAPIException e) {
				e.printStackTrace();
				throw new GitException("Can't checkout to this version: " + versionID);
			}
		}
	}

	private void openRepository(String path) {
		File file = new File(path);
		try {
			git = Git.open(file);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null == git) {
				try {
					git = Git.open(file.getParentFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	public Git getGit() {
		return git;
	}
	
	public Status getStatus() throws NoWorkTreeException, GitAPIException {
		if(git == null) {
			throw new GitException("No share this project with Git");
		}
		return git.status().call();
	}
	
	public static Set<ChangedFile> getDifferences(Status repositoryStatus, String rootPath) {
		Set<ChangedFile> differences = new TreeSet<ChangedFile>();
		
		for (String string : repositoryStatus.getModified()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.MODIFIED.name(), rootPath);
			differences.add(changedFile);
			changedFile.setTypeChange(TypeChange.MODIFIED);
		}
		
		for (String string : repositoryStatus.getAdded()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.ADDED.name(), rootPath);
			differences.add(changedFile);
			changedFile.setTypeChange(TypeChange.ADDED);
		}
		
		for (String string : repositoryStatus.getUntracked()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.UNTRACKED.name(), rootPath);
			differences.add(changedFile);
			System.out.println("path: " + changedFile.getPath());
			changedFile.setTypeChange(TypeChange.UNTRACKED);
		}
		
		for	(String string : repositoryStatus.getRemoved()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.REMOVED.name(), rootPath);
			differences.add(changedFile);
			changedFile.setTypeChange(TypeChange.REMOVED);
		}
		
		return differences;
	}
	
	public static Set<ChangedFile> getRemovedFiles(Status repositoryStatus, String rootPath) {
		Set<ChangedFile> differences = new TreeSet<ChangedFile>();
		for	(String string : repositoryStatus.getRemoved()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.REMOVED.name(), rootPath);
			differences.add(changedFile);
			changedFile.setTypeChange(TypeChange.REMOVED);
		}
		return differences;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
}
