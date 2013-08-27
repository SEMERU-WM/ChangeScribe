package unal.edu.co.repository.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

public class SCMRepository {
	
	private Git git;
	private Repository repository;

	public SCMRepository() {
		super();
		try {
			git = Git.open(new File(""));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
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

	
}
