package co.edu.unal.colswe.changescribe.core.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.swt.widgets.Shell;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.ast.ProjectInformation;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile.TypeChange;
import edu.stanford.nlp.util.Sets;

public class DescribeVersionsDialogUtil {
	
	public static Set<ChangedFile> computeModifications(Shell shell, Git git, String olderVersionId, String newerVersionId) {
		ObjectId currentId = null;
		Set<ChangedFile> differences = null;
		try {
			currentId = git.getRepository()
					.resolve(newerVersionId + Constants.TREE);

			ObjectId oldId = git.getRepository().resolve(
					olderVersionId + Constants.TREE);

			ObjectReader reader = git.getRepository().newObjectReader(); 

			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			
			if(oldId != null || currentId != null) {
				oldTreeIter.reset(reader, oldId);
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				newTreeIter.reset(reader, currentId);
	
				List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
				differences = new TreeSet<ChangedFile>();
				String projectName = ProjectInformation.getProject(ProjectInformation.getSelectedProject()).getName();
				
				//rename detector
				TreeWalk tw = new TreeWalk(git.getRepository());
				tw.setRecursive(true);
				tw.addTree(oldTreeIter);
				tw.addTree(newTreeIter);
	
				RenameDetector rd = new RenameDetector(git.getRepository());
				rd.addAll(diffs);
	
				List<DiffEntry> lde = rd.compute(tw.getObjectReader(), null);
				
				List<DiffEntry> finalChanges = cleanRenamed(diffs, lde);
				
				for (DiffEntry diffEntry : finalChanges) {
					
					ChangedFile changedFile = new ChangedFile();
					String name = (diffEntry.getNewPath() != null) ? getFileName(diffEntry.getNewPath()) : getFileName(diffEntry.getOldPath());
					changedFile.setName(name);
					changedFile.setAbsolutePath(diffEntry.getNewPath() != null ? projectName + "/" + diffEntry.getNewPath() : projectName + "/" + diffEntry.getOldPath());
					changedFile.setPath(changedFile.getAbsolutePath());
					if(diffEntry.getChangeType().name().equals(Constants.RENAME)) {
						changedFile.setRenamedPath(diffEntry.getOldPath());
						changedFile.setRenamed(true);
						changedFile.setChangeType(TypeChange.ADDED.name());
						changedFile.setTypeChange(TypeChange.ADDED);
					} else	if(diffEntry.getChangeType().name().equals(Constants.MODIFY)) {
						changedFile.setRenamed(false);
						changedFile.setChangeType(TypeChange.MODIFIED.name());
						changedFile.setTypeChange(TypeChange.MODIFIED);
					} else if(diffEntry.getChangeType().name().equals(Constants.ADD)) {
						changedFile.setRenamed(false);
						changedFile.setChangeType(TypeChange.ADDED.name());
						changedFile.setTypeChange(TypeChange.ADDED);
					} else if(diffEntry.getChangeType().name().equals(Constants.REMOVE) || diffEntry.getChangeType().name().equals(Constants.DELETE)) {
						changedFile.setRenamed(false);
						changedFile.setChangeType(TypeChange.REMOVED.name());
						changedFile.setTypeChange(TypeChange.REMOVED);
						
						changedFile.setAbsolutePath(projectName + "/" + diffEntry.getOldPath());
						changedFile.setPath(changedFile.getAbsolutePath());
					} 
					
					if(changedFile.getTypeChange() != null) {
						differences.add(changedFile);
					}
					System.out.println(changedFile.toString());
				}
				Sets.intersection(new HashSet<DiffEntry>(diffs), new HashSet<DiffEntry>(lde));
			} else {
				MessageDialog.openWarning(shell, "Error", "The older or newer commit id is not valid!");
			}
				
			} catch (RevisionSyntaxException | IOException | GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return differences;
	}
	
	private static List<DiffEntry> cleanRenamed(List<DiffEntry> diffs, List<DiffEntry> lde) {
		List<DiffEntry> finalList = new ArrayList<>(diffs);
		int i = 0;
		for (DiffEntry diffEntry : diffs) {
			DiffEntry verifyRenamed = verifyRenamed(diffEntry, lde) ;
			if(verifyRenamed != null) {
				if(!finalList.contains(verifyRenamed)) {
					diffEntry = verifyRenamed;
					finalList.set(i, verifyRenamed);
				} 
			}
			i++;
		}
		
		return finalList;
	}
	
	private static DiffEntry verifyRenamed(DiffEntry diffEntry, List<DiffEntry> lde) {
		DiffEntry renamedEntry = null;
		for (DiffEntry diffEntryRenamed : lde) {
			if(diffEntryRenamed.getScore() >= 90) {
				if(diffEntry.getChangeType().name().equals(Constants.ADD)) {
					if(diffEntry.getNewPath().equals(diffEntryRenamed.getNewPath())) {
						renamedEntry = diffEntryRenamed;
						break;
					}
				} else if(diffEntry.getChangeType().name().equals(Constants.REMOVE)) {
					if(diffEntry.getOldPath().equals(diffEntryRenamed.getOldPath())) {
						renamedEntry = diffEntryRenamed;
						break;
					}
				} else if(diffEntry.getChangeType().name().equals(Constants.RENAME)) {
					if(diffEntry.getOldPath().equals(diffEntryRenamed.getOldPath())) {
						renamedEntry = diffEntryRenamed;
						break;
					}
				}
			}
			
		}
		return renamedEntry;
	}
	
	public static String getFileName(String absolutePath) {
		return absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
	}

}
