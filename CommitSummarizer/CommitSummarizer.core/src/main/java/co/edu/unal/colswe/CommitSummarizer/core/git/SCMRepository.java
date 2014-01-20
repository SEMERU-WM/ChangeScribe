package co.edu.unal.colswe.CommitSummarizer.core.git;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

import co.edu.unal.colswe.CommitSummarizer.core.ast.ProjectInformation;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;

public class SCMRepository {
	
	private Git git;
	private Repository repository;

	public SCMRepository() {
		super();
		File file = new File(ProjectInformation.getSelectedProject().getProject().getLocationURI().getPath().toString());
		try {
			git = Git.open(file);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				git = Git.open(file.getParentFile());
			} catch (IOException e) {
				e.printStackTrace();
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
	
}
