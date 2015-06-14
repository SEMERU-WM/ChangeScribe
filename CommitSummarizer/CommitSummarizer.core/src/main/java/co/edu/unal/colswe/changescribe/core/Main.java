package co.edu.unal.colswe.changescribe.core;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.git.GitException;
import co.edu.unal.colswe.changescribe.core.git.SCMRepository;
import co.edu.unal.colswe.changescribe.core.summarizer.SummarizeChanges;

public class Main {
	
	private static SCMRepository repo ;
	private static Set<ChangedFile> differences;
	private static Git git;
	private static String projectPath = "/home/fernando/git/test/";
	
	private static IStatus gettingRepositoryStatus() {
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
				e.printStackTrace();
			}
			
			System.out.println("Extracting source code differences !");
			differences = SCMRepository.getDifferences(status,git.getRepository().getWorkTree().getAbsolutePath());
			
		} else {
			
			System.out.println("Git repository not found!");
			return org.eclipse.core.runtime.Status.CANCEL_STATUS;
		}
		return org.eclipse.core.runtime.Status.OK_STATUS;
	}

	public static void main(String[] args) {
		repo = new SCMRepository(projectPath);
		
		gettingRepositoryStatus();
		
		SummarizeChanges summarizer = new SummarizeChanges(git, false, 0, null, null);
		summarizer.setProjectPath(projectPath);
		if(null != differences && differences.size() > 0) {
			ChangedFile [] changes = new ChangedFile[differences.size()];
			summarizer.summarize(differences.toArray(changes));
		}
		
		File output = new File("/home/fernando/ouput.log");
		try {
			FileUtils.writeStringToFile(output, summarizer.getSummary());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
