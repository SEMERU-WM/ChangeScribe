package co.edu.unal.colswe.changescribe.core.textgenerator.phrase.util;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.textgenerator.phrase.Parameter;
import co.edu.unal.colswe.changescribe.core.textgenerator.pos.TaggedTerm;
import co.edu.unal.colswe.changescribe.core.textgenerator.tokenizer.Tokenizer;

public class MethodPhraseUtils {
	
	public static String getWordsBeforePrep(String doValue, List<TaggedTerm> methodTagger, int index) {
		String words = Constants.EMPTY_STRING;
		for(int j = 0; j < index - 1; j++) {
			if(!words.contains(methodTagger.get(j).getTerm())) {
				words += " " + methodTagger.get(j).getTerm();
			}
		}
		
		return words;
	}
	
	public static String getWordsAfterPrep(String doValue, List<TaggedTerm> methodTagger, int index) {
		String words = Constants.EMPTY_STRING;
		for(int j = index + 1; j < methodTagger.size(); j++) {
			words += " " + methodTagger.get(j).getTerm();
		}
		return words;
	}
	
	public static String getFirstFormalName(MethodDeclaration method) {
		SingleVariableDeclaration param = (SingleVariableDeclaration) method.parameters().get(0); 
		return Tokenizer.split(param.getName().getFullyQualifiedName());
	}
	
	public static String getFirstFormalType(MethodDeclaration method) {
		SingleVariableDeclaration param = (SingleVariableDeclaration) method.parameters().get(0);
		String firstFormalType = Constants.EMPTY_STRING;
		Parameter parameter = new Parameter(param.getType().toString(), param.getName().getFullyQualifiedName());
		if(!parameter.isPrimitive()) {
			firstFormalType = param.getType().toString();
		}
		return Tokenizer.split(firstFormalType);
	}
	
	public static String getMethodParams(List<SingleVariableDeclaration> params) {
		String paramsConcat = Constants.EMPTY_STRING;
		for(SingleVariableDeclaration param : params) {
			paramsConcat += " " + param.getType().toString() + " " + param.getName();
		}
		
		return paramsConcat.trim();
	}
	
	public static String getMethodSignature(MethodDeclaration method) {
		String signature = method.getName().getFullyQualifiedName() + " (";
		@SuppressWarnings("unchecked")
		List<SingleVariableDeclaration> params = method.parameters();
		for(SingleVariableDeclaration param : params) {
			signature += " " + param.getType().toString() + " "  + ", ";
		}
		if(signature.endsWith(", ")) {
			signature = signature.substring(0, signature.length() - 2);
		}
		return signature + ")";
	}
	
	public static String getMethodParamsString(List<SingleVariableDeclaration> params) {
		String paramsConcat = Constants.EMPTY_STRING;
		for(SingleVariableDeclaration param : params) {
			paramsConcat += " " + param.getType().toString() + " " + param.getName() + ", ";
		}
		return paramsConcat.trim();
	}
}
