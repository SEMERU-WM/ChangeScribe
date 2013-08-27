package unal.edu.co;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import unal.edu.co.repository.git.SCMRepository;

public class CommitGenerator {

	public static void main(String[] args) throws NoWorkTreeException, GitAPIException {
		
		SCMRepository repo = new SCMRepository();
		Git git = repo.getGit();
		
		Status status = repo.getStatus();
		
		for (String string : status.getModified()) {
			System.out.println("Modified File: " + string);
		}
		
		for (String string : status.getAdded()) {
			System.out.println("Added File: " + string);
		}
		
		for (String string : status.getUntracked()) {
			System.out.println("Untracked File: " + string);
		}

	}

}
