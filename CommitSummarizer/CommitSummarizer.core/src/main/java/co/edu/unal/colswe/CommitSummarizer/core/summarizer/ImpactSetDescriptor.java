package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

import co.edu.unal.colswe.CommitSummarizer.core.dependencies.TypeDependencySummary;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;

public class ImpactSetDescriptor {
	
	public static String describe(ICompilationUnit cu, ChangedFile[] differences) {
		
		TypeDependencySummary dependency = new TypeDependencySummary((IJavaElement) cu);
		dependency.setDifferences(differences);
		dependency.find();
		dependency.generateSummary();
		
		return dependency.toString();
	}

}
