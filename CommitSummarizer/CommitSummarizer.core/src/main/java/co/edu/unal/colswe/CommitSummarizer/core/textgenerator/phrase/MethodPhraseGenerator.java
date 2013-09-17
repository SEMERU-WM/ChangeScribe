package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.POSTagger;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.Tag;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.TaggedTerm;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.tokenizer.Tokenizer;

public class MethodPhraseGenerator implements PhraseGenerator {
	
	private static POSTagger tagger;
	
	public enum MethodPhraseType {
		BASIC, COMPLETE
	}

	@Override
	public String generate(String text, String type, StereotypedElement element, StereotypedElement parent) {
		MethodDeclaration method = (MethodDeclaration) element.getElement();
		if(type.equals(MethodPhraseType.BASIC.toString())) {
			generateSimpleDescription(method);
		} else {
			//TODO generate description to body method
		}
		
		return null;
	}
	
	private static String generateSimpleDescription(MethodDeclaration method) {
		String methodName = Tokenizer.split(method.getName().getIdentifier());
		String returnType = Tokenizer.split(method.getReturnType2().toString());
		String className = Tokenizer.split(((TypeDeclaration) method.getParent()).getName().toString());
		String methodArguments = method.parameters().size() == 0 ? "" : Tokenizer.split(getMethodParams(method.parameters()));
		
		String pset = "";
		String verb = "";
		String doValue = "";
		
		if(method.isConstructor()) {
			pset = methodName  + " " + methodArguments; //NPs
			//System.out.println("IS CONSTRUCTOR: " + pset);
		} else if(PhraseUtils.hasTrailingPastParticiple(getTaggerText(methodName).get(methodName.split("\\s").length - 1))) {
			pset = methodName; //NP
			//System.out.println("has trailing past participle: " + pset);
		} else if(PhraseUtils.hasLeadingPreposition(getTaggerText(methodName).get(0))) {
			pset = className + " " + methodName; //NP
			//System.out.println("has leading preposition: " + pset);
		} else if(PhraseUtils.hasLeadingVerb(getTaggerText(methodName).get(0))) {
			List<TaggedTerm> methodTagger = getTaggerText(methodName);
			verb = methodTagger.get(0).getTerm();
			
			if(methodTagger.size() > 1 && PhraseUtils.hasObjectInName(getTaggerText(methodName).get(1))) {
				doValue = getTaggerText(methodName).get(1).getTerm();
			} else if(method.parameters().size() > 0) {
				
				doValue = getFirstFormalName(method) + " " + getFirstFormalType(method);
			} else {
				doValue = className;
			}
			
			if(PhraseUtils.containsPrepositions(methodTagger)) {
				int i = 0;
				for(TaggedTerm term : methodTagger) {
					String doTemp = "";
					String ioTemp = "";
					if(Tag.isPrep(term.getTag())) {
						doTemp = getWordsBeforePrep(doValue, methodTagger, i);
						ioTemp = getWordsAfterPrep(doValue, methodTagger, i);
						pset = pset + " " + inferArguments(verb, doTemp, methodTagger, ioTemp, methodArguments);
					}
					i++;
				}
			} else {
				pset = inferArguments(verb, doValue, null, "", methodArguments);
			}

		} else {
			pset = pset + " " + methodName; //NP
		}
		
		System.out.println("METHOD: " + method.getReturnType2() + " " + method.getName().getFullyQualifiedName() + "(" + getMethodParamsString(method.parameters()) + ") "  + " PHRASE: " + pset);
		return pset;
	}
	
	private static String inferArguments(String verb, String doValue, List<TaggedTerm> methodTagger, String ioValue, String methodArguments) {
		String phrase = "";
		if(methodTagger != null) {
			for(TaggedTerm term : methodTagger) {
				phrase += " " + verb + term.getTerm() + " " + doValue + " " + ioValue;
			}
		} else {
			phrase = verb + " " + doValue;
		}
		
		return phrase;
	}
	
	private static String getWordsBeforePrep(String doValue, List<TaggedTerm> methodTagger, int index) {
		String words = "";
		for(int j = 0; j < index; j++) {
			words += " " + methodTagger.get(j).getTerm();
		}
		
		return words;
	}
	
	private static String getWordsAfterPrep(String doValue, List<TaggedTerm> methodTagger, int index) {
		String words = "";
		for(int j = index; j < methodTagger.size(); j++) {
			words += " " + methodTagger.get(j).getTerm();
		}
		return words;
	}
	
	private static String getFirstFormalName(MethodDeclaration method) {
		SingleVariableDeclaration param = (SingleVariableDeclaration) method.parameters().get(0); 
		return param.getName().getFullyQualifiedName();
	}
	
	private static String getFirstFormalType(MethodDeclaration method) {
		SingleVariableDeclaration param = (SingleVariableDeclaration) method.parameters().get(0); 
		return param.getType().toString();
	}
	
	private static LinkedList<TaggedTerm> getTaggerText(String phrase) {
		return tagger.tag(phrase);
	}
	
	public static String getReturnType(String method) {
		return method.substring(0, method.indexOf(" "));
	}
	
	public static String getMethodParams(List<SingleVariableDeclaration> params) {
		String paramsConcat = "";
		for(SingleVariableDeclaration param : params) {
			paramsConcat += " " + param.getType().toString() + " " + param.getName();
		}
		
		return paramsConcat.trim();
		
	}
	
	public static String getMethodParamsString(List<SingleVariableDeclaration> params) {
		String paramsConcat = "";
		for(SingleVariableDeclaration param : params) {
			paramsConcat += " " + param.getType().toString() + " " + param.getName() + ", ";
		}
		
		return paramsConcat.trim();
		
	}

}
