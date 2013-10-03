package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase;

import java.util.LinkedList;
import java.util.List;

import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.util.StringUtils;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.POSTagger;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.TaggedTerm;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.tokenizer.Tokenizer;

public class NounPhrase extends Phrase {
	private StringBuilder phrase;
	private List<Parameter> parameters;
	private String connector;
	private StringBuilder complementPhrase;

	public NounPhrase(String name) {
		super(POSTagger.tag(Tokenizer.split(name)));
	}

	public NounPhrase(LinkedList<TaggedTerm> taggedPhrase, List<Parameter> parameters, String connector) {
		super(taggedPhrase);
		this.setParameters(parameters);
		this.connector = connector;
	}

	public void generate() {
		if (this.taggedPhrase == null || this.taggedPhrase.isEmpty()) {
			return;
		}
		this.phrase = new StringBuilder();
		for (final TaggedTerm tt : this.taggedPhrase) {
			this.phrase.append(String.valueOf(tt.getTerm()) + " ");
		}
		
		if(parameters != null && !parameters.isEmpty()) {
			this.complementPhrase = new StringBuilder();
			String argsDescriptor = "";
			for(Parameter param : getParameters()) {
				ParameterPhrase pp = new ParameterPhrase(param);
				pp.generate();
				String paramText = pp.toString();
				if(!argsDescriptor.toLowerCase().contains(paramText.toLowerCase())) {
					if(!argsDescriptor.equals("")) {
						argsDescriptor += ", ";
					} else {
						argsDescriptor = "";
					}
					argsDescriptor += paramText;
				}
			}
			this.complementPhrase.append(StringUtils.clearLastCharacterInText(argsDescriptor, ",") + " ");
		}
		
	}

	public String toString() {
		StringBuilder phrase = new StringBuilder();
		if(this.phrase != null && !this.phrase.toString().equals("")) {
			phrase.append(this.phrase.toString() + " ");
		}
		if(this.complementPhrase != null && !this.complementPhrase.toString().equals("")) {
			phrase.append(connector + " " + this.complementPhrase.toString() + " ");
		}
		
		return phrase.toString().trim();
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}
}
