package co.edu.unal.colswe.CommitSummarizer.core.dependencies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeReferenceMatch;
import org.eclipse.jdt.internal.core.ResolvedSourceType;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;

@SuppressWarnings("restriction")
public class TypeDependencySummary implements DependencySummary {
	
	private ChangedFile changedFile;
	private IJavaElement element;
	private List<SearchMatch> dependencies;
	private StringBuilder builder;

	public TypeDependencySummary(ChangedFile changedFile, IJavaElement element) {
		this.changedFile = changedFile;
		this.element = element;
		this.setDependencies(new ArrayList<SearchMatch>());
	}

	@Override
	public void find() {
		if(changedFile == null) {
			return;
		}
		SearchRequestor requestor = new SearchRequestor() {
			@Override
            public void acceptSearchMatch(SearchMatch match) {
            	if(match.getAccuracy() == SearchMatch.A_ACCURATE) {
            		if(match.getElement() instanceof ResolvedSourceType) {
                		System.out.println(match.toString());
                		addMatched(match);
                	}
            	}
            }
        };
		SearchEngine searchEngine = new SearchEngine();
		try {
			searchEngine.searchDeclarationsOfReferencedTypes(getElement(), requestor, new NullProgressMonitor());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void generateSummary() {
		if(getDependencies() != null && getDependencies().size() > 0) {
			builder = new StringBuilder("Related with: \n\n");
		}
		
		for (SearchMatch match : getDependencies()) {
			ResolvedSourceType type = (ResolvedSourceType) match.getElement();
			IResource resource = match.getResource();
			try {
				type.getTypes();
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(match.isInsideDocComment()) {
				builder.append("\t" + " Referenced in comments of " +type.getFullyQualifiedName('.') + " class \n");
			} else if(match.isImplicit()) {
				builder.append("\t" + " Implicit reference in " + type.getFullyQualifiedName('.') + " class \n");
			} else {
				builder.append("\t" + type.getFullyQualifiedName('.') + " class \n");
			}
		}
	}
	
	public void addMatched(SearchMatch match) {
		this.getDependencies().add(match);
	}

	public ChangedFile getChangedFile() {
		return changedFile;
	}

	public void setChangedFile(ChangedFile changedFile) {
		this.changedFile = changedFile;
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
			string = builder.toString() + "\n";
		}
		return string;
	}

}
