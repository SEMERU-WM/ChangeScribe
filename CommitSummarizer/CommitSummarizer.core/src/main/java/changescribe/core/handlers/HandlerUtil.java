package changescribe.core.handlers;

import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;

import co.edu.unal.colswe.changescribe.core.Messages;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.git.GitException;
import co.edu.unal.colswe.changescribe.core.git.SCMRepository;
import co.edu.unal.colswe.changescribe.core.util.UIUtils;

public class HandlerUtil {
	
	public static SCMRepository createRepository() {
			return new SCMRepository(null);
	}
	
	public static Set<ChangedFile> initMonitorDialog(final IStructuredSelection selection, final Git git, final SCMRepository repo, 
			final IWorkbenchWindow window) {
				Set<ChangedFile> differences = null;
				differences = gettingRepositoryStatus(git, repo, window);
            	return differences;
	}
	
	public static void openMonitorDialog(final TitleAreaDialog dialog) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				dialog.create();
				dialog.open();
			}
		});
	}
	
	private static Set<ChangedFile> gettingRepositoryStatus(Git git, SCMRepository repo, final IWorkbenchWindow window) {
		git = repo.getGit();
		Status status = null;
		Set<ChangedFile> differences = null;
		
		if(git != null) {
			try {
				status = repo.getStatus();
			} catch (NoWorkTreeException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			} catch (final GitException e) {
				UIUtils.showInformationWindow(window, Messages.INFORMATION, e.getMessage());
			}
			differences = SCMRepository.getDifferences(status,git.getRepository().getWorkTree().getAbsolutePath());
		} else {
			UIUtils.showInformationWindow(window, Messages.INFORMATION, Messages.HandlerUtil_RepositoryNotFound);
		}
		return differences;
	}

	public static IJavaProject getJavaProject(Object selected) {
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

}
