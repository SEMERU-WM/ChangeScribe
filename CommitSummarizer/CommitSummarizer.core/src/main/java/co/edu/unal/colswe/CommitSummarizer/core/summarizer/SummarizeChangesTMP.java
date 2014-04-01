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

import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.swt.widgets.Display;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import co.edu.unal.colswe.CommitSummarizer.core.Activator;
import co.edu.unal.colswe.CommitSummarizer.core.DescribeVersionsDialog;
import co.edu.unal.colswe.CommitSummarizer.core.Module;
import co.edu.unal.colswe.CommitSummarizer.core.ast.ProjectInformation;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.CommitSummarizer.core.impactanalysis.Impact;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypeIdentifier;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedCommit;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedMethod;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.CommitStereotype;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.MethodStereotype;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.util.CompilationUtils;
import co.edu.unal.colswe.CommitSummarizer.core.util.Utils;

import commitsummarizer.core.preferences.PreferenceConstants;

public class SummarizeChangesTMP {
	
	private Git git;
	private StereotypeIdentifier stereotypeIdentifier;
	private List<Module> modules;
	private List<StereotypeIdentifier> identifiers;
	private StringBuilder comment = new StringBuilder();
	private ChangedFile[] differences;
	private DescribeVersionsDialog changedListDialog;
	private SortedMap<String, StereotypeIdentifier> summarized = new TreeMap<String, StereotypeIdentifier>();
	private FileDistiller distiller; 
	private LinkedList<ChangedFile> modifiedFiles;
	private LinkedList<ChangedFile> otherFiles;
	private List<StereotypeIdentifier> typesProblem;
	private String older = "";
	private String current = "";
	
	public SummarizeChangesTMP(Git git) {
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
		this.modifiedFiles = new LinkedList<>();
		this.otherFiles = new LinkedList<>();
		this.typesProblem = new LinkedList<>();
		this.modules = new ArrayList<>();
		
		if(getChangedListDialog() != null) {
			getChangedListDialog().getEditor().getText().setText("");
		}
		removeCreatedPackages();
		//deleteTmpProject();
	}

	@SuppressWarnings("unused")
	public void summarize(final ChangedFile[] differences) {
		initSummary(differences);
		String currentPackage = "";
		older = changedListDialog.getAuthorText().getText();
		setCurrent(changedListDialog.getCommitterText().getText());
		//rebuildVersion();
		
		//if (!Utils.isInitialCommit(git)) {
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
									if (file.getAbsolutePath().endsWith(".java")) {
										if (file.getChangeType().equals(TypeChange.UNTRACKED.name()) || file.getChangeType().equals(TypeChange.ADDED.name())) {
											monitor.subTask("Identifying stereotypes for " + file.getName());
											identifier = identifyStereotypes(file,file.getChangeType());
										} else if (file.getChangeType().equals(TypeChange.REMOVED.name())) {
											monitor.subTask("Identifying stereotypes for " + file.getName());
											identifier = identifyStereotypes(file, file.getChangeType());
										} else if (file.getChangeType().equals(TypeChange.MODIFIED.name())) {
											monitor.subTask("Identifying stereotypes for "+ file.getName());
											identifier = identifyStereotypes(file,file.getChangeType());
										}
									} else {
										otherFiles.add(file);
									}

									if (identifier != null) {
										monitor.subTask("Describing type " + file.getName());
										summarizeType(identifier);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								return Status.OK_STATUS;
							}
						};
						/*internalJob.addJobChangeListener(new JobChangeAdapter() {
									public void done(IJobChangeEvent event) {
										updateTextInputDescription();
									}
								});*/
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
			job.addJobChangeListener(new JobChangeAdapter() {
				public void done(IJobChangeEvent event) {
					updateTextInputDescription();
				}
			});
			job.schedule();
		/*} else {
			describeInitialCommit(); 
		}*/
	}
	
	public void describeInitialCommit() {
		StringBuilder desc = new StringBuilder("Initial commit. "); 
		CommitGeneralDescriptor generalDescriptor = new CommitGeneralDescriptor();
		generalDescriptor.setDifferences(differences);
		generalDescriptor.setGit(git);
		desc.append(generalDescriptor.describe());
		
		getChangedListDialog().getEditor().getText().setText(desc.toString());
	}

	public void updateTextInputDescription() {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				/*if(summarized.size() + modifiedFiles.size() + otherFiles.size() + typesProblem.size() == differences.length
						|| summarized.size() + modifiedFiles.size() + otherFiles.size() + typesProblem.size() == differences.length) {*/
					
					Impact impact = new Impact(identifiers);
					impact.setProject(ProjectInformation.getProject(ProjectInformation.getSelectedProject()));
					impact.calculateImpactSet();
					
					String currentPackage = "";
					StringBuilder desc = new StringBuilder(); 
					
					int i = 1;
					int j = 1;
					
					boolean isInitialCommit = Utils.isInitialCommit(git); 
					if(isInitialCommit) {
						getNewModules();
						describeNewModules(desc);
					} 
					
					IPreferenceStore store = Activator.getDefault().getPreferenceStore();

					boolean filtering = store.getBoolean(PreferenceConstants.P_FILTER_COMMIT_MESSAGE);
					double factor = store.getDouble(PreferenceConstants.P_FILTER_FACTOR);
					
					for(Entry<String, StereotypeIdentifier> identifier : summarized.entrySet()) {
						StringBuilder descTmp = new StringBuilder("");
						StereotypeIdentifier calculated = identifiers.get(identifiers.indexOf(identifier.getValue()));
						if(filtering && calculated != null && calculated.getImpactPercentaje() <= (factor /* 100*/) ) {
							continue;
						}
						if(i == 1) {
							desc.append(" This change set is mainly composed of:  \n\n");
						}
						if(currentPackage.trim().equals("")) {
							currentPackage = identifier.getValue().getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName();
							System.out.println("current 1: " + currentPackage);
							desc.append(i + ". Changes to package " + currentPackage + ":  \n\n");
							i++;
						} else if(!currentPackage.equals(identifier.getValue().getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName())) {
							String[] lines = desc.toString().trim().split("\\n");
							if(lines != null && lines.length > 0) {
								String lastLine = lines[lines.length - 1];
								if(lastLine.contains("Changes to package " + currentPackage)) {
									lines[lines.length - 1] = "\n\n";
									desc = new StringBuilder(StringUtils.join(lines));
									i--;
								}
							}
							currentPackage = identifier.getValue().getParser().getCompilationUnit().getPackage().getName().getFullyQualifiedName();
							System.out.println("current 2: " + currentPackage);
							desc.append(i + ". Changes to package " + currentPackage + ":  \n\n");
							j = 1;
							i++;
						}
						if(identifier.getValue().getScmOperation().equals(TypeChange.MODIFIED.toString())) {
							ModificationDescriptor modificationDescriptor = new ModificationDescriptor();
							modificationDescriptor.setDifferences(differences);
							modificationDescriptor.setFile(identifier.getValue().getChangedFile());
							modificationDescriptor.setGit(getGit());
							modificationDescriptor.extractDifferencesBetweenVersions(identifier.getValue().getChangedFile(), git, changedListDialog.getAuthorText().getText(), changedListDialog.getCommitterText().getText());
							modificationDescriptor.extractModifiedMethods();
							modificationDescriptor.describe(i, j, descTmp);
						} else {
							if(!identifier.getValue().getChangedFile().isRenamed()) {
								descTmp.append((i - 1) + "." + j + ". " + identifier.getValue().toString());
							} else {
								descTmp.append((i - 1) + "." + j + ". " + "Rename type " + identifier.getValue().getChangedFile().getRenamedPath().substring(identifier.getValue().getChangedFile().getRenamedPath().lastIndexOf("/") + 1).replace(".java", "") + " with " + identifier.getValue().getChangedFile().getName().replace(".java", "\n\n"));
							}
						}
						if(!descTmp.toString().equals("")) {
							desc.append(descTmp.toString());
							j++;
						}
					}
					
					CommitGeneralDescriptor generalDescriptor = new CommitGeneralDescriptor();
					generalDescriptor.setDifferences(differences);
					generalDescriptor.setInitialCommit(isInitialCommit);
					generalDescriptor.setGit(git);
					desc.insert(0, generalDescriptor.describe());
					
					//Commit stereotype description
					desc.insert(0, summarizeCommitStereotype());
					
					if(isInitialCommit) {
						desc.insert(0, "Initial commit. "); 
					} else { 
						desc.insert(0, "BUG - FEATURE: <type-ID> \n\n");
					}
					
					String[] lines = desc.toString().trim().split("\\n");
					if(lines != null && lines.length > 0) {
						String lastLine = lines[lines.length - 1];
						if(lastLine.contains("Changes to package " + currentPackage)) {
							lines[lines.length - 1] = "\n\n";
							desc = new StringBuilder(StringUtils.join(lines, "\n"));
						}
					}
					
					getChangedListDialog().getEditor().getText().setText(desc.toString());
					getChangedListDialog().updateSignatureCanvas();
					getChangedListDialog().updateMessage();
					
					removeCreatedPackages();
				}
			//}
		});

	}
	
	protected void describeNewModules(StringBuilder desc) {
		
		if(modules != null && modules.size() == 0) {
			return;
		}
		StringBuilder descTmp = new StringBuilder("");
		String connector = (modules.size() == 1)?" this new module":" these new modules";
		descTmp.append("The commit includes" + connector + ": \n\n");
		for (Module module : modules) {
			if(!descTmp.toString().contains("\t- " + module.getModuleName() + "\n")) {
				descTmp.append("\t- " + module.getModuleName() + "\n");
			}
		}
		descTmp.append("\n");
		
		desc.append(descTmp);
	}

	protected void getNewModules() {
		for (StereotypeIdentifier identifier : identifiers) {
			try {
				IType[] allTypes = identifier.getCompilationUnit().getAllTypes();
				
				for (IType iType : allTypes) {
					String packageName = iType.getPackageFragment().getElementName();
					String extractedName = packageName.substring(packageName.lastIndexOf(".") + 1, packageName.length());
					
					Module module = new Module();
					module.setModuleName(extractedName);
					module.setPackageName(packageName);
					
					if(!modules.contains(module)) {
						modules.add(module);
					}
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			if(changedListDialog.getAuthorText().getText() != null && !changedListDialog.getAuthorText().getText().equals("")) { 
				previousType = Utils.getFileContentOfCommitID(file.getPath(), getGit().getRepository(), changedListDialog.getAuthorText().getText());
			} else {
				previousType = Utils.getFileContentOfLastCommit(file.getPath(), getGit().getRepository());
			}
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

	public void summarizeType(StereotypeIdentifier identifier) throws JavaModelException {
		if(identifier.getStereotypedElements().size() == 0) {
			typesProblem.add(identifier);
		}
		int i = 0;
		for(StereotypedElement element : identifier.getStereotypedElements()) {
				SummarizeType summarizeType = new SummarizeType(element, identifier, differences);
				if(i > 0) {
					summarizeType.setLocal(true);
				} else {
					summarizeType.setLocal(false);
				}
				if(!identifier.getScmOperation().equals(TypeChange.MODIFIED.toString())) {
					summarizeType.generate();
					identifier.getBuilder().append(summarizeType.getBuilder().toString());
				}
				
				String packageName = element.getFullyQualifiedName();
				
				if(!summarized.containsKey(packageName) && !summarized.containsValue(identifier)) {
					
					summarized.put(packageName, identifier);
				}
				i++;
				
		}
	}
	
	@SuppressWarnings("unchecked")
	public String summarizeCommitStereotype() {
		List<StereotypedMethod> methods = new ArrayList<StereotypedMethod>();
		String result = "";
		
		for(StereotypeIdentifier identifier : identifiers) {

			for(StereotypedElement element : identifier.getStereotypedElements()) {
				if(!identifier.getScmOperation().equals(TypeChange.MODIFIED.name()) && !identifier.getChangedFile().isRenamed()) {
					methods.addAll((Collection<? extends StereotypedMethod>) element.getStereoSubElements());
				} else {
					List<StructureEntityVersion> modifiedMethods = identifier.getChangedFile().getModifiedMethods();
					if (modifiedMethods != null) {
						for (StructureEntityVersion structureEntityVersion : modifiedMethods) {
							StereotypedElement stereotypedMethod = getStereotypedElementFromName(
									element,
									structureEntityVersion);
							if(stereotypedMethod != null) {
								methods.add((StereotypedMethod) stereotypedMethod);
							}
						}
					}
				}
			}
		}
		if(methods.size() > 0) {
			StereotypedCommit stereotypedCommit = new StereotypedCommit(methods);
			stereotypedCommit.buildSignature();
			CommitStereotype stereotype = stereotypedCommit.findStereotypes();
			
			if(stereotype != null) {
				result = CommitStereotypeDescriptor.describe(stereotypeIdentifier.getCompilationUnit() ,stereotypedCommit);
			} else {
				result = "Not found commit stereotype. ";
			}
			changedListDialog.setSignatureMap(stereotypedCommit.getSignatureMap());
		} else {
			changedListDialog.setSignatureMap(new TreeMap<MethodStereotype, Integer>());
		}
		
		return result;
	}
	
	public StereotypedElement getStereotypedElementFromName(StereotypedElement element, StructureEntityVersion searchedElement) {
		StereotypedElement result =  null;
		if(element.getStereoSubElements() != null) {
			for (StereotypedElement stereotyped : element.getStereoSubElements()) { 
				//System.out.println(stereotyped.getFullyQualifiedName() + " / " + searchedElement.getJavaStructureNode().getFullyQualifiedName());
				if(stereotyped.getFullyQualifiedName().equals(searchedElement.getJavaStructureNode().getFullyQualifiedName()) || 
						searchedElement.getJavaStructureNode().getFullyQualifiedName().endsWith(stereotyped.getFullyQualifiedName())) {
					result = stereotyped;
					break;
				}
			}
		}
		return result;
	}
	
	/*ppublic void rebuildVersion() {
		
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
			//rules.addAll(result);
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
	}
	
	protected ICompilationUnit findRefactorings(ChangedFile file) {
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

	rotected void deleteTmpProject() {
		IProject project = createProject();
		try {
			project.delete(true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public IProject createProject() {
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
		
		if(scmOperation.equals(TypeChange.ADDED.toString()) ||scmOperation.equals(TypeChange.UNTRACKED.toString()) || scmOperation.equals(TypeChange.MODIFIED.toString())) {
			stereotypeIdentifier = getAddedStereotypeIdentifier(file);
		} else if(scmOperation.equals(TypeChange.REMOVED.toString())) {
			stereotypeIdentifier = getRemovedStereotypeIdentifier(file);
		} 
		
		stereotypeIdentifier.identifyStereotypes();
		stereotypeIdentifier.setScmOperation(scmOperation);
		stereotypeIdentifier.setChangedFile(file);
		
		identifiers.add(stereotypeIdentifier);
		
		return stereotypeIdentifier;
	}
	
	public StereotypeIdentifier getAddedStereotypeIdentifier(ChangedFile file) {
		try {
			String projectName;
			IResource res;
			if(changedListDialog.getSelection() != null) {
				projectName = changedListDialog.getSelection().getElementName();
				
				if(file.getPath().startsWith(projectName)) {
					res = changedListDialog.getSelection().getProject().findMember(file.getPath().replaceFirst(projectName, ""));
				} else {
					res = changedListDialog.getSelection().getProject().findMember(file.getPath());
				}
				stereotypeIdentifier = new StereotypeIdentifier((ICompilationUnit) JavaCore.create(res, changedListDialog.getSelection()), 0, 0);
			} else {
				projectName = ProjectInformation.getProject(ProjectInformation.getSelectedProject()).getName();
				res = ProjectInformation.getProject(ProjectInformation.getSelectedProject()).findMember(file.getPath().replaceFirst(projectName, ""));
				IFile ifile = ProjectInformation.getSelectedProject().getWorkspace().getRoot().getFile(res.getFullPath());
				stereotypeIdentifier = new StereotypeIdentifier((ICompilationUnit) JavaCore.create(ifile), 0, 0);
			}
		} catch (RevisionSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return stereotypeIdentifier;
	}

	public StereotypeIdentifier getRemovedStereotypeIdentifier(ChangedFile file) {
		try {
			String removedFile = ""; //Utils.getStringContentOfLastCommit(file.getPath(), getGit().getRepository());
			if(older != null && !older.equals("")) { 
				removedFile = Utils.getStringContentOfCommitID(file.getPath(), getGit().getRepository(), older);
			} else {
				removedFile = Utils.getStringContentOfLastCommit(file.getPath(), getGit().getRepository());
			}
			IPackageFragment pack = null;
			String packageName = "";
			packageName = "commsummtmp." + CompilationUtils.getPackageNameFromStringClass(removedFile);
			IFolder folder = ((IJavaProject)changedListDialog.getSelection()).getProject().getFolder("src");
			pack = changedListDialog.getSelection().getPackageFragmentRoot(folder).createPackageFragment(packageName, true, null);
			String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1);
			ICompilationUnit cu = pack.createCompilationUnit(fileName, removedFile,true, null);
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
			String removedFile = "";//Utils.getStringContentOfLastCommit(file.getPath(), getGit().getRepository());
			if(changedListDialog.getAuthorText().getText() != null && !changedListDialog.getAuthorText().getText().equals("")) { 
				removedFile = Utils.getStringContentOfCommitID(file.getPath(), getGit().getRepository(), changedListDialog.getAuthorText().getText());
			} else {
				removedFile = Utils.getStringContentOfLastCommit(file.getPath(), getGit().getRepository());
			}
			
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

	public DescribeVersionsDialog getChangedListDialog() {
		return changedListDialog;
	}

	public void setChangedListDialog(DescribeVersionsDialog changedListDialog) {
		this.changedListDialog = changedListDialog;
	}

	public LinkedList<ChangedFile> getModifiedFiles() {
		return modifiedFiles;
	}

	public void setModifiedFiles(LinkedList<ChangedFile> modifiedFiles) {
		this.modifiedFiles = modifiedFiles;
	}

	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

}
