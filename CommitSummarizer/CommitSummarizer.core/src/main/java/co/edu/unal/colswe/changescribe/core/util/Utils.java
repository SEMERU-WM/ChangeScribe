package co.edu.unal.colswe.changescribe.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
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

import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import edu.stanford.nlp.io.IOUtils;

public class Utils {

	public Utils() {
		// TODO Auto-generated constructor stub
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
		treeWalk.setPostOrderTraversal(true);
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
	
	public static String getStringContentOfLastCommit(String filePath,Repository repository) throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
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
		treeWalk.setPostOrderTraversal(true);
		treeWalk.setFilter(PathFilter.create(filePath));
		if (!treeWalk.next()) {
			//TODO the file is added to project
			throw new IllegalStateException(
					"CHANGECOMMIT -- Did not find expected file '" + filePath + "'");
		}

		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repository.open(objectId);
		
		return IOUtils.stringFromFile(Utils.inputStreamToFile(loader.openStream()).getAbsolutePath(), "utf-8");
	}
	
	public static String getStringContentOfCommitID(String filePath,Repository repository, String commitID) throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		// find the HEAD
		ObjectId lastCommitId = repository.resolve(commitID);

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
		treeWalk.setPostOrderTraversal(true);
		treeWalk.setFilter(PathFilter.create(filePath.substring(filePath.indexOf("/") + 1)));
		if (!treeWalk.next()) {
			
			//TODO the file is added to project
			throw new IllegalStateException(
					"CHANGECOMMIT -- Did not find expected file '" + filePath + "'");
		}
		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repository.open(objectId);
		return IOUtils.stringFromFile(Utils.inputStreamToFile(loader.openStream()).getAbsolutePath(), "utf-8");
	}
	
	public static File getFileContentOfCommitID(String filePath,Repository repository, String commitID) throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		// find the HEAD
		ObjectId lastCommitId = repository.resolve(commitID);
		// a RevWalk allows to walk over commits based on some filtering that is
		// defined
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(lastCommitId);
		// and using commit's tree find the path
		RevTree tree = commit.getTree();
		System.out.println("Having tree: " + tree); 
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		//treeWalk.setPostOrderTraversal(true);
		treeWalk.setFilter(PathFilter.create(filePath.substring(filePath.indexOf("/") + 1)));
		
		if (!treeWalk.next()) {
			treeWalk = new TreeWalk(repository);
			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);
			treeWalk.setFilter(PathFilter.create(filePath));
			if(!treeWalk.next()) {
				//TODO the file is added to project
				throw new IllegalStateException(
						"CHANGECOMMIT -- Did not find expected file '" + filePath + "'");
			}
		}

		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repository.open(objectId);
		
		return Utils.inputStreamToFile(loader.openStream());
	}

	public static File inputStreamToFile(InputStream is) throws IOException {
		File contentFile = File.createTempFile("tmpCont", ".txt");
		OutputStream outputStream = null;
		outputStream = new FileOutputStream(contentFile);
		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = is.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
		outputStream.close();
		
		return contentFile;
	}
	
	public static String cleanRelativePath(String path) {
		String newPath = path;
		if(!System.getProperty("path.separator").equals("\\")) {
			newPath.replaceAll("/", System.getProperty("file.separator"));
		}
		return newPath;
	}
	
	public static void compareModified(ChangedFile file,Git git) {
		File previousType = null;
		try {
			previousType = Utils.getFileContentOfLastCommit(file.getPath(), git.getRepository());
			if(previousType == null) {
				throw new Exception("File renamed");
			}
		} catch (RevisionSyntaxException e) {
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isInitialCommit(Git git) {
		boolean isInitialCommit = true;
		LogCommand log = git.log();
		Iterable<RevCommit> commits = null;
		try {
			commits = log.all().call();
			for (RevCommit revCommit : commits) {
				revCommit.getId();
				isInitialCommit = false;
				break;
			}
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			System.out.println("NO HEAD: INITIAL COMMIT");
			isInitialCommit = true;
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return isInitialCommit;
	}

}
