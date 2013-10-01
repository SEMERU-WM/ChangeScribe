package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jgit.api.Git;

import co.edu.unal.colswe.CommitSummarizer.core.ast.ProjectInformation;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypeIdentifier;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedCommit;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedMethod;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.CommitStereotype;
import co.edu.unal.colswe.CommitSummarizer.core.util.Utils;

public class SummarizeChanges {
	
	private Git git;
	private StereotypeIdentifier stereotypeIdentifier;
	private List<StereotypeIdentifier> identifiers;
	private StringBuilder comment = new StringBuilder();
	private ChangedFile[] differences;
	
	public SummarizeChanges(Git git) {
		super();
		this.git = git;
		this.stereotypeIdentifier = new StereotypeIdentifier();
		this.identifiers = new ArrayList<StereotypeIdentifier>();
	}

	@SuppressWarnings("unused")
	public void summarize(ChangedFile[] differences) {
		this.differences = differences;
		String currentPackage = "";
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
				} else if(file.getChangeType().equals(TypeChange.UNTRACKED.name())) {
					if(file.getAbsolutePath().endsWith(".java")) {
						identifyStereotypes(file, "added");
					} 
				} else if(file.getChangeType().equals(TypeChange.REMOVED.name())) {
					if(file.getAbsolutePath().endsWith(".java")) {
						identifyStereotypes(file, "removed");
					}
				}
			} catch(Exception e) {
			    System.err.println("Warning: error while change distilling. " + e.getMessage());
			}
		}
		summarizeCommitStereotype();
		summarizeTypes();
		
	}
	
	public void summarizeTypes() {
		String currentPackage = "";
		for(StereotypeIdentifier identifier : identifiers) {
			for(StereotypedElement element : identifier.getStereotypedElements()) {
					SummarizeType summarizeType = new SummarizeType(element, identifier, differences);
					summarizeType.generate();
					
					if(currentPackage.trim().equals("")) {
						currentPackage = identifier.getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName();
						System.out.println("current 1: " + currentPackage);
						getComment().append("* Modifications to package " + currentPackage + ":  \n\n");
					} else if(!currentPackage.equals(identifier.getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName())) {
						currentPackage = identifier.getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName();
						System.out.println("current 2: " + currentPackage);
						getComment().append("* Modifications to package " + currentPackage + ":  \n\n");
					}
					
					getComment().append(summarizeType.getBuilder().toString());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void summarizeCommitStereotype() {
		List<StereotypedMethod> methods = new ArrayList<StereotypedMethod>();
		
		for(StereotypeIdentifier identifier : identifiers) {
			for(StereotypedElement element : identifier.getStereotypedElements()) {
				methods.addAll((Collection<? extends StereotypedMethod>) element.getStereoSubElements());
			}
			
		}
		StereotypedCommit stereotypedCommit = new StereotypedCommit(methods);
		stereotypedCommit.buildSignature();
		CommitStereotype stereotype = stereotypedCommit.findStereotypes();
		
		if(stereotype != null) {
			//getComment().append(stereotypedCommit.getStereotypes().toString() + "\n\n");
			getComment().append(CommitStereotypeDescriptor.describe(stereotypeIdentifier.getCompilationUnit() ,stereotypedCommit) + "\n\n");
		} else {
			getComment().append("Not found commit stereotype\n\n");
		}
	}
	
	public void identifyStereotypes(ChangedFile file, String scmOperation) {
		String projectName = ProjectInformation.getProject(ProjectInformation.getSelectedProject()).getName();
		IResource res = ProjectInformation.getProject(ProjectInformation.getSelectedProject()).findMember(file.getPath().replaceFirst(projectName, ""));
		IFile ifile = ProjectInformation.getSelectedProject().getWorkspace().getRoot().getFile(res.getFullPath());
		stereotypeIdentifier = new StereotypeIdentifier((ICompilationUnit) JavaCore.create(ifile), 0, 0);
		stereotypeIdentifier.identifyStereotypes();
		stereotypeIdentifier.setScmOperation(scmOperation);
		
		identifiers.add(stereotypeIdentifier);
	}

	public Git getGit() {
		return git;
	}

	public void setGit(Git git) {
		this.git = git;
	}

	public StringBuilder getComment() {
		return comment;
	}

	public void setComment(StringBuilder comment) {
		this.comment = comment;
	}

	public List<StereotypeIdentifier> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(List<StereotypeIdentifier> identifiers) {
		this.identifiers = identifiers;
	}

}
