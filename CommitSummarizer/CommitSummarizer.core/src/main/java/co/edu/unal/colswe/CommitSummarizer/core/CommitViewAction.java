package co.edu.unal.colswe.CommitSummarizer.core;

import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.git.SCMRepository;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class CommitViewAction implements IViewActionDelegate {
	//private IWorkbenchWindow window;
	
	private IViewPart view;
	/**
	 * The constructor.
	 */
	public CommitViewAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		
		SCMRepository repo = new SCMRepository();
		Git git = repo.getGit();
		
		Status status = null;
		try {
			status = repo.getStatus();
		} catch (NoWorkTreeException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} 
		
		if(git != null) {
			Set<ChangedFile> differences = SCMRepository.getDifferences(status,git.getRepository().getWorkTree().getAbsolutePath());
			
			//MyTitleAreaDialog areaDialog = new MyTitleAreaDialog(window.getShell());
			FilesChangedListDialog listDialog = new FilesChangedListDialog(view.getSite().getShell(), differences);
		    
		    
		    listDialog.create();
		    listDialog.open();
		} else {
			MessageDialog.openInformation(view.getSite().getShell(), "Info", "Git repository not found!");
		}
		
		
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		this.view = view;
		
	}
}