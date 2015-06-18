package co.edu.unal.colswe.changescribe.core.textgenerator.phrase.util;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ITypeBinding;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.textgenerator.pos.Tag;
import co.edu.unal.colswe.changescribe.core.textgenerator.pos.TaggedTerm;

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
	
	public static boolean isVerb(TaggedTerm term) {
		return Tag.isVerb(term.getTag());
	}
	
	public static boolean hasLeadingNoun(LinkedList<TaggedTerm> taggedTerms) {
        return Tag.isNoun(taggedTerms.getFirst().getTag());
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
	
	public static boolean hasLeadingPreposition(LinkedList<TaggedTerm> taggedTerms) {
        return Tag.isPrep(taggedTerms.getFirst().getTag());
    }
	
	public static boolean hasTrailingAdjective(LinkedList<TaggedTerm> taggedTerms) {
        return Tag.isAdjective(taggedTerms.getFirst().getTag());
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
		String adjetive = Constants.EMPTY_STRING;
		for(TaggedTerm term : terms) {
			
			if(Tag.isAdjective(term.getTag())) {
				adjetive = term.getTerm();
				break;
			}
		}
		return adjetive;
	}
	
	public static String getObject(List<TaggedTerm> terms) {
		String object = Constants.EMPTY_STRING;
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
		String rta = Constants.EMPTY_STRING;
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
	
	public static String enumeratedTypes(final ITypeBinding[] types) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < types.length; ++i) {
            result.append(types[i].getName());
            if (i != types.length - 1) {
                result.append(", ");
            }
        }
        return result.toString();
    }
	
	public static String enumeratedFields(List<String> fields) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.size(); ++i) {
            result.append(fields.get(i));
            if (i != fields.size() - 1) {
                result.append(", ");
            }
        }
        final int andReplace = result.lastIndexOf(",");
        if (andReplace != -1) {
            result.replace(andReplace, andReplace + 1, " and");
        }
        return result.toString();
    }
	
	public static String getImplementationDescription(final ITypeBinding[] types) {
        final StringBuilder result = new StringBuilder();
        result.append(enumeratedTypes(types));
        result.append(" ");
        result.append("implementation");
        return result.toString();
    }
    
    public static String getExtensionDescription(final ITypeBinding type) {
        final StringBuilder result = new StringBuilder();
        result.append(type.getName());
        result.append(" ");
        result.append("extension");
        return result.toString();
    }
    
    public static int indexOfMiddleTo(LinkedList<TaggedTerm> taggedTerms) {
        for (int i = 0; i < taggedTerms.size(); ++i) {
            final TaggedTerm t = taggedTerms.get(i);
            if (Tag.isTo(t.getTag()) || t.getTerm().equals("2")) {
                return i;
            }
        }
        return -1;
    }
    
    public static String getIsAre(int size) {
    	String value = Constants.EMPTY_STRING;
    	if(size == 1) {
    		value = " is ";
    	} else if(size > 1) {
    		value = " are ";
    	}
    	return value;
    }
}
