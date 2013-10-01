package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import org.eclipse.jdt.core.dom.CompilationUnit;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.information.TypeInfo;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedType;

public class GeneralDescriptor {
	
	public static String describe(StereotypedElement element, CompilationUnit cu, String operation) {
		StringBuilder description = new StringBuilder();
		if(((StereotypedType)element).getRelatedTypes() != null && ((StereotypedType)element).getRelatedTypes().size() > 0) {
			description.append("A ");
			for(TypeInfo type : ((StereotypedType)element).getRelatedTypes()) {
				if(type.getTypeBinding().isInterface()) {
					description.append(type.getTypeBinding().getName() + " ");
				}
			}
			description.append("implementation \n");
		} else {
			description.append(". The " + element.getStereotypes() + " class " + element.getName().toString() + " was " + operation + ". This class allows: \n");
		}
		
		return description.toString();
	}

}
