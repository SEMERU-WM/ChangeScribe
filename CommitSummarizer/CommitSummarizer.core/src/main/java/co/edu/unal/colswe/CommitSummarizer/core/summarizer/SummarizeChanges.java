package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.swt.widgets.Display;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.Delete;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import co.edu.unal.colswe.CommitSummarizer.core.FilesChangedListDialog;
import co.edu.unal.colswe.CommitSummarizer.core.ast.ProjectInformation;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypeIdentifier;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedCommit;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedMethod;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.CommitStereotype;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.NounPhrase;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.util.CompilationUtils;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.tokenizer.Tokenizer;
import co.edu.unal.colswe.CommitSummarizer.core.util.Utils;

public class SummarizeChanges {
	
	private Git git;
	private StereotypeIdentifier stereotypeIdentifier;
	private List<StereotypeIdentifier> identifiers;
	private StringBuilder comment = new StringBuilder();
	private ChangedFile[] differences;
	private FilesChangedListDialog changedListDialog;
	private SortedMap<String, StereotypeIdentifier> summarized = new TreeMap<String, StereotypeIdentifier>();
	private LinkedList<ChangedFile> modulesAdded;
	private FileDistiller distiller; 
	private LinkedList<ChangedFile> modifiedFiles;
	
	public SummarizeChanges(Git git) {
		super();
		this.git = git;
		this.stereotypeIdentifier = new StereotypeIdentifier();
		this.identifiers = new ArrayList<StereotypeIdentifier>();
		distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
	}
	
	public void initSummary(final ChangedFile[] differences) {
		this.differences = differences;
		this.identifiers = new ArrayList<StereotypeIdentifier>();
		this.summarized = new TreeMap<String, StereotypeIdentifier>();
		this.modulesAdded = new LinkedList<>();
		this.modifiedFiles = new LinkedList<>();
		getChangedListDialog().getEditor().getText().setText("");
		removeCreatedPackages();
		//deleteTmpProject();
	}

	@SuppressWarnings("unused")
	public void summarize(final ChangedFile[] differences) {
		initSummary(differences);
		String currentPackage = "";
		//rebuildVersion();

		Job job = new Job("Calculating method and types stereotypes") {
				@Override
				protected IStatus run(IProgressMonitor externalMonitor) {
					for (final ChangedFile file : differences) {
						Job internalJob = new Job("Calculating stereotype for " + file.getName()) {
							@Override
							protected IStatus run(IProgressMonitor monitor) {
								StereotypeIdentifier identifier = null;
								try {
									System.out.println("CHANGE TYPE: " + file.getChangeType());
									if(file.getChangeType().equals(TypeChange.UNTRACKED.name()) 
											|| file.getChangeType().equals(TypeChange.ADDED.name())) {
										if(file.getAbsolutePath().endsWith(".java")) {
											monitor.subTask("Identifying stereotypes for " + file.getName());
											identifier = identifyStereotypes(file, file.getChangeType());
										} 
									} else if(file.getChangeType().equals(TypeChange.REMOVED.name())) {
										if(file.getAbsolutePath().endsWith(".java")) {
											monitor.subTask("Identifying stereotypes for " + file.getName());
											identifier = identifyStereotypes(file, file.getChangeType());
										}
									} else if(file.getChangeType().equals(TypeChange.MODIFIED.name())) {
										monitor.subTask("Identifying stereotypes for " + file.getName());
										modifiedFiles.add(file);
									}
									if(identifier != null) {
										monitor.subTask("Describing type " + file.getName());
										summarizeType(identifier);
									}
								} catch(Exception e) {
								    e.printStackTrace();
								}
								
								return Status.OK_STATUS;
							}
						};
						internalJob.addJobChangeListener(new JobChangeAdapter() {
							public void done(IJobChangeEvent event) {
								updateTextInputDescription();					           
						    }
						});
						internalJob.schedule();
						try {
							internalJob.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					return Status.OK_STATUS;
				}
			};
			job.schedule();
	}

	public void updateTextInputDescription() {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(summarized.size() + modulesAdded.size() + modifiedFiles.size() == differences.length) {
					String currentPackage = "";
					StringBuilder desc = new StringBuilder(); 
					desc.append(summarizeCommitStereotype());
					int i = 1;
					int j = 1;
					for(Entry<String, StereotypeIdentifier> identifier : summarized.entrySet()) {
						if(currentPackage.trim().equals("")) {
							currentPackage = identifier.getValue().getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName();
							System.out.println("current 1: " + currentPackage);
							desc.append(i + ". Modifications to package " + currentPackage + ":  \n\n");
							i++;
						} else if(!currentPackage.equals(identifier.getValue().getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName())) {
							currentPackage = identifier.getValue().getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName();
							System.out.println("current 2: " + currentPackage);
							desc.append(i + ". Modifications to package " + currentPackage + ":  \n\n");
							j = 1;
							i++;
						}
						desc.append((i - 1) + "." + j + ". " + identifier.getValue().toString());
						j++;
					}
					
					for(ChangedFile file : modifiedFiles) {
						try {
							compareModified(file);
						} catch(IllegalStateException ex) {
							ex.printStackTrace();
							desc.append(i + ". the " + file.getName() + " was renamed:  \n\n");
						}
						List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
						if(changes != null) {
							desc.append(i + ". Modifications to file " + file.getName() + ":  \n\n");
						    for(SourceCodeChange change : changes) {
						    	desc.append("\t\t");
						    	if(change instanceof Update) {
						    		Update update = (Update) change;
						    		if(update.getChangeType() == ChangeType.STATEMENT_UPDATE) {
						    			String fType = update.getChangedEntity().getType().name().toLowerCase().replace("_", " ");
						    			desc.append((i) + "." + j + ". " + fType+ " modified ");
						    			if(update.getChangedEntity().getType() == JavaEntityType.METHOD_INVOCATION) {
						    				MessageSend methodC = (MessageSend) update.getChangedEntity().getAstNode();
						    				MessageSend methodN = (MessageSend) update.getNewEntity().getAstNode();
						    				
						    				if(methodC.receiver != methodN.receiver) {
						    					desc.append("of " + new String(methodC.receiver.toString()) + " to " + new String(methodN.receiver.toString()) + " on " + update.getParentEntity().getName() + " method");
						    				} else if(methodC.selector != methodN.selector) {
						    					desc.append("of " + new String(methodC.selector.toString()) + " to " + new String(methodN.selector.toString()) + " on " + update.getParentEntity().getName() + " method");
						    				}
						    				//desc.append((i) + "." + j + ". " + fType+ " modified ");
						    			} else if(update.getChangedEntity().getType() == JavaEntityType.ASSIGNMENT) {
						    				Assignment asC = (Assignment) update.getChangedEntity().getAstNode();
						    				Assignment asN = (Assignment) update.getNewEntity().getAstNode();
						    				
						    				if(asC.lhs != asN.lhs) {
						    					desc.append("of " + new String(asC.lhs.toString()) + " to " + new String(asN.lhs.toString()) + " on " + update.getParentEntity().getName() + " method");
						    				} else if(asC.expression != asN.expression) {
						    					desc.append("of " + new String(asC.expression.toString()) + " to " + new String(asN.expression.toString()) + " on " + update.getParentEntity().getName() + " method");
						    				}
						    			}
						    			else {
						    				desc.append((i) + "." + j + ". " + update.getChangedEntity().getName() + " by " + update.getNewEntity().getUniqueName() + " on " + update.getParentEntity().getName()  + " method");
						    			}
						    		} else if(update.getChangeType() == ChangeType.METHOD_RENAMING) {
						    			desc.append((i) + "." + j + ". " + update.getChangedEntity().getName().substring(0, update.getChangedEntity().getName().indexOf("(")) + " method renamed " + " by " + update.getNewEntity().getName().substring(0, update.getNewEntity().getName().indexOf("(")));
						    		} else if(update.getChangeType() == ChangeType.ATTRIBUTE_RENAMING) {
						    			desc.append((i) + "." + j + ". " + update.getChangedEntity().getName().substring(0, update.getChangedEntity().getName().indexOf(":")).trim() + " attribute renamed " + " by " + update.getNewEntity().getName().substring(0, update.getNewEntity().getName().indexOf(":")).trim());
						    		} else if(update.getChangeType() == ChangeType.CONDITION_EXPRESSION_CHANGE) {
						    			desc.append((i) + "." + j + ". " + "Conditional expression " + update.getChangedEntity().getName().substring(1, update.getChangedEntity().getName().length() - 1) + " was modified for " + update.getNewEntity().getUniqueName() + " on " + update.getParentEntity().getName() + " method");
						    		} else if(update.getChangeType() == ChangeType.INCREASING_ACCESSIBILITY_CHANGE) {
						    			desc.append((i) + "." + j + ". " + "Accessibility was increased of " + update.getChangedEntity().getUniqueName() + " to " + update.getNewEntity().getUniqueName() + " for " + update.getRootEntity().getJavaStructureNode().getName().substring(0, update.getRootEntity().getJavaStructureNode().getName().indexOf(":") - 1) + " " + update.getRootEntity().getType().name().toLowerCase());
						    		} else if(update.getChangeType() == ChangeType.DECREASING_ACCESSIBILITY_CHANGE) {
						    			desc.append((i) + "." + j + ". " + "Accessibility was decreased of " + update.getChangedEntity().getUniqueName() + " to " + update.getNewEntity().getUniqueName() + " for " + update.getRootEntity().getJavaStructureNode().getName() + " " + update.getRootEntity().getType().name().toLowerCase());
						    		} else if(update.getChangeType() == ChangeType.COMMENT_INSERT || update.getChangeType() == ChangeType.DOC_INSERT) {
						    			String type = update.getChangedEntity().getType().name().toLowerCase().replace("_", " ");
						    			String entityType = update.getRootEntity().getJavaStructureNode().getType().name().toLowerCase();
						    			desc.append((i) + "." + j + ". " + type +" updated on " + update.getRootEntity().getJavaStructureNode().getName() + " " + entityType);
						    		}
						    		else {
							    		desc.append((i) + "." + j + ". " + change.getLabel() + " OLD CODE: " + change.getParentEntity() + 
								    			" - NEW CODE: " + update.getNewEntity() + " - " + 
								    			change.getSignificanceLevel() + " change type: " + change.getChangeType() + "\n");
						    		}
						    		
						    	} else if(change instanceof Insert) {
						    		Insert insert = (Insert) change;
						    		String fType = insert.getChangedEntity().getType().name().toLowerCase().replace("_", " ");
						    		if(insert.getChangeType() == ChangeType.ADDITIONAL_FUNCTIONALITY) {
						    			desc.append((i) + "." + j + ". " + "An additional funtionality for " + insert.getChangedEntity().getName().substring(0, insert.getChangedEntity().getName().indexOf("(")) + " was added");
						    		} else if(insert.getChangeType() == ChangeType.COMMENT_INSERT || insert.getChangeType() == ChangeType.DOC_INSERT) {
						    			String type = insert.getChangedEntity().getType().name().toLowerCase().replace("_", " ");
						    			String entityType = insert.getRootEntity().getJavaStructureNode().getType().name().toLowerCase();
						    			desc.append((i) + "." + j + ". " + type +" added on " + insert.getRootEntity().getJavaStructureNode().getName() + " " + entityType);
						    		} else if(insert.getChangedEntity().getType() == JavaEntityType.METHOD_INVOCATION) {
						    			String type = insert.getChangedEntity().getType().name().toLowerCase().replace("_", " ");
						    			MessageSend methodC = (MessageSend) insert.getChangedEntity().getAstNode();
					    				
					    				desc.append((i) + "." + j + ". " + type + " was added for " + new String(methodC.selector) + " on " + insert.getRootEntity().getJavaStructureNode().getName() + " method");
					    			} 
					    			
					    		} else if(change instanceof Delete) {
					    			Delete delete = (Delete) change;
					    			if(delete.getChangeType() == ChangeType.STATEMENT_DELETE) {
					    				desc.append((i) + "." + j + ". " );
					    				
					    				String statementType = delete.getChangedEntity().getType().name().toLowerCase().replace("statement", "").replace("_", " ");
					    				desc.append(statementType);
					    				if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof LocalDeclaration) {
					    					LocalDeclaration localDec = (LocalDeclaration) delete.getChangedEntity().getAstNode();
					    					NounPhrase phrase = new NounPhrase(Tokenizer.split(new String(localDec.name)));
					    					phrase.generate();
					    					desc.append(" for " + phrase.toString() + " was removed on " + delete.getRootEntity().getJavaStructureNode().getName() + " method");
					    				} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof ForeachStatement) {
					    					ForeachStatement forDec = (ForeachStatement) delete.getChangedEntity().getAstNode();
					    					NounPhrase phrase = new NounPhrase(Tokenizer.split(((MessageSend)forDec.collection).receiver.toString()));
					    					phrase.generate();
					    					desc.append(" loop on " + phrase.toString() + " collection was removed on " + delete.getRootEntity().getJavaStructureNode().getName() + " method");
					    				} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof MessageSend) {
					    					MessageSend messageSend = (MessageSend) delete.getChangedEntity().getAstNode();
					    					NounPhrase phrase = new NounPhrase(Tokenizer.split(new String(messageSend.selector)));
					    					phrase.generate();
					    					desc.append(" to " + phrase.toString());
					    					if(messageSend.arguments != null && messageSend.arguments.length > 0) {
					    						phrase = new NounPhrase(Tokenizer.split(new String(((SingleNameReference)messageSend.arguments[0]).token)));
					    						phrase.generate();
					    					}
					    					if(!desc.toString().endsWith(phrase.toString())) {
					    						desc.append(" " + phrase.toString());
					    					}
					    					desc.append("  was removed on " + delete.getRootEntity().getJavaStructureNode().getName() + " method");
					    				}
					    				else {
					    					System.out.println("other");
					    				}
						    		} else if(delete.getChangeType() == ChangeType.COMMENT_INSERT || delete.getChangeType() == ChangeType.DOC_INSERT) {
						    			String type = delete.getChangedEntity().getType().name().toLowerCase().replace("_", " ");
						    			String entityType = delete.getRootEntity().getJavaStructureNode().getType().name().toLowerCase();
						    			desc.append((i) + "." + j + ". " + type +" removed on " + delete.getRootEntity().getJavaStructureNode().getName() + " " + entityType);
						    		}
					    		} else if(change instanceof Move) {
					    			
					    		}
						    	desc.append("\n");
						    	j++;
						    }
						    i++;
						}
					}
					
					getChangedListDialog().getEditor().getText().setText(desc.toString());
					if(summarized.size() > 0) {
						getChangedListDialog().updateSignatureCanvas();
					}
					
					removeCreatedPackages();
				}
			}
		});

	}
	
	protected void removeCreatedPackages() {
		IFolder folder = ((IJavaProject)changedListDialog.getSelection()).getProject().getFolder("src/commsummtmp");
		try {
			folder.delete(true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void compareModified(ChangedFile file) {
		File previousType = null;
		File currentType = null;
		
		try {
			previousType = Utils.getFileContentOfLastCommit(file.getPath(), getGit().getRepository());
			currentType = new File(file.getAbsolutePath());
			distiller.extractClassifiedSourceCodeChanges(previousType, currentType);
			
		} catch (RevisionSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			result = CommitStereotypeDescriptor.describe(stereotypeIdentifier.getCompilationUnit() ,stereotypedCommit);
			result += CommitStereotypeDescriptor.describeNewModules(git, differences) + "\n\n";
		} else {
			result = "Not found commit stereotype\n\n";
		}
		changedListDialog.setSignatureMap(stereotypedCommit.getSignatureMap());
		return result;
	}
	
	/*public void rebuildVersion() {
		
		Set<ICompilationUnit> previousCU = new HashSet<>();
		Set<ICompilationUnit> currentCU = new HashSet<>();
		IPackageFragment[] packages;
	    try {
	        packages = changedListDialog.getSelection().getPackageFragments();
	        for (IPackageFragment mypackage : packages) {
	            if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
	            	for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
	            		ChangedFile file = new ChangedFile(unit.getPath().toString().replaceFirst("/", ""), TypeChange.ADDED.name(), getGit().getRepository().getWorkTree().getAbsolutePath());
	            		IndexDiff diff = null;
						try {
							diff = new IndexDiff(git.getRepository(), "HEAD", new FileTreeIterator(git.getRepository()));
							diff.setFilter(new PathFilterGroup().createFromStrings(file.getPath()));
		            		diff.diff();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            		
	            		if (!diff.getModified().isEmpty() || !diff.getRemoved().isEmpty()) {
	            			previousCU.add(findRefactorings(file));
	            		}
		
		            }
	            	Set<ChangedFile> differences = SCMRepository.getRemovedFiles(git.status().call(), getGit().getRepository().getWorkTree().getAbsolutePath());
	            	for (ChangedFile file : differences) {
	            			previousCU.add(findRefactorings(file));
		            }
	                System.out.println("Source Name " + mypackage.getElementName());
	                System.out.println("Number of Classes: " + mypackage.getClassFiles().length);
	                
	                //findRefactorings(file);
	            }
	           
	        }
	    } catch (JavaModelException | NoWorkTreeException | GitAPIException e) {
	        e.printStackTrace();
	    }
	    
	    try {
			currentCU = getFiles("ChangeDiff");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    List<LSDResult> result = (new LSDiffExecutor()).doLSDiff(currentCU, previousCU);
		if(result != null && !result.isEmpty()) {
			rules.addAll(result);
		}
	}
	
	private static Set<ICompilationUnit> getFiles(String projname) throws CoreException {
		IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
		IProject proj = ws.getProject(projname);
		IJavaProject javaProject = JavaCore.create(proj);
		Set<ICompilationUnit> files = new HashSet<ICompilationUnit>();
		javaProject.open(new NullProgressMonitor());
		for( IPackageFragment packFrag : javaProject.getPackageFragments()) {
			for (ICompilationUnit icu : packFrag.getCompilationUnits()) {
				files.add(icu);
			}
		}
		javaProject.close();
		return files;
	}*/
	
	/*protected ICompilationUnit findRefactorings(ChangedFile file) {
	IProject project = createProject();
	IFolder src = null;
	IJavaProject javaProject = null;
	ICompilationUnit cu = null;
	try {
		javaProject = JavaCore.create(project);
		javaProject.open(null);
		src = project.getFolder("src");
		if(!src.exists()) {
			src.create(true , true , null);
		}
		
		String removedFile;
	
		removedFile = Utils.getStringContentOfLastCommit(file.getPath(), getGit().getRepository());
		IPackageFragment pack = null;
		String packageName = "";
		packageName = CompilationUtils.getPackageNameFromStringClass(removedFile);
		IPackageFragmentRoot rootPack = javaProject.getPackageFragmentRoot(src);
		
		pack = rootPack.createPackageFragment(packageName, true, null);
		cu = pack.createCompilationUnit(file.getName(), removedFile,true, null);
		stereotypeIdentifier = new StereotypeIdentifier(cu, 0, 0);
		
	} catch (RevisionSyntaxException | IOException | IllegalStateException e) {
		e.printStackTrace();
	} catch (CoreException | NullPointerException e) {
		//deleteTmpProject();
		e.printStackTrace();
	} 
	return cu;
	}

	protected void deleteTmpProject() {
		IProject project = createProject();
		try {
			project.delete(true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	/*public IProject createProject() {
		IProject project = null;
		IFolder src = null;
		IJavaProject javaProject = null;
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			project = root.getProject("MyProject");
			
			if(!project.exists()) {
				project.create(null);
			}
			project.open(null);
			
			src = project.getFolder("src");
			if(!src.exists()) {
				src.create(false , true , null);
			}
			
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = JavaCore.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
			
			javaProject = JavaCore.create(project);
			javaProject.open(null);
			
			IPackageFragmentRoot rootPack = javaProject.getPackageFragmentRoot(src);
			
			IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
			IClasspathEntry[] newEntries = new IClasspathEntry[1];
			//System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
			newEntries[0] = JavaCore.newSourceEntry(rootPack.getPath());
			javaProject.setRawClasspath(newEntries, null);
			
		} catch (CoreException | RevisionSyntaxException e) {
			e.printStackTrace();
		} 
		return project;
	}*/
	
	public StereotypeIdentifier identifyStereotypes(ChangedFile file, String scmOperation) {
		
		if(scmOperation.equals(TypeChange.ADDED.toString()) ||scmOperation.equals(TypeChange.UNTRACKED.toString())) {
			getAddedStereotypeIdentifier(file);
		} else if(scmOperation.equals(TypeChange.REMOVED.toString())) {
			stereotypeIdentifier = getRemovedStereotypeIdentifier(file);
		} 
		
		stereotypeIdentifier.identifyStereotypes();
		stereotypeIdentifier.setScmOperation(scmOperation);
		
		identifiers.add(stereotypeIdentifier);
		
		return stereotypeIdentifier;
	}
	
	public StereotypeIdentifier getAddedStereotypeIdentifier(ChangedFile file) {
		
		String projectName;
		IResource res;
		
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
		
		return stereotypeIdentifier;
	}

	public StereotypeIdentifier getRemovedStereotypeIdentifier(ChangedFile file) {
		try {
			String removedFile = Utils.getStringContentOfLastCommit(file.getPath(), getGit().getRepository());
			IPackageFragment pack = null;
			String packageName = "";
			packageName = "commsummtmp." + CompilationUtils.getPackageNameFromStringClass(removedFile);
			IFolder folder = ((IJavaProject)changedListDialog.getSelection()).getProject().getFolder("src");
			pack = changedListDialog.getSelection().getPackageFragmentRoot(folder).createPackageFragment(packageName, true, null);
			ICompilationUnit cu = pack.createCompilationUnit(file.getName(), removedFile,true, null);
			stereotypeIdentifier = new StereotypeIdentifier(cu, 0, 0);
		} catch (RevisionSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stereotypeIdentifier;
	}
	
	public StereotypeIdentifier getModifiedStereotypeIdentifier(ChangedFile file) {
		try {
			String removedFile = Utils.getStringContentOfLastCommit(file.getPath(), getGit().getRepository());
			IPackageFragment pack = null;
			String packageName = "";
			packageName = "commsummtmp." + CompilationUtils.getPackageNameFromStringClass(removedFile);
			IFolder folder = ((IJavaProject)changedListDialog.getSelection()).getProject().getFolder("src");
			pack = changedListDialog.getSelection().getPackageFragmentRoot(folder).createPackageFragment(packageName, true, null);
			ICompilationUnit cu = pack.createCompilationUnit(file.getName(), removedFile,true, null);
			stereotypeIdentifier = new StereotypeIdentifier(cu, 0, 0);
		} catch (RevisionSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public LinkedList<ChangedFile> getModifiedFiles() {
		return modifiedFiles;
	}

	public void setModifiedFiles(LinkedList<ChangedFile> modifiedFiles) {
		this.modifiedFiles = modifiedFiles;
	}

}
