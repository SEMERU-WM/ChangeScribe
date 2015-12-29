package co.edu.unal.colswe.changescribe.core.impactanalysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceField;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.stereotype.stereotyped.StereotypeIdentifier;

@SuppressWarnings("restriction")
public class Impact {
	
	private List<StereotypeIdentifier> identifiers;
	private long impactSet;
	private long total;
	private IProject project;

	public Impact(List<StereotypeIdentifier> identifiers) {
		super();
		this.setIdentifiers(identifiers);
	}

	public void calculateImpactSet() {
		calculateSizeModifiedMethods();
		searchImpactSet();
		calculateImpactPercenje();
	}
	
	public void searchImpactSet() {
		for (final StereotypeIdentifier identifier : identifiers) {
			SearchRequestor findMethod = new SearchRequestor() {
	            @SuppressWarnings("unused")
				@Override
	            public void acceptSearchMatch(SearchMatch match) throws CoreException {
	            	IJavaElement type = null;
	            	if(match.getElement() instanceof SourceType) {
                		addMatched(identifier, match);
	            	} else if(match.getElement() instanceof ResolvedSourceType) {
	            		type = ((ResolvedSourceType )match.getElement()).getParent();
	            		addMatched(identifier, match);
	            	} else if(match.getElement() instanceof ResolvedSourceField) {
	            		type = ((ResolvedSourceField)match.getElement()).getParent();
	            		addMatched(identifier, match);
	            	} else if(match.getElement() instanceof ResolvedSourceMethod) {
	            		type = ((ResolvedSourceMethod)match.getElement()).getParent();
	            		addMatched(identifier, match);
	            	}
	            }
	        };
	        SearchEngine engine = new SearchEngine();
	        IJavaSearchScope workspaceScope = null;
	        
	        if(getProject() != null) {
	        	workspaceScope = SearchEngine.createJavaSearchScope(createSearchScope());
	        } else {
	        	workspaceScope = SearchEngine.createWorkspaceScope();
	        }
	        
	        String typeName = Constants.EMPTY_STRING;
	        if(null != identifier.getCompilationUnit() && identifier.getCompilationUnit().findPrimaryType() != null) {
	        	typeName = identifier.getCompilationUnit().findPrimaryType().getElementName();
	        } else {
	        	if(null != identifier.getCompilationUnit()) {
		        	typeName = identifier.getCompilationUnit().getElementName();
		        	if(typeName.endsWith(Constants.JAVA_EXTENSION)) {
		        		typeName = typeName.replace(Constants.JAVA_EXTENSION, Constants.EMPTY_STRING);
		        	}
	        	}
	        }
	        
	        SearchPattern pattern = SearchPattern
	                .createPattern(
	                		typeName,
	                        IJavaSearchConstants.TYPE,
	                        IJavaSearchConstants.REFERENCES,
	                        SearchPattern.R_EXACT_MATCH);
	        SearchParticipant[] participant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
	        try {
				engine.search(pattern, participant, workspaceScope, findMethod, new NullProgressMonitor()); 
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public IJavaElement[] createSearchScope() {
		final IJavaElement[] scope = new IJavaElement[identifiers.size()];
		String projectName = project.getName();
		int i = 0;
		for(final StereotypeIdentifier cf : identifiers) {
			if(cf.getChangedFile().getPath().startsWith(projectName)) {
				scope[i] = JavaCore.create(project.findMember(cf.getChangedFile().getPath().replaceFirst(projectName, Constants.EMPTY_STRING)));
			} else if(null != cf.getCompilationUnit()) {
				scope[i] = cf.getCompilationUnit();
			} else {
				scope[i] = JavaCore.create(project.findMember(cf.getChangedFile().getPath()));
			}
			
			i++;
		}
		System.out.println("SEARCHING IMPACT INTO A SET OF " + i + " FILES");
		return scope;
	}
	
	public void calculateImpactPercenje() {
		double total = 0d;
		for(StereotypeIdentifier identifier : identifiers) {
			double methods = 0d;
			if(identifier.getRelatedTypes() != null) {
				for(SearchMatch match : identifier.getRelatedTypes()) {
					IType[] types = null;
					try {
						if(match.getElement() instanceof SourceType) {
							types = ((SourceType) match.getElement()).getCompilationUnit().getAllTypes();
						} else if(match.getElement() instanceof ResolvedSourceField) {
							types = ((SourceField) match.getElement()).getCompilationUnit().getAllTypes();
						} else if(match.getElement() instanceof ResolvedSourceMethod) {
							types = ((SourceMethod) match.getElement()).getCompilationUnit().getAllTypes();
						} else {
							System.out.println("hola");
						}
						for (IType iType : types) {
							IType type = null;
							@SuppressWarnings("unused")
							String typeName = Constants.EMPTY_STRING;
							if(identifier.getCompilationUnit().getAllTypes() != null && identifier.getCompilationUnit().getAllTypes().length > 0) {
								type = identifier.getCompilationUnit().getAllTypes()[0];
								typeName = type.getFullyQualifiedName();
							}
							methods += iType.getMethods().length;
						}
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				identifier.setImpactPercentaje((methods / getTotal()) * 100);
			} else {
				identifier.setImpactPercentaje(0d);
			}
			total += identifier.getImpactPercentaje();
			System.out.println("Impact (" + methods + " / " + getTotal() + ")" + identifier.getCompilationUnit().getElementName() + ": " + identifier.getImpactPercentaje());
		}
		System.out.println(total);
	}
	
	public void addMatched(StereotypeIdentifier identifier, SearchMatch match) {
		
		if(identifier.getRelatedTypes() == null) {
			identifier.setRelatedTypes(new ArrayList<SearchMatch>());
		}
		if(!included(match, identifier.getRelatedTypes())) {
			identifier.getRelatedTypes().add(match);
		}
	}
	
	public boolean included(SearchMatch matchVerify, List<SearchMatch> matches) {
		boolean included = false;
		for (SearchMatch match : matches) {
			if(match.getResource() == matchVerify.getResource()) {
				included = true;
				break;
			}
		}
		
		return included;
	}
	
	public void calculateSizeModifiedMethods() {
		for (StereotypeIdentifier identifier : identifiers) {
			try {
				if (null != identifier.getCompilationUnit() && identifier.getCompilationUnit().exists()) {
					IType[] allTypes = identifier.getCompilationUnit().getAllTypes();
					for (IType iType : allTypes) {
						if(iType.isAnonymous()) {
							System.out.println("INNER");
						}
						IMethod[] methods = iType.getMethods();
						if (methods != null) {
							//se acumula la cantidad total de métodos afectados por el conjunto de cambios.
							total += methods.length;
						}
						IJavaElement[] children = iType.getChildren();
						for (IJavaElement childrenElement : children) {
							if(childrenElement instanceof SourceType) {
								total += ((SourceType) childrenElement).getMethods().length;
							}
						}
					}
				}
				
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Total methods: " + total);
	}

	public long getImpactSet() {
		return impactSet;
	}

	public void setImpactSet(long impactSet) {
		this.impactSet = impactSet;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<StereotypeIdentifier> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(List<StereotypeIdentifier> identifiers) {
		this.identifiers = identifiers;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
}
