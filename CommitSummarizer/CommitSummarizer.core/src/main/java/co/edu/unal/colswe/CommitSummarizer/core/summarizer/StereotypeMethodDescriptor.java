package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.MethodPhraseGenerator;

public class StereotypeMethodDescriptor {
	
	public static String describe(List<StereotypedElement> element) {
		String description = "";
		for (StereotypedElement method : element) {
			if(method.getElement() instanceof MethodDeclaration) {
				MethodPhraseGenerator phraseGenerator = new MethodPhraseGenerator(method, "BASIC");
				phraseGenerator.generate();
				description += phraseGenerator.getPhrase(); 
			}
		}
		return description;
	}

}
