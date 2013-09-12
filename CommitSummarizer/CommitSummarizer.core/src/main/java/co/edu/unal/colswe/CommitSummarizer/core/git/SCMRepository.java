package co.edu.unal.colswe.CommitSummarizer.core.git;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
		return git.status().call();
	}
	
	public static Set<ChangedFile> getDifferences(Status repositoryStatus, String rootPath) {
		Set<ChangedFile> differences = new HashSet<ChangedFile>();
		
		for (String string : repositoryStatus.getModified()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.MODIFIED.name(), rootPath);
			differences.add(changedFile);
		}
		
		for (String string : repositoryStatus.getAdded()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.ADDED.name(), rootPath);
			differences.add(changedFile);
		}
		
		for (String string : repositoryStatus.getUntracked()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.UNTRACKED.name(), rootPath);
			differences.add(changedFile);
		}
		
		return differences;
	}
	
}
