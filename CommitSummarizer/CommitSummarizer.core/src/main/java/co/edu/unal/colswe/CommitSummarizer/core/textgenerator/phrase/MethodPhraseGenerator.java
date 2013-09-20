package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.util.MethodPhraseUtils;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.util.PhraseUtils;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.POSTagger;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.Tag;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.TaggedTerm;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.tokenizer.Tokenizer;

public class MethodPhraseGenerator implements PhraseGenerator {
	
	private MethodDeclaration method;
	private final StereotypedElement element;
	private String type;
	private String phraseString;
	private List<Parameter> parameters = new ArrayList<Parameter>();
	private Phrase phrase;
	
	public MethodPhraseGenerator(StereotypedElement element, String type) {
		super();
		this.element = element;
		this.type = type;
		this.method = (MethodDeclaration)  element.getElement();
		setParameters();
	}
	
	@SuppressWarnings("unchecked")
	private void setParameters() {
		List<SingleVariableDeclaration> parameters2 = ((List<SingleVariableDeclaration>) getMethod().parameters());
		for (int i = 0; i < parameters2.size(); i++) {
			SingleVariableDeclaration param = parameters2.get(i);
			Parameter parameter = new Parameter(param.getType().toString(), param.getName().getFullyQualifiedName());
			if(!parameter.isPrimitive()) {
				getParameters().add(parameter);
			}
		}
	}

	public enum MethodPhraseType {
		BASIC, COMPLETE
	}

	@Override
	public void generate() {
		//String description = "";
		if(type.equals(MethodPhraseType.BASIC.toString())) {
			generateSimpleDescription();
		} else {
			//TODO generate description to body method
		}
	}
	
	@SuppressWarnings("unchecked")
	private void generateSimpleDescription() {
		String methodName = Tokenizer.split(getMethod().getName().getFullyQualifiedName());
		String className = Tokenizer.split(((TypeDeclaration) getMethod().getParent()).getName().toString());
		String methodArguments = getMethod().parameters().size() == 0 ? "" : Tokenizer.split(MethodPhraseUtils.getMethodParams(getMethod().parameters()));
		
		String pset = "";
		String verb = "";
		String doValue = "";
		
		if(getMethod().isConstructor()) {
			phrase = new NounPhrase(POSTagger.tag(methodName), parameters, "with");
			phrase.generate();
			pset = "instantiate " + phrase.toString();
		} else if(PhraseUtils.hasTrailingPastParticiple(getTaggedText(methodName).get(methodName.split("\\s").length - 1))) {
			pset = methodName; //NP
		} else if(PhraseUtils.hasLeadingPreposition(getTaggedText(methodName).get(0))) {
			pset = className + " " + methodName; //NP
		} else if(PhraseUtils.hasLeadingVerb(getTaggedText(methodName).get(0))) {
			List<TaggedTerm> taggedMethod = getTaggedText(methodName);
			verb = taggedMethod.get(0).getTerm();
			if(PhraseUtils.hasObjectInName(getTaggedText(methodName))) {
				doValue = PhraseUtils.getObject(taggedMethod);
			} else if(getMethod().parameters().size() > 0) {
				doValue = MethodPhraseUtils.getFirstFormalName(getMethod());
				String type = MethodPhraseUtils.getFirstFormalType(getMethod());
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
						doTemp = MethodPhraseUtils.getWordsBeforePrep(doValue, taggedMethod, i);
						ioTemp = MethodPhraseUtils.getWordsAfterPrep(doValue, taggedMethod, i);
						if(getMethod().parameters().size() > 0) {
							pset = pset + " " + inferArguments(verb, doTemp, taggedMethod, ioTemp, methodArguments, getMethod(), "") + " " + term.getTerm() + " " + ioTemp;
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
				if(getParameters().size() == 0) {
					if(PhraseUtils.hasObjectInName(getTaggedText(methodName))) {
						pset = methodName ;//+ " " + " of " + className;
					} else {
						pset = methodName + " of " + doValue;
					}
					
				} else {
					pset = inferArguments(verb, doValue, null, "", methodArguments, getMethod(), adjetive);
				}
			}
		} else {
			pset = pset + " " + methodName; //NP
		}
		System.out.println("METHOD: " + getMethod().getReturnType2() + " " + getMethod().getName().getFullyQualifiedName() + "(" + MethodPhraseUtils.getMethodParamsString(getMethod().parameters()) + ") "  + " PHRASE: " + pset);
		phraseString = pset.trim()  + "\n";
	}
	
	private String inferArguments(String verb, String doValue, List<TaggedTerm> taggedMethod, String ioValue, String methodArguments, MethodDeclaration method, String adjetive) {
		String phrase = "";
		String originalPhrase = "";
		int i = 0;
		phrase += "" + verb;
		for(Parameter param : getParameters()) {
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
	
	private String describeParam(Parameter parameter) {
		String description = "";
        final ParameterPhrase varGen = new ParameterPhrase(parameter);
        varGen.generate();
        description = varGen.toString();
		return description; 
	}
	
	private LinkedList<TaggedTerm> getTaggedText(String phrase) {
		return POSTagger.tag(phrase);
	}
	
	public String getReturnType(String method) {
		return method.substring(0, method.indexOf(" "));
	}
	
	public MethodDeclaration getMethod() {
		return method;
	}

	public void setMethod(MethodDeclaration method) {
		this.method = method;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public StereotypedElement getElement() {
		return element;
	}

	public String getPhrase() {
		return phraseString;
	}

	public void setPhrase(String phrase) {
		this.phraseString = phrase;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

}
