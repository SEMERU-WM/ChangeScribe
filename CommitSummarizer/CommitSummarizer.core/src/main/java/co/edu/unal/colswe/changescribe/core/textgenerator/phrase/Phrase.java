package co.edu.unal.colswe.changescribe.core.textgenerator.phrase;

import java.util.LinkedList;

import co.edu.unal.colswe.changescribe.core.textgenerator.pos.TaggedTerm;

public abstract class Phrase {
	protected LinkedList<TaggedTerm> taggedPhrase;

	protected Phrase() {
		super();
	}

	protected Phrase(final LinkedList<TaggedTerm> taggedPhrase) {
		super();
		this.taggedPhrase = taggedPhrase;
	}

	public abstract void generate();

	public abstract String toString();
}
