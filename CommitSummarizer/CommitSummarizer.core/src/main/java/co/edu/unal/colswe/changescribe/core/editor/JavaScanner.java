package co.edu.unal.colswe.changescribe.core.editor;

import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.graphics.Color;

/**
 * A simple fuzzy scanner for Java
 */
@SuppressWarnings("rawtypes")
public class JavaScanner {

	protected Hashtable fgKeys = null;

	protected StringBuffer fBuffer = new StringBuffer();

	protected String fDoc;

	protected int fPos;

	protected int fEnd;

	protected int fStartToken;

	protected boolean fEofSeen = false;

	int[] tokenColors;

	Color[] colors;

	Vector blockComments = new Vector();

	public static final int EOF = -1;

	public static final int EOL = 10;

	public static final int WORD = 0;

	public static final int WHITE = 1;

	public static final int KEY = 2;

	public static final int COMMENT = 3;

	public static final int STRING = 5;

	public static final int OTHER = 6;

	public static final int NUMBER = 7;

	public static final int MAXIMUM_TOKEN = 8;

	private String[] fgKeywords = { "abstract", "class", "final", "implements",
			"interface", "native", "package", "private", "protected", "public",
			"static", "added", "removed", "commit", "degenerate", "incidental",
			"lazy", "mutator", "controller", "control", "methods", "empty",
			"Referenced", "small", "BUG", "FEATURE", "modifier", "state",
			"update", "access", "structure", "behavior", "large", "module",
			"modules" };

	public JavaScanner() {
		initialize();
	}

	/**
	 * Returns the ending location of the current token in the document.
	 */
	public final int getLength() {
		return fPos - fStartToken;
	}

	/**
	 * Initialize the lookup table.
	 */
	@SuppressWarnings("unchecked")
	void initialize() {
		fgKeys = new Hashtable();
		Integer k = new Integer(KEY);
		for (int i = 0; i < fgKeywords.length; i++)
			fgKeys.put(fgKeywords[i], k);
	}

	/**
	 * Returns the starting location of the current token in the document.
	 */
	public final int getStartOffset() {
		return fStartToken;
	}

	/**
	 * Returns the next lexical token in the document.
	 */
	@SuppressWarnings("unused")
	public int nextToken() {
		int c;
		fStartToken = fPos;
		while (true) {
			switch (c = read()) {
			case EOF:
				return EOF;
			case '/': // comment
				c = read();
				if (c == '/') {
					while (true) {
						c = read();
						if ((c == EOF) || (c == EOL)) {
							unread(c);
							return COMMENT;
						}
					}
				} else {
					unread(c);
				}
				return OTHER;
			case '\'': // char const
				character: for (;;) {
					c = read();
					switch (c) {
					case '\'':
						return STRING;
					case EOF:
						unread(c);
						return STRING;
					case '\\':
						c = read();
						break;
					}
				}

			case '"': // string
				string: for (;;) {
					c = read();
					switch (c) {
					case '"':
						return STRING;
					case EOF:
						unread(c);
						return STRING;
					case '\\':
						c = read();
						break;
					}
				}

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				do {
					c = read();
				} while (Character.isDigit((char) c));
				unread(c);
				return NUMBER;
			default:
				if (Character.isWhitespace((char) c)) {
					do {
						c = read();
					} while (Character.isWhitespace((char) c));
					unread(c);
					return WHITE;
				}
				if (c == '*') {
					return KEY;
				}
				if (Character.isJavaIdentifierStart((char) c)) {
					fBuffer.setLength(0);
					do {
						fBuffer.append((char) c);
						c = read();
					} while (Character.isJavaIdentifierPart((char) c));
					unread(c);
					Integer i = (Integer) fgKeys.get(fBuffer.toString());
					if (i != null)
						return i.intValue();
					return WORD;
				}
				return OTHER;
			}
		}
	}

	/**
	 * Returns next character.
	 */
	protected int read() {
		if (fPos <= fEnd) {
			return fDoc.charAt(fPos++);
		}
		return EOF;
	}

	public void setRange(String text) {
		fDoc = text;
		fPos = 0;
		fEnd = fDoc.length() - 1;
	}

	protected void unread(int c) {
		if (c != EOF)
			fPos--;
	}
}
