package co.edu.unal.colswe.CommitSummarizer.core.dependencies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceType;

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

	public TypeDependencySummary(IJavaElement element, String operation) {
		this.element = element;
		this.operation = operation;
		this.setDependencies(new ArrayList<SearchMatch>());
	}

	@Override
	public void find() {
		SearchRequestor requestor = new SearchRequestor() {
			@Override
            public void acceptSearchMatch(SearchMatch match) {
            	if(match.getAccuracy() == SearchMatch.A_ACCURATE) {
            		if(match.getElement() instanceof ResolvedSourceType && inChangedFiles(match.getResource().getName())) {
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
			String lead = "";
			if(operation.equals(TypeChange.REMOVED.toString())) {
				lead = "Was referenced by:";
			} else {
				lead = "Referenced by:";
			}
			builder = new StringBuilder(lead +"\n");
		}
		
		for (SearchMatch match : getDependencies()) {
			ResolvedSourceType type = (ResolvedSourceType) match.getElement();

			if(match.isInsideDocComment()) {
				builder.append("\t" + " Referenced in comments of " + type.getFullyQualifiedName('.').replaceFirst("commsummtmp.", "") + " " + PhraseUtils.getStringType(type) + "\n");
			} else if(match.isImplicit()) {
				builder.append("\t" + " Implicit reference in " + type.getFullyQualifiedName('.').replaceFirst("commsummtmp.", "") + " " + PhraseUtils.getStringType(type) + "\n");
			} else {
				builder.append("\t" + type.getFullyQualifiedName('.').replaceFirst("commsummtmp.", "") + " " + PhraseUtils.getStringType(type) + "\n");
			}
		}
	}
	
	public void addMatched(SearchMatch match) {
		this.getDependencies().add(match);
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
			if(cf.getPath().endsWith("/" + file)) {
				exist = true;
				System.out.println("exist original: " + cf.getPath() + " searched: " + file);
				break;
			} 
		}
		return exist;
	}

}
