package co.edu.unal.colswe.changescribe.core.textgenerator.phrase;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.stereotype.stereotyped.StereotypedElement;
import co.edu.unal.colswe.changescribe.core.textgenerator.phrase.util.MethodPhraseUtils;
import co.edu.unal.colswe.changescribe.core.textgenerator.phrase.util.PhraseUtils;
import co.edu.unal.colswe.changescribe.core.textgenerator.pos.POSTagger;
import co.edu.unal.colswe.changescribe.core.textgenerator.pos.Tag;
import co.edu.unal.colswe.changescribe.core.textgenerator.pos.TaggedTerm;
import co.edu.unal.colswe.changescribe.core.textgenerator.tokenizer.Tokenizer;

public class MethodPhraseGenerator implements PhraseGenerator {
	
	private MethodDeclaration method;
	private final StereotypedElement element;
	private String type;
	private String phraseString;
	private LinkedList<Parameter> parameters = new LinkedList<Parameter>();
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
		if(type.equals(MethodPhraseType.BASIC.toString())) {
			generateSimpleDescription();
		} else {
			//TODO generate description to body method
		}
	}
	
	@SuppressWarnings("unchecked")
	private void generateSimpleDescription() {
		String methodName = Tokenizer.split(getMethod().getName().getFullyQualifiedName());
		String className = Constants.EMPTY_STRING;
		String returnType = Constants.EMPTY_STRING;
		if(getMethod().getReturnType2() != null) {
			returnType = getMethod().getReturnType2().toString();
		}
		if(getMethod().getParent() instanceof TypeDeclaration) {
			className = Tokenizer.split(((TypeDeclaration) getMethod().getParent()).getName().toString());
		} 
		
	 final LinkedList<TaggedTerm> taggedMethod = POSTagger.tag(Tokenizer.split(getMethod().getName().getFullyQualifiedName()));
        if (getMethod().isConstructor()) {
        	if(getMethod().parameters() != null && getMethod().parameters().size() > 0) {
        		this.phrase = new VerbPhrase("instantiate", new NounPhrase(taggedMethod),  this.parameters);
        	} else {
        		this.phrase = new VerbPhrase("instantiate", new NounPhrase(taggedMethod));
        	}
        }
        else if (PhraseUtils.hasLeadingVerb(getTaggedText(methodName).get(0))) {
            if (PhraseUtils.isThirdPersonVerb(taggedMethod.getFirst())) {
                this.phrase = new VerbPhrase("check if", new TypePhrase(className), new NounPhrase(taggedMethod));
            }
            else if (Tag.isPastOrPastPartVerb(taggedMethod.getFirst().getTag())) {
                this.phrase = new VerbPhrase("get", new NounPhrase(taggedMethod));
            }
            else {
                this.phrase = new VerbPhrase(taggedMethod, className, this.parameters, PhraseUtils.hasTrailingPrepositionOrAdverb(taggedMethod));
            }
        }
        else if (PhraseUtils.hasTrailingPastParticiple(getTaggedText(methodName).get(methodName.split("\\s").length - 1)) || PhraseUtils.hasTrailingAdjective(taggedMethod)) {
            this.phrase = new VerbPhrase("handle", new NounPhrase(taggedMethod));
        }
        else if (PhraseUtils.hasLeadingPreposition(taggedMethod)) {
            final LinkedList<TaggedTerm> tt = POSTagger.tag(className);
            tt.addAll(taggedMethod);
            if (Tag.isTo(taggedMethod.getFirst().getTag())) {
                this.phrase = new VerbPhrase("convert", new NounPhrase(tt));
            }
            else {
                this.phrase = new VerbPhrase("process", new TypePhrase(className), new NounPhrase(tt));
            }
        }
        else if (PhraseUtils.indexOfMiddleTo(taggedMethod) != -1) {
            final int toIndex = PhraseUtils.indexOfMiddleTo(taggedMethod);
            final LinkedList<TaggedTerm> dirObj = new LinkedList<TaggedTerm>();
            final LinkedList<TaggedTerm> indirObj = new LinkedList<TaggedTerm>();
            for (int i = 0; i < taggedMethod.size(); ++i) {
                if (i < toIndex) {
                    dirObj.add(taggedMethod.get(i));
                }
                else if (i == toIndex) {
                    taggedMethod.get(i).setTerm("to");
                    indirObj.add(taggedMethod.get(i));
                }
                else {
                    indirObj.add(taggedMethod.get(i));
                }
            }
            this.phrase = new VerbPhrase("convert", new NounPhrase(dirObj), new NounPhrase(indirObj));
        }
        else if (PhraseUtils.hasLeadingNoun(taggedMethod)) {
            if (returnType.equals("void") || returnType.equals("boolean")) {
                this.phrase = new VerbPhrase("handle", new NounPhrase(taggedMethod));
            }
            else {
                this.phrase = new VerbPhrase("get", new NounPhrase(taggedMethod));
            }
        }
        else {
            this.phrase = new VerbPhrase(taggedMethod, className, this.parameters, false);
        }
	    phrase.generate(); 
		System.out.println("METHOD: " + returnType + " " + methodName + "(" + MethodPhraseUtils.getMethodParamsString(getMethod().parameters()) + ") "  + " PHRASE: " + phrase.toString());
		phraseString = "\t" + StringUtils.capitalize(phrase.toString())  + ";\n";
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

	public LinkedList<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(LinkedList<Parameter> parameters) {
		this.parameters = parameters;
	}

}
