package unal.edu.co;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import unal.edu.co.ast.JavaASTParser;
import unal.edu.co.repository.git.ChangedFile;
import unal.edu.co.repository.git.ChangedFile.TypeChange;
import unal.edu.co.repository.git.SCMRepository;
import unal.edu.co.textgenerator.ChangeDescriptor;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class CommitGenerator {
	
	public static void extractDifferences() {
		SCMRepository repo = new SCMRepository();
		Git git = repo.getGit();
		
		Status status = null;
		try {
			status = repo.getStatus();
		} catch (NoWorkTreeException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} 
		
		Set<ChangedFile> differences = SCMRepository.getDifferences(status,git.getRepository().getWorkTree().getAbsolutePath());
		
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		ChangeDescriptor cd = new ChangeDescriptor();
		for (ChangedFile file : differences) {
			try {
				if(!file.getChangeType().equals(TypeChange.UNTRACKED.name())) {
					File left = SCMRepository.getFileContentOfLastCommit(file.getPath(), git.getRepository());
					File right = new File(file.getAbsolutePath());
					
					if(file.getAbsolutePath().endsWith(".java")) {
						distiller.extractClassifiedSourceCodeChanges(left, right);
						
						List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
						if(changes != null) {
						    for(SourceCodeChange change : changes) {
						    	System.out.println(cd.generateChangeDescription(change));
						    }
						}
						
					}
				} else {
					if(file.getAbsolutePath().endsWith(".java")) {
						File right = new File(file.getAbsolutePath());
						JavaASTParser parser = new JavaASTParser(right);
						parser.parse();
						for (ASTNode node : parser.getNodes()) {
							System.out.println(node.getNodeType());
						}
						
					} else {
						//TODO other files
					}
				}
			} catch(Exception e) {
			    System.err.println("Warning: error while change distilling. " + e.getMessage());
			}
			
		}
	}
	
	public static void main(String[] args) {
		
		extractDifferences();
		
		/*SCMRepository repo = new SCMRepository();
		Git git = repo.getGit();
		
		Status status = null;
		try {
			status = repo.getStatus();
		} catch (NoWorkTreeException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} 
		
		Set<ChangedFile> differences = SCMRepository.getDifferences(status,git.getRepository().getWorkTree().getAbsolutePath());
		
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		
		for (ChangedFile file : differences) {
			try {
				if(!file.getChangeType().equals(TypeChange.UNTRACKED.name())) {
					File left = SCMRepository.getFileContentOfLastCommit(file.getPath(), git.getRepository());
					File right = new File(file.getAbsolutePath());
					//System.out.println("--- CHANGE TYPE: " + file.getChangeType());
					distiller.extractClassifiedSourceCodeChanges(left, right);
					if(file.getAbsolutePath().endsWith(".java")) {
						//System.out.println("--- FILE: " + file.getAbsolutePath());
						CompilationUnit cuLeft = JavaASTParser.parseFile(left);
						CompilationUnit cuRight = JavaASTParser.parseFile(right);
						
					}
				} else {
					//TODO for UNTRACKED files.
				}
			} catch(Exception e) {
			    System.err.println("Warning: error while change distilling. " + e.getMessage());
			}
			//System.out.println(file.getChangeType() + " File: " + file.toString());
			List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
			if(changes != null) {
			    for(SourceCodeChange change : changes) {
			    	System.out.println("Cambio: " + change.getLabel() + " OLD CODE: " + change.getParentEntity() + " - NEW CODE: " + change.getChangedEntity() + " - " + 
			    			change.getSignificanceLevel() + " - " + change.getChangeType() + " - " + change.getParentEntity() );
			    	
			    }
			}
		}*/
		
		
		

	}

}
