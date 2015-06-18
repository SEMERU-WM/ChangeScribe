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
import co.edu.unal.colswe.changescribe.core.ui.DescribeVersionsDialog;
import co.edu.unal.colswe.changescribe.core.util.UIUtils;

public class CompareVersionsHandler extends AbstractHandler {

	private IWorkbenchWindow window;
	private Git git;
	private SCMRepository repo;
	private Set<ChangedFile> differences;
	
	/**
	 * Constructor for Action1.
	 */
	public CompareVersionsHandler() {
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
			changescribe.core.handlers.HandlerUtil.createRepository();
			differences = changescribe.core.handlers.HandlerUtil.initMonitorDialog(selection, git, repo, window);
			changescribe.core.handlers.HandlerUtil.openMonitorDialog(new DescribeVersionsDialog(window.getShell(), differences, git, javaProject));
		} catch (final RuntimeException e) {
			UIUtils.showInformationWindow(window, Messages.INFORMATION, e.getMessage());
		}
		return null;
	}
}
