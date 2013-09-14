package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jgit.api.Git;

import co.edu.unal.colswe.CommitSummarizer.core.ast.JParser;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.analyzer.TypeAnalyzer;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedMethod;
import co.edu.unal.colswe.CommitSummarizer.core.util.Utils;

public class SummarizeChanges {
	
	private Git git;
	
	public SummarizeChanges(Git git) {
		super();
		this.git = git;
	}

	public void summarize(ChangedFile[] differences) {
		for (ChangedFile file : differences) {
			try {
				if(!file.getChangeType().equals(TypeChange.UNTRACKED.name())) {
					File left = Utils.getFileContentOfLastCommit(file.getPath(), getGit().getRepository());
					File right = new File(file.getAbsolutePath());
					
					/*if(file.getAbsolutePath().endsWith(".java")) {
						distiller.extractClassifiedSourceCodeChanges(left, right);
						
						List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
						if(changes != null) {
						    for(SourceCodeChange change : changes) {
						    	System.out.println(cd.generateChangeDescription(change));
						    }
						}
						
					}*/
				} else {
					if(file.getAbsolutePath().endsWith(".java")) {
						System.out.println("File: " + file.getAbsolutePath());
						File right = new File(file.getAbsolutePath());
						JParser parser = new JParser(right);
						parser.parse();
						for (ASTNode node : parser.getElements()) {
							TypeAnalyzer analyzer = new TypeAnalyzer((TypeDeclaration) node);
							for (StereotypedMethod method : analyzer.getStereotypedMethods()) {
								System.out.println("Method: " + method.getQualifiedName() + "Stereotype: " + method.getStereotypes());
							}
							
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
	
	

	public Git getGit() {
		return git;
	}

	public void setGit(Git git) {
		this.git = git;
	}

}
