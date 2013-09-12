package co.edu.unal.colswe.CommitSummarizer.core.git;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.CommitSummarizer.core.util.Utils;

public class SCMRepository {
	
	private Git git;
	private Repository repository;
	private IProject project;

	public SCMRepository() {
		super();
		File file = new File(getSelectedProject().getLocationURI().getPath().toString());
		try {
			git = Git.open(file);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				git = Git.open(file.getParentFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public IResource getSelectedProject() {
		IWorkbench iworkbench = PlatformUI.getWorkbench();
		IWorkbenchWindow iworkbenchwindow = null;
		IWorkbenchPage iworkbenchpage = null;
		if (iworkbench != null) {
			iworkbenchwindow = iworkbench.getActiveWorkbenchWindow();
		} 
		if (iworkbenchwindow != null) {
			iworkbenchpage = iworkbenchwindow.getActivePage();
		}
		ISelection selection = iworkbenchpage.getSelection();
	   //the current selection in the navigator view
	   
		return extractSelection(selection);
	}
	
	public static IResource extractSelection(ISelection sel) {
		if (!(sel instanceof IStructuredSelection))
			return null;
		IStructuredSelection ss = (IStructuredSelection) sel;
		Object element = ss.getFirstElement();
		if (element instanceof IResource)
			return (IResource) element;
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return (IResource) adapter;
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	public Git getGit() {
		return git;
	}
	
	public Status getStatus() throws NoWorkTreeException, GitAPIException {
		return git.status().call();
	}
	
	public static Set<ChangedFile> getDifferences(Status repositoryStatus, String rootPath) {
		Set<ChangedFile> differences = new HashSet<ChangedFile>();
		
		for (String string : repositoryStatus.getModified()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.MODIFIED.name(), rootPath);
			differences.add(changedFile);
		}
		
		for (String string : repositoryStatus.getAdded()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.ADDED.name(), rootPath);
			differences.add(changedFile);
		}
		
		for (String string : repositoryStatus.getUntracked()) {
			ChangedFile changedFile = new ChangedFile(string, TypeChange.UNTRACKED.name(), rootPath);
			differences.add(changedFile);
		}
		
		return differences;
	}
	
	public static File getFileContentOfLastCommit(String filePath,Repository repository) throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		// find the HEAD
		ObjectId lastCommitId = repository.resolve(Constants.HEAD);

		// a RevWalk allows to walk over commits based on some filtering that is
		// defined
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(lastCommitId);
		// and using commit's tree find the path
		RevTree tree = commit.getTree();
		System.out.println("Having tree: " + tree);

		// now try to find a specific file
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create(filePath));
		if (!treeWalk.next()) {
			
			//TODO the file is added to project
			throw new IllegalStateException(
					"CHANGECOMMIT -- Did not find expected file '" + filePath + "'");
		}

		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repository.open(objectId);

		return Utils.inputStreamToFile(loader.openStream());
	}

	
}
