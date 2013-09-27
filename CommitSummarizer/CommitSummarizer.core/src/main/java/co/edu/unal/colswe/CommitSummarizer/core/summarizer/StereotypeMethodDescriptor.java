package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import java.util.List;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.MethodPhraseGenerator;

public class StereotypeMethodDescriptor {
	
	public static String describe(List<StereotypedElement> element) {
		String description = "";
		for (StereotypedElement method : element) {
			MethodPhraseGenerator phraseGenerator = new MethodPhraseGenerator(method, "BASIC");
			phraseGenerator.generate();
			description += phraseGenerator.getPhrase(); 
		}
		return "\t" + description;
	}

}
