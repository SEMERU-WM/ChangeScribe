package co.edu.unal.colswe.changescribe.core;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.git.SCMRepository;

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
	private Git git;
	private Set<ChangedFile> differences;
	private SCMRepository repo;
	/**
	 * 
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
		
		repo = new SCMRepository();
		initMonitorDialog(action);
		
	}
	
	private void initMonitorDialog(IAction event) {
            final Job job = new Job("ChangeScribe - Summarizing types") {
                protected IStatus run(final IProgressMonitor monitor) {
                	IStatus status = gettingRepositoryStatus(monitor);
                	createDialog();
                	return status;
                }
            };
            job.schedule();
            
    }
	
	private void createDialog() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				FilesChangedListDialog listDialog = new FilesChangedListDialog(view.getSite().getShell(), differences, git, null);
				listDialog.create();
				listDialog.open();
			}
		});
	}
	
	private IStatus gettingRepositoryStatus(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
            return org.eclipse.core.runtime.Status.CANCEL_STATUS;
        }
		monitor.beginTask("Getting status for git repository ", 1);
		
		
		git = repo.getGit();
		Status status = null;
		try {
			status = repo.getStatus();
		} catch (NoWorkTreeException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} 
		
		if(git != null) {
			monitor.beginTask("Extracting source code differences ", 2);
			this.differences = SCMRepository.getDifferences(status,git.getRepository().getWorkTree().getAbsolutePath());
			
		} else {
			MessageDialog.openInformation(view.getSite().getShell(), "Info", "Git repository not found!");
		}
		return org.eclipse.core.runtime.Status.OK_STATUS;
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