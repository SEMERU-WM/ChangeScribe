package co.edu.unal.colswe.changescribe.core.textgenerator.pos;

@SuppressWarnings("unused")
public class Tag {

	private static final String VERB_BASE_FORM = "VB";
	private static final String VERB_PRESENT_3_PERSON = "VBZ";
	private static final String VERB_PAST = "VBD";
	private static final String VERB_PAST_PART = "VBN";
	private static final String PREPOSITION_OR_CONJUNCTION = "IN";
	private static final String TO = "TO";
	private static final String NOUN_COMMON_SING = "NN";
	private static final String ADJECTIVE = "JJ";
	private static final String ADVERB = "RB";
	private static final String PLURAL_PROPER_NOUN = "NNPS";
	private static final String PLURAL_COMMON_NOUN = "NNS";

	public static boolean isVerb(final String tag) {
		return tag.startsWith("VB");
	}

	public static boolean isThirdPersonVerb(final String tag) {
		return tag.equals("VBZ");
	}

	public static boolean isPastOrPastPartVerb(final String tag) {
		return tag.equals("VBD") || tag.equals("VBN");
	}

	public static boolean isPrep(final String tag) {
		return tag.equals("IN") || tag.equals("TO");
	}

	public static boolean isTo(final String tag) {
		return tag.equals("TO");
	}

	public static boolean isNoun(final String tag) {
		return tag.startsWith("NN");
	}

	public static boolean isAdjective(final String tag) {
		return tag.startsWith("JJ");
	}

	public static boolean isAdverb(final String tag) {
		return tag.startsWith("RB");
	}

	public static boolean isPluralNoun(final String tag) {
		return tag.equals("NNS") || tag.equals("NNPS");
	}
}
