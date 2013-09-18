package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase;

import java.util.List;

import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.Tag;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.TaggedTerm;

public class PhraseUtils {
	
	public static boolean hasTrailingPastParticiple(TaggedTerm term) {
		return Tag.isPastOrPastPartVerb(term.getTag());
	}
	
	public static boolean hasLeadingPreposition(TaggedTerm term) {
		return Tag.isPrep(term.getTag());
	}
	
	public static boolean hasLeadingVerb(TaggedTerm term) {
		return Tag.isVerb(term.getTag());
	}
	
	public static boolean hasObjectInName(TaggedTerm term) {
		return Tag.isNoun(term.getTag());
	}
	
	public static boolean isThirdPersonVerb(TaggedTerm term) {
		return Tag.isThirdPersonVerb(term.getTag());
	}
	
	public static boolean isPastOrPastPartVerb(TaggedTerm term) {
		return Tag.isPastOrPastPartVerb(term.getTag());
	}
	
	public static boolean hasNounOrAdjective(List<TaggedTerm> taggedPhrase) {
        for (final TaggedTerm taggedTerm : taggedPhrase) {
            if (Tag.isNoun(taggedTerm.getTag()) || Tag.isAdjective(taggedTerm.getTag())) {
                return true;
            }
        }
        return false;
    }
	
	public static boolean hasPrepositionOrAdverb(List<TaggedTerm> taggedPhrase) {
        for (final TaggedTerm taggedTerm : taggedPhrase) {
            if (Tag.isPrep(taggedTerm.getTag()) || Tag.isAdverb(taggedTerm.getTag())) {
                return true;
            }
        }
        return false;
    }
	
	public static boolean hasTrailingPrepositionOrAdverb(List<TaggedTerm> taggedTerms) {
        return Tag.isPrep(taggedTerms.get(taggedTerms.size() - 1).getTag()) || Tag.isAdverb(taggedTerms.get(taggedTerms.size() - 1).getTag());
    }
	
	public static boolean containsPrepositions(List<TaggedTerm> terms) {
		boolean contains = false;
		for(TaggedTerm term : terms) {
			
			if(Tag.isPrep(term.getTag())) {
				contains = true;
				break;
			}
		}
		return contains;
	}

}
