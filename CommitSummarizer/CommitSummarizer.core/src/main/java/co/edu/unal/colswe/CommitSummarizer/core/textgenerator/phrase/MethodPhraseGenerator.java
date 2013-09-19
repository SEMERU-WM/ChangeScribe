package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase;

import java.util.ArrayList;
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
	
	
	public enum MethodPhraseType {
		BASIC, COMPLETE
	}

	@Override
	public String generate(String text, String type, StereotypedElement element, StereotypedElement parent) {
		MethodDeclaration method = (MethodDeclaration) element.getElement();
		String description = "";
		if(type.equals(MethodPhraseType.BASIC.toString())) {
			description = generateSimpleDescription(method) + "\n";
		} else {
			//TODO generate description to body method
		}
		
		return description;
	}
	
	@SuppressWarnings("unchecked")
	private static String generateSimpleDescription(MethodDeclaration method) {
		String methodName = Tokenizer.split(method.getName().getIdentifier());
		String className = Tokenizer.split(((TypeDeclaration) method.getParent()).getName().toString());
		String methodArguments = method.parameters().size() == 0 ? "" : Tokenizer.split(getMethodParams(method.parameters()));
		
		String pset = "";
		String verb = "";
		String doValue = "";
		
		List<Parameter> parameters = new ArrayList<Parameter>();
		List<SingleVariableDeclaration> parameters2 = ((List<SingleVariableDeclaration>) method.parameters());
		for (int i = 0; i < parameters2.size(); i++) {
			SingleVariableDeclaration param = parameters2.get(i);
			Parameter parameter = new Parameter(param.getType().toString(), param.getName().getFullyQualifiedName());
			parameters.add(parameter);
		}
		
		if(method.isConstructor()) {
			pset = "create " + PhraseUtils.getIndefiniteArticle(methodName) + " " + methodName  + " "; //NPs
			String argsDescriptor = "";
			for(Parameter param : parameters) {
				String paramText = describeParam(param);
				if(!argsDescriptor.toLowerCase().contains(paramText.toLowerCase())) {
					if(!argsDescriptor.equals("")) {
						argsDescriptor += " and ";
					} else {
						argsDescriptor = " for ";
					}
					argsDescriptor += paramText;
				}
			}
			pset += argsDescriptor;
		} else if(PhraseUtils.hasTrailingPastParticiple(getTaggedText(methodName).get(methodName.split("\\s").length - 1))) {
			pset = methodName; //NP
		} else if(PhraseUtils.hasLeadingPreposition(getTaggedText(methodName).get(0))) {
			pset = className + " " + methodName; //NP
		} else if(PhraseUtils.hasLeadingVerb(getTaggedText(methodName).get(0))) {
			List<TaggedTerm> taggedMethod = getTaggedText(methodName);
			verb = taggedMethod.get(0).getTerm();
			if(PhraseUtils.hasObjectInName(getTaggedText(methodName))) {
				doValue = PhraseUtils.getObject(taggedMethod);
			} else if(method.parameters().size() > 0) {
				doValue = getFirstFormalName(method);
				String type = getFirstFormalType(method);
				if(!doValue.toLowerCase().contains(type.toLowerCase())) {
					doValue += " " + type;
				}
			} else {
				doValue = className;
			}
			if(PhraseUtils.containsPrepositions(taggedMethod)) {
				int i = 0;
				for(TaggedTerm term : taggedMethod) {
					String doTemp = "";
					String ioTemp = "";
					if(Tag.isPrep(term.getTag())) {
						doTemp = getWordsBeforePrep(doValue, taggedMethod, i);
						ioTemp = getWordsAfterPrep(doValue, taggedMethod, i);
						if(method.parameters().size() > 0) {
							pset = pset + " " + inferArguments(verb, doTemp, taggedMethod, ioTemp, methodArguments, method, "", parameters) + " " + term.getTerm() + " " + ioTemp;
						} else {
							pset = methodName;
						}
					}
					i++;
				}
			} else {
				String adjetive = "";
				if(PhraseUtils.containsAdjetives(taggedMethod)) {
					adjetive = PhraseUtils.getAdjetive(taggedMethod);
				} 
				if(parameters.size() == 0) {
					if(PhraseUtils.hasObjectInName(getTaggedText(methodName))) {
						pset = methodName + " " + " of " + className;
					} else {
						pset = methodName + " of " + doValue;
					}
					
				} else {
					pset = inferArguments(verb, doValue, null, "", methodArguments, method, adjetive, parameters);
				}
			}
		} else {
			pset = pset + " " + methodName; //NP
		}
		System.out.println("METHOD: " + method.getReturnType2() + " " + method.getName().getFullyQualifiedName() + "(" + getMethodParamsString(method.parameters()) + ") "  + " PHRASE: " + pset);
		return pset;
	}
	
	private static String inferArguments(String verb, String doValue, List<TaggedTerm> taggedMethod, String ioValue, String methodArguments, MethodDeclaration method, String adjetive, List<Parameter> parameters) {
		String phrase = "";
		String originalPhrase = "";
		int i = 0;
		phrase += "" + verb;
		for(Parameter param : parameters) {
			if(taggedMethod != null && i < 2) {
				if(doValue.toLowerCase().contains(param.getTypeName().toLowerCase()) || ioValue.toLowerCase().contains(param.getVariableName().toLowerCase())) {
					phrase += "" + originalPhrase + " " + doValue + " " + describeParam(param); 
				} else {
					phrase += " " + describeParam(param);
				}
			} else if(i < 2) {
				phrase += " " + adjetive;
				if(!phrase.contains(doValue.toLowerCase().trim())) {
					phrase += " " + doValue;
				}
				
				if(!phrase.toLowerCase().contains(describeParam(param).toLowerCase().trim())) {
					if(verb.trim().equals("is")) {
						phrase = describeParam(param) + " " + phrase;
					} else {
						phrase += " of " + describeParam(param);
					}
				}
				
			} 
			i++;
		} 
		if(phrase.equals("")) {
			phrase = "nothing description";
		}
		return phrase;
	}
	
	private static String describeParam(Parameter parameter) {
		String description = "";
        final ParameterPhrase varGen = new ParameterPhrase(parameter);
        varGen.generate();
        description = varGen.toString();
		return description; 
	}
	
	private static String getWordsBeforePrep(String doValue, List<TaggedTerm> methodTagger, int index) {
		String words = "";
		for(int j = 0; j < index - 1; j++) {
			if(!words.contains(methodTagger.get(j).getTerm())) {
				words += " " + methodTagger.get(j).getTerm();
			}
		}
		
		return words;
	}
	
	private static String getWordsAfterPrep(String doValue, List<TaggedTerm> methodTagger, int index) {
		String words = "";
		for(int j = index + 1; j < methodTagger.size(); j++) {
			words += " " + methodTagger.get(j).getTerm();
		}
		return words;
	}
	
	private static String getFirstFormalName(MethodDeclaration method) {
		SingleVariableDeclaration param = (SingleVariableDeclaration) method.parameters().get(0); 
		return Tokenizer.split(param.getName().getFullyQualifiedName());
	}
	
	private static String getFirstFormalType(MethodDeclaration method) {
		SingleVariableDeclaration param = (SingleVariableDeclaration) method.parameters().get(0);
		String firstFormalType = "";
		Parameter parameter = new Parameter(param.getType().toString(), param.getName().getFullyQualifiedName());
		if(!parameter.isPrimitive()) {
			firstFormalType = param.getType().toString();
		}
		return Tokenizer.split(firstFormalType);
	}
	
	private static LinkedList<TaggedTerm> getTaggedText(String phrase) {
		return POSTagger.tag(phrase);
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
