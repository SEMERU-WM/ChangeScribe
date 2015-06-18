package co.edu.unal.colswe.changescribe.core.textgenerator.tokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer
{
    public static final String SEPARATOR = " ";
    private static final Pattern MIDDLE_DIGITS;
    private static final Pattern CAMEL_CASE;
    
    static {
        MIDDLE_DIGITS = Pattern.compile("(\\D*)(\\d+)(\\D*)");
        CAMEL_CASE = Pattern.compile("(\\s+[a-z]||[A-Z])[a-z]+");
    }
    
    public static String split(final String identifier) {
        String result = replaceSpecialSymbols(identifier);
        result = splitMiddleDigits(result);
        result = splitCamelCase(result);
        return result;
    }
    
    private static String replaceSpecialSymbols(final String text) {
        return text.replaceAll("[^a-zA-Z0-9]+", " ").trim();
    }
    
    private static String replaceMultipleBlanks(final String text) {
        return text.replaceAll("\\s++", " ").trim();
    }
    
    private static String splitMiddleDigits(final String text) {
        final StringBuffer stringBuffer = new StringBuffer(text.length());
        final Matcher matcher = Tokenizer.MIDDLE_DIGITS.matcher(text);
        while (matcher.find()) {
            final String replacement = String.valueOf(matcher.group(1)) + " " + matcher.group(2) + " " + matcher.group(3);
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(stringBuffer);
        return replaceMultipleBlanks(stringBuffer.toString());
    }
    
    private static String splitCamelCase(final String text) {
        final StringBuffer stringBuffer = new StringBuffer(text.length());
        final Matcher matcher = Tokenizer.CAMEL_CASE.matcher(text);
        while (matcher.find()) {
            final String replacement = " " + matcher.group().toLowerCase() + " ";
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(stringBuffer);
        return replaceMultipleBlanks(stringBuffer.toString());
    }
}
