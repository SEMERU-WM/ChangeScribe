package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.util;

public class StringUtils {
	
	public static String clearLastCharacterInText(String text, String character) {
		if(text.endsWith(character)) {
			text = text.substring(0, text.length() - 2);
		}
		return text;
		
	}

}
