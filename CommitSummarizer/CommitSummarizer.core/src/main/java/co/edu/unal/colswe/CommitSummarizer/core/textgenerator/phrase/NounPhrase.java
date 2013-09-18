package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase;

import java.util.LinkedList;

import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.POSTagger;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.TaggedTerm;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.tokenizer.Tokenizer;

public class NounPhrase extends Phrase {
	private StringBuilder phraseBuilder;

	protected NounPhrase(final String name) {
		super(POSTagger.tag(Tokenizer.split(name)));
	}

	public NounPhrase(final LinkedList<TaggedTerm> taggedPhrase) {
		super(taggedPhrase);
	}

	public void generate() {
		if (this.taggedPhrase == null || this.taggedPhrase.isEmpty()) {
			return;
		}
		this.phraseBuilder = new StringBuilder();
		for (final TaggedTerm tt : this.taggedPhrase) {
			this.phraseBuilder.append(String.valueOf(tt.getTerm()) + " ");
		}
	}

	public String toString() {
		return this.phraseBuilder.toString().trim();
	}
}
