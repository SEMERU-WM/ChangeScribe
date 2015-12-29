package co.edu.unal.colswe.changescribe.core.ui.describeversion;

import java.io.IOException;
import java.util.HashSet;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;

import co.edu.unal.colswe.changescribe.core.Messages;
import co.edu.unal.colswe.changescribe.core.git.RepositoryHistory;
import co.edu.unal.colswe.changescribe.core.git.SCMRepository;

public class DescribeTwoVersionsWizard extends Wizard {
	protected HistoryPage one;
	protected GenerateMessagePage two;
	private Git git;
	private SCMRepository repository;
	private IJavaProject selection;

	public DescribeTwoVersionsWizard(Git git, SCMRepository repository, IJavaProject selection) {
		super();
		this.setGit(git);
		this.setRepository(repository);
		this.setSelection(selection);
		
		setNeedsProgressMonitor(true);
	}

	@Override
	public String getWindowTitle() {
		return Messages.FilesChangedListDialog_DescribeChanges;
	}

	@Override
	public void addPages() {
		
		one = new HistoryPage(repository);
		two = new GenerateMessagePage(this.getShell(), new HashSet<>(), this.git, selection);
		try {
			one.setCommits(RepositoryHistory.getRepositoryHistory(git));
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		addPage(one);
		addPage(two); 
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Git getGit() {
		return git;
	}

	public void setGit(Git git) {
		this.git = git;
	}

	public SCMRepository getRepository() {
		return repository;
	}

	public void setRepository(SCMRepository repository) {
		this.repository = repository;
	}

	public IJavaProject getSelection() {
		return selection;
	}

	public void setSelection(IJavaProject selection) {
		this.selection = selection;
	}

}
