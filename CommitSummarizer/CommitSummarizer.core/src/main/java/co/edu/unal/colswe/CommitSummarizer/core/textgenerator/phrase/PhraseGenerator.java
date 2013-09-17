package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;

public interface PhraseGenerator {
		
	public String generate(String text, String type, StereotypedElement element, StereotypedElement parent);

	

}
