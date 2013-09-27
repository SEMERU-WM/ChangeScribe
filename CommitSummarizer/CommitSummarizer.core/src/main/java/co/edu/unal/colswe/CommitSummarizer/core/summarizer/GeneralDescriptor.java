package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import org.eclipse.jdt.core.dom.CompilationUnit;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;

public class GeneralDescriptor {
	
	public static String describe(StereotypedElement element, CompilationUnit cu, String operation) {
		StringBuilder description = new StringBuilder();
		description.append("The " + element.getStereotypes() + " class " + element.getName().toString() + " was " + operation + ". This class allows: \n");
		return description.toString();
	}

}
