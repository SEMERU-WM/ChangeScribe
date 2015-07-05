package co.edu.unal.colswe.changescribe.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.git.GitException;
import co.edu.unal.colswe.changescribe.core.git.RepositoryHistory;
import co.edu.unal.colswe.changescribe.core.git.SCMRepository;
import co.edu.unal.colswe.changescribe.core.summarizer.SummarizeChanges;

public class Main {
	
	private static final String REPOSITORY = "repository";
	private static final String OUTPUT = "output";
	private static final String FILTER_FACTOR = "filterFactor";
	private static final String OLDER_VERSION_ID = "olderVersionId";
	private static final String NEWER_VERSION_ID= "newerVersionId";
	
	private static SCMRepository repo ;
	private static Set<ChangedFile> differences;
	private static Git git;
	private static String projectPath = "/home/fernando/git/test/";
	private static String[] parameters = {"repository", "output", "filterFactor", "olderVersionId", "newerVersionId"};
	private static String olderVersionId;
	private static String newerVersionId;
	private static String outputFile;
	private static double filterFactor; 
	
	private static boolean readParams(String[] args) {
		boolean isValid = true;
		for (String string : args) {
			isValid = validateParam(string);
			if(Boolean.FALSE == isValid) {
				break;
			} else {
				assignValue(string);
			}
		}
		
		return isValid;
	}
	
	private static void assignValue(String string) {
		String [] param = string.split(Constants.EQUAL);
		if(param[0].equals(REPOSITORY)) {
			projectPath = param[1];
		}
		
		if(param[0].equals(OUTPUT)) {
			outputFile = param[1];
		}
		
		if(param[0].equals(FILTER_FACTOR)) {
			try {
				filterFactor = Double.parseDouble(param[1]);
			} catch (NumberFormatException e) {
		        System.err.format("Argument %s must be a double value", param[0]);
		        System.exit(1);
		    }
		}
		
		if(param[0].equals(NEWER_VERSION_ID)) {
			newerVersionId = param[1];
		}
		
		if(param[0].equals(OLDER_VERSION_ID)) {
			olderVersionId = param[1];
		}
	}

	private static boolean validateParam(String string) {
		boolean isValid = true;
		List<String> params = Arrays.asList(parameters);
		String []split = string.split(Constants.EQUAL);
		
		if(!string.contains(Constants.EQUAL)) {
			System.err.println("The parameter format should be contains the =");
			isValid = false;
		} 
		
		if(null != split && split.length != 2) {
			System.err.println("The parameter format should be param=value");
			isValid = false;
		}
		
		if(split[0] != null && !params.contains(split[0])) {
			System.err.format("The parameter %s is not valid", split[0]);
			isValid = false;
		}
		
		return isValid;
	}

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
			System.err.println("Git repository not found!");
			return org.eclipse.core.runtime.Status.CANCEL_STATUS;
		}
		return org.eclipse.core.runtime.Status.OK_STATUS;
	}

	public static void main(String[] args) {
		if(null == args || args.length == 0 || !readParams(args)) {
			System.err.println("Error in the input parameters");
			return;
		}
		try {
			repo = new SCMRepository(projectPath);
			
			gettingRepositoryStatus();
			
			RepositoryHistory.getRepositoryHistory(git);
			
			SummarizeChanges summarizer = new SummarizeChanges(git, false, filterFactor, olderVersionId, newerVersionId);
			summarizer.setProjectPath(projectPath);
			if(null != differences && differences.size() > 0) {
				ChangedFile [] changes = new ChangedFile[differences.size()];
				summarizer.summarize(differences.toArray(changes));
			}
			File output = new File(outputFile);
			FileUtils.writeStringToFile(output, summarizer.getSummary());
		} catch (RuntimeException e1) {
			System.err.println("Not found a repository in the path " + projectPath);
		} catch (IOException e) {
			System.err.println("The output file can not be created in " + outputFile);
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
