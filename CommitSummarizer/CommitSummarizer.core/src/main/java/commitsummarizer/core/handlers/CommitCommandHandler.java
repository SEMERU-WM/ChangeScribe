package commitsummarizer.core.handlers;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import co.edu.unal.colswe.CommitSummarizer.core.Activator;
import co.edu.unal.colswe.CommitSummarizer.core.FilesChangedListDialog;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.git.GitException;
import co.edu.unal.colswe.CommitSummarizer.core.git.SCMRepository;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CommitCommandHandler extends AbstractHandler {
	
	private IWorkbenchWindow window;
	private Git git;
	private Set<ChangedFile> differences;
	private SCMRepository repo;
	
	/**
	 * The constructor.
	 */
	public CommitCommandHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		try {
			repo = new SCMRepository();
			initMonitorDialog(selection);
		} catch (final RuntimeException e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(window.getShell(), "Information", e.getMessage());
				}});
		}
		return null;
	}
	
	private void initMonitorDialog(final IStructuredSelection selection) {
        final Job job = new Job("JSummarizer - Summarizing types") {
            protected IStatus run(final IProgressMonitor monitor) {
            	IStatus status = gettingRepositoryStatus(monitor);
            	if(status.equals(org.eclipse.core.runtime.Status.OK_STATUS)) {
            		createDialog(getJavaProject(selection.getFirstElement()));
            	}
            	return status;
            }
        };
        job.schedule();
	        
	}

	private void createDialog(final IJavaProject selection) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				FilesChangedListDialog listDialog = new FilesChangedListDialog(window.getShell(), differences, git, selection);
				listDialog.create();
				listDialog.open();
				Activator.getDefault().setFilesChangedListDialog(listDialog);
			}
		});
	}
	
	private IJavaProject getJavaProject(Object selected) {
		IJavaProject project = null;
		if(selected instanceof IMember) {
			project = ((IMember)selected).getJavaProject();
		} else if(selected instanceof IJavaProject) {
			project = ((IJavaProject)selected).getJavaProject();
		} else if(selected instanceof ICompilationUnit) {
			project = ((ICompilationUnit)selected).getJavaProject();
		} else if(selected instanceof IJavaProject) {
			project = ((IJavaProject)selected).getJavaProject();
		} else if(selected instanceof IPackageFragment) {
			project = ((IPackageFragment)selected).getJavaProject();
		}  else if(selected instanceof IPackageFragmentRoot) {
			project = ((IPackageFragmentRoot)selected).getJavaProject();
		} 
		return project;
	}

	private IStatus gettingRepositoryStatus(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
	        return org.eclipse.core.runtime.Status.CANCEL_STATUS;
	    }
		monitor.beginTask("Getting status for git repository ", 1);
		
		git = repo.getGit();
		
		if(git != null) {
			Status status = null;
			try {
				status = repo.getStatus();
			} catch (NoWorkTreeException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			} catch (final GitException e) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openInformation(window.getShell(), "Information", e.getMessage());
					}});
			}
			
			monitor.beginTask("Extracting source code differences ", 2);
			this.differences = SCMRepository.getDifferences(status,git.getRepository().getWorkTree().getAbsolutePath());
			
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(window.getShell(), "Information", "Git repository not found!");
			}});
			return org.eclipse.core.runtime.Status.CANCEL_STATUS;
		}
		return org.eclipse.core.runtime.Status.OK_STATUS;
	}
}
