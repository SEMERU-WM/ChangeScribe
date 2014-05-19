package co.edu.unal.colswe.changescribe.core.textgenerator.phrase;

import java.util.LinkedList;
import java.util.List;

import co.edu.unal.colswe.changescribe.core.textgenerator.phrase.util.PhraseUtils;
import co.edu.unal.colswe.changescribe.core.textgenerator.pos.Tag;
import co.edu.unal.colswe.changescribe.core.textgenerator.pos.TaggedTerm;
import co.edu.unal.colswe.changescribe.core.textgenerator.tokenizer.Tokenizer;

public class VerbPhrase extends Phrase {
	private String verb;
	private String directObject;
	private String indirectObject;
	private String declaringClass;
	private List<Parameter> parameters;
	private boolean addFirstParam;

	public VerbPhrase(final String verb, final NounPhrase directObj) {
		super();
		this.verb = verb;
		directObj.generate();
		this.directObject = directObj.toString();
	}

	public VerbPhrase(final String verb, final NounPhrase directObj,
			final NounPhrase indirectObj) {
		super();
		this.verb = verb;
		directObj.generate();
		this.directObject = directObj.toString();
		this.indirectObject = indirectObj.toString();
	}

	public VerbPhrase(final String verb, final TypePhrase type,
			final NounPhrase indirectObj) {
		super();
		this.verb = verb;
		type.generate();
		this.directObject = type.toString();
		indirectObj.generate();
		this.indirectObject = indirectObj.toString();
		this.addFirstParam = false;
	}

	public VerbPhrase(final LinkedList<TaggedTerm> taggedMethod,
			final String declaringClass, final List<Parameter> parameters,
			final boolean addFirstParam) {
		super(taggedMethod);
		this.declaringClass = Tokenizer.split(declaringClass);
		this.parameters = parameters;
		this.addFirstParam = addFirstParam;
	}

	public VerbPhrase(String string, NounPhrase directObj,
			LinkedList<Parameter> parameters) {
		super();
		this.verb = string;
		directObj.generate();
		this.directObject = directObj.toString();
		this.parameters = parameters;
	}

	public void generate() {
		if (this.taggedPhrase == null || this.taggedPhrase.isEmpty()) {
			return;
		}
		this.verb = this.taggedPhrase.getFirst().getTerm();
		
		if(this.taggedPhrase.size() == 2 && PhraseUtils.isVerb(this.taggedPhrase.get(1))) {
			this.verb = this.taggedPhrase.get(1).getTerm();
		}
		if (this.taggedPhrase.size() > 0) {
			final StringBuilder dirObj = new StringBuilder();
			if (hasNounOrAdjective(this.taggedPhrase) || hasPrepositionOrAdverb(this.taggedPhrase)) {
				for (int i = 1; i < this.taggedPhrase.size(); ++i) {
					final TaggedTerm tt = this.taggedPhrase.get(i);
					if (Tag.isPrep(tt.getTag()) || Tag.isAdverb(tt.getTag())) {
						this.setIndirectObject(i);
						break;
					}
					dirObj.append(String.valueOf(tt.getTerm()) + " ");
				}
				this.directObject = dirObj.toString();
			} else if (this.parameters != null && !this.parameters.isEmpty() && !this.addFirstParam) {
				for (final Parameter par : this.parameters) {
					if (!par.isPrimitive()) {
						final ParameterPhrase varGen = new ParameterPhrase(par);
						varGen.generate();
						this.directObject = varGen.toString();
						break;
					}
				}
			}
		}
		if (this.addFirstParam && !this.parameters.isEmpty()) {
			final ParameterPhrase varGen2 = new ParameterPhrase(this.parameters.get(0));
			varGen2.generate();
			if (this.indirectObject != null) {
				this.indirectObject = this.indirectObject.concat(" ").concat(varGen2.toString()).trim();
			} else {
				this.indirectObject = varGen2.toString();
			}
		} else if ((this.directObject == null || this.directObject.isEmpty()) && this.indirectObject != null && !this.indirectObject.isEmpty() && this.parameters != null
				&& !this.parameters.isEmpty()) {
			final ParameterPhrase varGen2 = new ParameterPhrase(this.parameters.get(0));
			varGen2.generate();
			this.directObject = varGen2.toString();
		} else if (this.directObject == null || this.directObject.isEmpty()) {
			final Phrase typePhrase = new TypePhrase(this.declaringClass);
			typePhrase.generate();
			this.directObject = typePhrase.toString();
		}
	}

	private void setIndirectObject(final int fromIndex) {
		final StringBuilder io = new StringBuilder();
		for (int j = fromIndex; j < this.taggedPhrase.size(); ++j) {
			io.append(String.valueOf(this.taggedPhrase.get(j).getTerm()) + " ");
		}
		this.indirectObject = io.toString().trim();
	}

	private static boolean hasNounOrAdjective(
			final LinkedList<TaggedTerm> taggedPhrase) {
		for (final TaggedTerm taggedTerm : taggedPhrase) {
			if (Tag.isNoun(taggedTerm.getTag()) || Tag.isAdjective(taggedTerm.getTag())) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasPrepositionOrAdverb(
			final LinkedList<TaggedTerm> taggedPhrase) {
		for (final TaggedTerm taggedTerm : taggedPhrase) {
			if (Tag.isPrep(taggedTerm.getTag()) || Tag.isAdverb(taggedTerm.getTag())) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		final StringBuilder result = new StringBuilder();
		if (this.verb != null) {
			result.append(String.valueOf(this.verb) + " ");
		}
		if (this.directObject != null) {
			result.append(String.valueOf(this.directObject) + " ");
		}
		if (this.indirectObject != null) {
			result.append(this.indirectObject);
		}
		if(this.verb.equals("instantiate") && parameters != null && parameters.size() > 0) {
			for(Parameter param : parameters) {
				ParameterPhrase varGen = new ParameterPhrase(param);
				varGen.generate(); 
				if (parameters.indexOf(param) == 0) {
					result.append(" with "); 
				}
				result.append(String.valueOf(varGen.toString()));
				if(parameters.size() > 1 && parameters.indexOf(param) < parameters.size() - 1) {
					result.append(", ");
				} else if(parameters.size() > 1 && parameters.indexOf(param) == parameters.size() - 1) {
					result.append(" and " + String.valueOf(varGen.toString()));
				}
			}
		}
		return result.toString().trim();
	}

	public String toStringWithNoVerb() {
		final StringBuilder result = new StringBuilder();
		if (this.directObject != null) {
			result.append(String.valueOf(this.directObject) + " ");
		}
		if (this.indirectObject != null) {
			result.append(this.indirectObject);
		}
		return result.toString().trim();
	}

	public String getVerb() {
		return this.verb;
	}

	public boolean hasIndirectObject() {
		return this.indirectObject != null && !this.indirectObject.isEmpty();
	}

	public boolean hasDirectObject() {
		return this.directObject != null && !this.directObject.isEmpty();
	}
}
