package co.edu.unal.colswe.changescribe.core.git;

import org.eclipse.jgit.revwalk.RevCommit;

public class CommitWrapper implements Comparable<CommitWrapper> {
	
	private RevCommit commit;
	
	public CommitWrapper(RevCommit commit) {
		super();
		this.commit = commit;
	}

	public RevCommit getCommit() {
		return commit;
	}

	public void setCommit(RevCommit commit) {
		this.commit = commit;
	}

	@Override
	public int compareTo(CommitWrapper o) {
		return commit.getCommitTime() > o.getCommit().getCommitTime() ? 1 : 0;
	}
}
