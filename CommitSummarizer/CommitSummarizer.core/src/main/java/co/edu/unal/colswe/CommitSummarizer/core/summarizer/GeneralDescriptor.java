package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedType;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.NounPhrase;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.util.PhraseUtils;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.tokenizer.Tokenizer;

public class GeneralDescriptor {
	
	public static String describe(StereotypedElement element, CompilationUnit cu, String operation) {
		StereotypedType type = (StereotypedType) element;
		StringBuilder description = new StringBuilder();
		ITypeBinding superclass = null; 
		ITypeBinding[] interfaces = null;
		if (type.getElement().resolveBinding() != null) {
			superclass = type.getElement().resolveBinding().getSuperclass();
			interfaces = type.getElement().resolveBinding().getInterfaces();
		}
		
		if(type.isInterface()) {
			description.append(describeInterface(type) + " ");
		} else {
			if (interfaces != null && superclass != null && interfaces.length != 0 && !superclass.getKey().equals("Ljava/lang/Object;")) {
                description.append(PhraseUtils.getImplementationDescription(interfaces));
                description.append(", and ");
                description.append(PhraseUtils.getExtensionDescription(superclass));
            } else if (interfaces != null && interfaces.length != 0) {
                description.append(PhraseUtils.getImplementationDescription(interfaces));
            } else if (superclass != null && !superclass.getKey().equals("Ljava/lang/Object;")) {
                description.append(PhraseUtils.getExtensionDescription(superclass));
            } else if (type.isBoundary()) {
                description.append("boundary class");
            } else if (type.isEntity() || type.isDataProvider() || type.isCommander()) {
                description.append("entity class");
            } else if (type.isMinimalEntity()) {
                description.append("trivial entity class");
            } else if (type.isFactory()) {
                description.append("object creator class");
            } else if (type.isController() || type.isPureController()) {
                description.append("controller class");
            } else if (type.isDataClass()) {
                description.append("data class");
            } else {
                description.append("class");
            } if (type.getElement().resolveBinding() != null &&Modifier.isAbstract(type.getElement().resolveBinding().getModifiers())) {
                description.insert(0, "an abstract ");
            } else {
                description.insert(0, PhraseUtils.getIndefiniteArticle(description.toString()).concat(" "));
            }
		}
		
		description.insert(0, describeOperation(operation) + " ");
		
		description.append(" for ");
		NounPhrase classNamePhrase = new NounPhrase(Tokenizer.split(type.getElement().getName().getFullyQualifiedName()));
		classNamePhrase.generate();
		description.append(classNamePhrase.toString());
		

		return description.toString();
	}
	
	private static String describeOperation(String operation) {
		String description = "";
		if(operation.equals(TypeChange.ADDED.toString()) || operation.equals(TypeChange.UNTRACKED.toString())) {
			description = "Add";
		} else if(operation.equals(TypeChange.REMOVED.toString())) {
			description = "Remove";
		}
		return description;
	}
	
	private static String describeInterface(StereotypedType type) {
		ITypeBinding[] interfaces = type.getElement().resolveBinding().getInterfaces();
        
        final StringBuilder template = new StringBuilder();
        if (interfaces.length > 0) {
            final String enumeratedTypes = PhraseUtils.enumeratedTypes(type.getElement().resolveBinding().getInterfaces());
            template.append(PhraseUtils.getIndefiniteArticle(enumeratedTypes));
            template.append(" ");
            template.append(enumeratedTypes);
            template.append(" ");
            template.append("interface extension");
        }
        else {
            template.append("An interface declaration");
        }
        return template.toString();
    }
	
}
