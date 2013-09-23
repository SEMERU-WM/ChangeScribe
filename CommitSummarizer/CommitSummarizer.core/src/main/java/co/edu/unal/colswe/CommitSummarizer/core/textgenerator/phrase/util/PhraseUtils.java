package co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.util;

import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

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
	
	public static boolean hasObjectInName(List<TaggedTerm> taggedPhrase) {
		boolean contains = false;
		for(TaggedTerm term : taggedPhrase) {
			
			if(Tag.isNoun(term.getTag())) {
				contains = true;
				break;
			}
		}
		return contains;
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
	
	public static boolean containsAdjetives(List<TaggedTerm> terms) {
		boolean contains = false;
		for(TaggedTerm term : terms) {
			
			if(Tag.isAdjective(term.getTag())) {
				contains = true;
				break;
			}
		}
		return contains;
	}
	
	public static String getAdjetive(List<TaggedTerm> terms) {
		String adjetive = "";
		for(TaggedTerm term : terms) {
			
			if(Tag.isAdjective(term.getTag())) {
				adjetive = term.getTerm();
				break;
			}
		}
		return adjetive;
	}
	
	public static String getObject(List<TaggedTerm> terms) {
		String object = "";
		for(TaggedTerm term : terms) {
			
			if(Tag.isNoun(term.getTag())) {
				object = term.getTerm();
				break;
			}
		}
		return object;
	}
	
	public static String getIndefiniteArticle(final String text) {
        String article;
        if (text.matches("<.*>[aeiouhAEIOUH].*") || text.matches("^[aeiouhAEIOUH].*")) {
            article = "an";
        } else {
            article = "a";
        }
        return article;
    }
	
	public static String getStringType(IType type) {
		String rta = "";
		try {
			if(type.isClass()) {
				rta = "class";
			} else if(type.isInterface()) {
				rta = "interface";
			} else if(type.isEnum()) {
				rta = "enumeration";
			} else if(type.isLocal()) {
				rta = "local type";
			}
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return rta;
	}
	

}
