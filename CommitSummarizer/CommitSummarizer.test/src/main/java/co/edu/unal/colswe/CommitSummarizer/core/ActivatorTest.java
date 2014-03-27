package co.edu.unal.colswe.CommitSummarizer.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.junit.Before;
import org.junit.Test;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.CommitSummarizer.core.summarizer.SummarizeChanges;
import co.edu.unal.colswe.CommitSummarizer.core.summarizer.SummarizeChangesEngine;

/**
 * Sample integration test. In Eclipse, right-click > Run As > JUnit-Plugin. <br/>
 * In Maven CLI, run "mvn integration-test".
 */
public class ActivatorTest {

	/*
	 * @Test public void veryStupidTest() {
	 * assertEquals("CommitSummarizer.core",Activator.PLUGIN_ID);
	 * assertTrue("Plugin should be started", Activator.getDefault().started); }
	 */

	private Git git;
	private List<ChangedFile> differences;

	@Before
	public void configureEnvironment() {
		String repositoryProject = "/home/fernandocortes/git/elasticsearch/";
		differences = new ArrayList<>();
		File file = new File(repositoryProject);
		try {
			git = Git.open(file);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				git = Git.open(file.getParentFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void getModifiedFilesBetweenTwoVersions() {
		
		String commitCurrentID = "fc6bc4c4776a2f710f57616e3495aaf6a230c4d3";
		String commitPreviousID = "b61ca9932a464628d64b13f3e12f831bdfc5dffa";
		String rootPath = "elasticsearch/";

		ObjectId currentId = null;
		try {
			currentId = git.getRepository()
					.resolve(commitCurrentID + "^{tree}");

			ObjectId oldId = git.getRepository().resolve(
					commitPreviousID + "^{tree}");

			ObjectReader reader = git.getRepository().newObjectReader();

			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, oldId);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, currentId);

			List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
			for (DiffEntry diffEntry : diffs) {
				
				ChangedFile changedFile = new ChangedFile();
				
				String name = (diffEntry.getNewPath() != null) ? getFileName(diffEntry.getNewPath()) : getFileName(diffEntry.getOldPath());
				
				
				changedFile.setName(name);
				changedFile.setAbsolutePath(diffEntry.getNewPath() != null ? rootPath + diffEntry.getNewPath() : rootPath + diffEntry.getOldPath());
				changedFile.setPath(changedFile.getAbsolutePath());
				
				if(diffEntry.getChangeType().name().equals("MODIFY")) {
					changedFile.setChangeType(TypeChange.MODIFIED.name());
					changedFile.setTypeChange(TypeChange.MODIFIED);
				} else if(diffEntry.getChangeType().name().equals("ADD")) {
					changedFile.setChangeType(TypeChange.ADDED.name());
					changedFile.setTypeChange(TypeChange.ADDED);
				} else if(diffEntry.getChangeType().name().equals("REMOVE")) {
					changedFile.setChangeType(TypeChange.REMOVED.name());
					changedFile.setTypeChange(TypeChange.REMOVED);
				}
				differences.add(changedFile);
				System.out.println(changedFile.toString());
			}
		} catch (RevisionSyntaxException | IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SummarizeChangesEngine summarizer = new SummarizeChangesEngine(git);
		
		summarizer.summarize(differences.toArray(new ChangedFile[differences.size()]));
		//System.out.println(summarizer.getDescriptor().toString());

	}
	
	public static String getFileName(String absolutePath) {
		return absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
	}

}