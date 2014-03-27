package co.edu.unal.colswe.CommitSummarizer.core.dependencies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.NamedMember;
import org.eclipse.jdt.internal.core.ResolvedSourceField;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.swt.widgets.Display;

import co.edu.unal.colswe.CommitSummarizer.core.ast.ProjectInformation;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.util.PhraseUtils;

@SuppressWarnings("restriction")
public class TypeDependencySummary implements DependencySummary {
	
	private IJavaElement element;
	private List<SearchMatch> dependencies;
	private StringBuilder builder;
	private ChangedFile[] differences;
	private String operation;
	private IProject project;

	public TypeDependencySummary(IJavaElement element, String operation) {
		this.element = element;
		this.operation = operation;
		this.setDependencies(new ArrayList<SearchMatch>());
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				project = ProjectInformation.getProject(ProjectInformation.getSelectedProject());
			}
		});
	}

	@Override
	public void find() {
		
        SearchEngine engine = new SearchEngine();
        IJavaSearchScope workspaceScope = null;
        
        if(project != null) {
        	workspaceScope = SearchEngine.createJavaSearchScope(createSearchScope());
        } else {
        	workspaceScope = SearchEngine.createWorkspaceScope();
        }
        
        SearchPattern pattern = SearchPattern.createPattern(
                		getElement().getPrimaryElement().getElementName().replace(".java", ""),
                        IJavaSearchConstants.TYPE,
                        IJavaSearchConstants.REFERENCES,
                        SearchPattern.R_EXACT_MATCH);
        SearchParticipant[] participant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        try {
			engine.search(pattern, participant, workspaceScope, createSearchRequestor(), new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SearchRequestor createSearchRequestor() {
		SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch match) throws CoreException {
            	IJavaElement type = null;
            	if(match.getElement() instanceof ResolvedSourceMethod) {
            		type = ((ResolvedSourceMethod )match.getElement()).getParent();
            	} else if(match.getElement() instanceof ResolvedSourceType) {
            		type = ((ResolvedSourceType )match.getElement()).getParent();
            	} else if(match.getElement() instanceof ResolvedSourceField) {
            		type = ((ResolvedSourceField)match.getElement()).getParent();
            	} /*else {
            		System.out.println("hola");
            	}*/
            	if(null != type && inChangedFiles(type.getElementName())) {
            		addMatched(match);
            	} 
            }
        };
        return requestor;
	}
	
	public IJavaElement[] createSearchScope() {
		final IJavaElement[] scope = new IJavaElement[differences.length];
		String projectName = project.getName();
		int i = 0;
		for(final ChangedFile cf : differences) {
			if(cf.getPath().startsWith(projectName)) {
				scope[i] = JavaCore.create(project.findMember(cf.getPath().replaceFirst(projectName, "")));
			} else {
				scope[i] = JavaCore.create(project.findMember(cf.getPath()));
			}
			
			i++;
		}
		return scope;
	}
	
	@Override
	public void generateSummary() {
		if(getDependencies() != null && getDependencies().size() > 0) {
			String lead = "";
			if(operation.equals(TypeChange.REMOVED.toString())) {
				lead = "\nWas referenced by:";
			} else {
				lead = "\nReferenced by:";
			}
			builder = new StringBuilder(lead +"\n");
		}
		
		for (SearchMatch match : getDependencies()) {
			NamedMember type = null;
        	if(match.getElement() instanceof ResolvedSourceMethod) {
        		type = ((ResolvedSourceMethod )match.getElement());
        	} else if(match.getElement() instanceof ResolvedSourceType) {
        		type = ((ResolvedSourceType )match.getElement());
        	} else if(match.getElement() instanceof ResolvedSourceField) {
        		type = ((ResolvedSourceField)match.getElement());
        	}

			if(match.isInsideDocComment()) {
				builder.append("\t" + " Referenced in comments of " + type.getParent().getElementName() + " " + PhraseUtils.getStringType(type.getDeclaringType()) + "\n");
			} else if(match.isImplicit()) {
				builder.append("\t" + " Implicit reference in " + type.getParent().getElementName() + " " + PhraseUtils.getStringType(type.getDeclaringType()) + "\n");
			} else if(!builder.toString().contains("\t" + type.getParent().getElementName() + " " + PhraseUtils.getStringType(type.getDeclaringType()) + "\n")){
				builder.append("\t" + type.getParent().getElementName() + " " + PhraseUtils.getStringType(type.getDeclaringType()) + "\n");
			}
		}
	}
	
	public void addMatched(SearchMatch match) {
		if(!included(match)) {
			this.getDependencies().add(match);
		}
	}
	
	public boolean included(SearchMatch matchVerify) {
		boolean included = false;
		for (SearchMatch match : this.getDependencies()) {
			if(match.getResource() == matchVerify.getResource()) {
				included = true;
				break;
			}
		}
		
		return included;
	}

	public IJavaElement getElement() {
		return element;
	}

	public void setElement(IJavaElement element) {
		this.element = element;
	}

	public List<SearchMatch> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<SearchMatch> dependencies) {
		this.dependencies = dependencies;
	}
	
	@Override
	public String toString() {
		String string = "";
		if(builder != null && !builder.toString().equals("")) {
			string = builder.toString();
		}
		return string;
	}

	public ChangedFile[] getDifferences() {
		return differences;
	}

	public void setDifferences(ChangedFile[] differences) {
		this.differences = differences;
	}
	
	private boolean inChangedFiles(String file) {
		boolean exist = false;
		for(ChangedFile cf : differences) {
			if(cf.getPath().endsWith("/" + file + ".java")) {
				exist = true;
				System.out.println("exist original: " + cf.getPath() + " searched: " + file);
				break;
			} 
		}
		return exist;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
