package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos;

import java.io.IOException;
import java.util.LinkedList;

import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.tokenizer.Tokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTagger {
	private static MaxentTagger tagger;

	private static void init() {
		if (POSTagger.tagger == null) {
			try {
				POSTagger.tagger = new MaxentTagger(
						"res/taggers/wsj-0-18-bidirectional-distsim.tagger");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static LinkedList<TaggedTerm> tag(final String phrase) {
		init();
		final LinkedList<TaggedTerm> taggedTerms = new LinkedList<TaggedTerm>();
		final StringBuilder completePhrase = new StringBuilder("you do ");
		if (phrase != null && !phrase.isEmpty()) {
			completePhrase.append(String.valueOf(phrase.charAt(0))
					.toLowerCase());
			if (phrase.length() > 2) {
				completePhrase.append(phrase.substring(1));
			}
		}
		final String[] taggedElements = POSTagger.tagger.tagString(
				completePhrase.toString()).split(" ");
		for (int i = 2; i < taggedElements.length; ++i) {
			final String[] element = taggedElements[i].split("_");
			final TaggedTerm tt = new TaggedTerm(element[0], element[1]);
			taggedTerms.add(tt);
		}
		return taggedTerms;
	}

	public static void main(final String[] args) throws ClassNotFoundException, IOException {
		MaxentTagger tagger2 =  new MaxentTagger(
				"res/taggers/wsj-0-18-bidirectional-distsim.tagger");
		final String[] identifiers = { "setUIFont", "UncaughtExceptions",
				"ShowStatusBar", "CompareTo", "MousePressed",
				"ActionPerformed", "AfterSave", "ToString", "Length", "Weigth",
				"SaveImage", "SavedImage", "ImageSaved", "BrokenImage",
				"ImageBroken", "ReturnPressed", "IsVisible", "HasProblems",
				"DragDropEnd", "computesWeigth", "showAboutDialog",
				"setSelectedSong", "hasLeadingComment", "mouseMove", "keyDown",
				"drawingRequestUpdate" };
		final POSTagger t = new POSTagger();
		for (int i = 0; i < identifiers.length; ++i) {
			for (final TaggedTerm tt : tag(Tokenizer.split(identifiers[i]))) {
				System.out.print(tt + "  ");
			}
			System.out.println();
		}
	}
}
