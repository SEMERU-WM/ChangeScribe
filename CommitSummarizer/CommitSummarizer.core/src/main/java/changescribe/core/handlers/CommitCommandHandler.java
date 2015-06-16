package changescribe.core.handlers;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.api.Git;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import co.edu.unal.colswe.changescribe.core.Messages;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.git.SCMRepository;
import co.edu.unal.colswe.changescribe.core.ui.FilesChangedListDialog;
import co.edu.unal.colswe.changescribe.core.util.UIUtils;

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
		IJavaProject javaProject = changescribe.core.handlers.HandlerUtil.getJavaProject(selection.getFirstElement());
		try {
			repo = changescribe.core.handlers.HandlerUtil.createRepository();
			git = repo.getGit();
			differences = changescribe.core.handlers.HandlerUtil.initMonitorDialog(selection, git, repo, window);
			changescribe.core.handlers.HandlerUtil.openMonitorDialog(new FilesChangedListDialog(window.getShell(), differences, git, javaProject));
		} catch (final RuntimeException e) {
			UIUtils.showInformationWindow(window, Messages.INFORMATION, e.getMessage());
		}
		return null;
	}

}
