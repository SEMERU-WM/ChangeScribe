package co.edu.unal.colswe.changescribe.core.summarizer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

import co.edu.unal.colswe.changescribe.core.dependencies.TypeDependencySummary;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;

public class ImpactSetDescriptor {
	
	public static String describe(ICompilationUnit cu, ChangedFile[] differences, String operation) {
		
		TypeDependencySummary dependency = new TypeDependencySummary((IJavaElement) cu, operation);
		if(null != cu) {
			dependency.setDifferences(differences);
			dependency.find();
			dependency.generateSummary();
		}
		
		return dependency.toString();
	}
}
