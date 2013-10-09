package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jgit.api.Git;
import org.eclipse.swt.widgets.Display;

import co.edu.unal.colswe.CommitSummarizer.core.FilesChangedListDialog;
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
	private FilesChangedListDialog changedListDialog;
	private SortedMap<String, StereotypeIdentifier> summarized = new TreeMap<String, StereotypeIdentifier>();
	
	public SummarizeChanges(Git git) {
		super();
		this.git = git;
		this.stereotypeIdentifier = new StereotypeIdentifier();
		this.identifiers = new ArrayList<StereotypeIdentifier>();
	}

	@SuppressWarnings("unused")
	public void summarize(final ChangedFile[] differences) {
		this.differences = differences;
		this.identifiers = new ArrayList<StereotypeIdentifier>();
		summarized = new TreeMap<String, StereotypeIdentifier>();
		getChangedListDialog().getText().setText("");
		String currentPackage = "";
		
			Job job = new Job("Calculating method and types stereotypes") {
				@Override
				protected IStatus run(IProgressMonitor arg0) {
					for (final ChangedFile file : differences) {
						Job internalJob = new Job("Calculating stereotype for " + file.getName()) {
							@Override
							protected IStatus run(IProgressMonitor arg0) {
								StereotypeIdentifier identifier = null;
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
											identifier = identifyStereotypes(file, "added");
										} 
									} else if(file.getChangeType().equals(TypeChange.REMOVED.name())) {
										if(file.getAbsolutePath().endsWith(".java")) {
											identifier= identifyStereotypes(file, "removed");
										}
									}
								} catch(Exception e) {
								    System.err.println("Warning: error while change distilling. " + e.getMessage() );
								    e.printStackTrace();
								}
								if(identifier != null) {
									summarizeType(identifier);
								}
								return Status.OK_STATUS;
							}
						};
						internalJob.addJobChangeListener(new JobChangeAdapter() {
							public void done(IJobChangeEvent event) {
								System.out.println("ANTES DE TERMINO");
						        if (event.getResult().isOK()) {
						        	if(summarized.size() == identifiers.size()) {
						        		System.out.println("TERMINO-TERMINO-TERMINO");
										updateTextInputDescription();
						        	}
						        }
						           
						    }
						});
						internalJob.schedule();
					}
					
					return Status.OK_STATUS;
				}
			};
			
			job.schedule();	
	}
	
	public void updateTextInputDescription() {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				
				String currentPackage = "";
				StringBuilder desc = new StringBuilder();
				desc.append(summarizeCommitStereotype());
				for(Entry<String, StereotypeIdentifier> identifier : summarized.entrySet()) {
					if(currentPackage.trim().equals("")) {
						currentPackage = identifier.getValue().getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName();
						System.out.println("current 1: " + currentPackage);
						desc.append("* Modifications to package " + currentPackage + ":  \n\n");
					} else if(!currentPackage.equals(identifier.getValue().getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName())) {
						currentPackage = identifier.getValue().getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName();
						System.out.println("current 2: " + currentPackage);
						desc.append("* Modifications to package " + currentPackage + ":  \n\n");
					}
					desc.append(identifier.getValue().toString());
				}
				getChangedListDialog().getText().setText(desc.toString());
			}
		});

	}
	
	public void summarizeType(StereotypeIdentifier identifier) {
		for(StereotypedElement element : identifier.getStereotypedElements()) {
				SummarizeType summarizeType = new SummarizeType(element, identifier, differences);
				summarizeType.generate();
				
				identifier.getBuilder().append(summarizeType.getBuilder().toString());
				
				if(!summarized.containsKey(element.getQualifiedName())) {
					summarized.put(element.getQualifiedName(), identifier);
				}
		}
	}
	
	@SuppressWarnings("unchecked")
	public String summarizeCommitStereotype() {
		List<StereotypedMethod> methods = new ArrayList<StereotypedMethod>();
		String result = "";
		for(StereotypeIdentifier identifier : identifiers) {
			for(StereotypedElement element : identifier.getStereotypedElements()) {
				methods.addAll((Collection<? extends StereotypedMethod>) element.getStereoSubElements());
			}
		}
		StereotypedCommit stereotypedCommit = new StereotypedCommit(methods);
		stereotypedCommit.buildSignature();
		CommitStereotype stereotype = stereotypedCommit.findStereotypes();
		
		if(stereotype != null) {
			result = CommitStereotypeDescriptor.describe(stereotypeIdentifier.getCompilationUnit() ,stereotypedCommit) + "\n\n";
		} else {
			result = "Not found commit stereotype\n\n";
		}
		return result;
	}
	
	public StereotypeIdentifier identifyStereotypes(ChangedFile file, String scmOperation) {
		
		String projectName = "";
		IResource res = null;
		if(changedListDialog.getSelection() != null) {
			projectName = changedListDialog.getSelection().getElementName();
			res = changedListDialog.getSelection().getProject().findMember(file.getPath().replaceFirst(projectName, ""));
			stereotypeIdentifier = new StereotypeIdentifier((ICompilationUnit) JavaCore.create(res, changedListDialog.getSelection()), 0, 0);
		} else {
			projectName = ProjectInformation.getProject(ProjectInformation.getSelectedProject()).getName();
			res = ProjectInformation.getProject(ProjectInformation.getSelectedProject()).findMember(file.getPath().replaceFirst(projectName, ""));
			IFile ifile = ProjectInformation.getSelectedProject().getWorkspace().getRoot().getFile(res.getFullPath());
			stereotypeIdentifier = new StereotypeIdentifier((ICompilationUnit) JavaCore.create(ifile), 0, 0);
		}
		
		stereotypeIdentifier.identifyStereotypes();
		stereotypeIdentifier.setScmOperation(scmOperation);
		
		identifiers.add(stereotypeIdentifier);
		
		return stereotypeIdentifier;
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

	public FilesChangedListDialog getChangedListDialog() {
		return changedListDialog;
	}

	public void setChangedListDialog(FilesChangedListDialog changedListDialog) {
		this.changedListDialog = changedListDialog;
	}

}
