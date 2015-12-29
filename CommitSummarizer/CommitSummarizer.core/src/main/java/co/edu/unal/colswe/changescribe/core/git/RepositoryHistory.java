package co.edu.unal.colswe.changescribe.core.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class RepositoryHistory {
	
	public static List<CommitWrapper> getRepositoryHistory(Git git) throws NoHeadException, GitAPIException, MissingObjectException, IncorrectObjectTypeException, IOException {
		RevWalk walk = new RevWalk(git.getRepository());
		
		RevCommit commit = null;
		List<CommitWrapper> commits = new ArrayList<>(); 

		Iterable<RevCommit> logs = git.log().call();
		Iterator<RevCommit> i = logs.iterator();

		while (i.hasNext()) {
		    commit = walk.parseCommit( i.next());
		    if(null != commit ) {
		    	CommitWrapper commitWrapper = new CommitWrapper(commit);
		    	commits.add(commitWrapper);
		    }
		}
		
		return commits;
	}
	
	public static void main(String[] args) {
		
	}

}
