package co.edu.unal.colswe.changescribe.core.textgenerator.pos;

public class TaggedTerm {
	private String term;
	private String tag;

	public TaggedTerm(final String term, final String tag) {
		super();
		this.term = term;
		this.tag = tag;
	}

	public String getTag() {
		return this.tag;
	}

	public void setTag(final String tag) {
		this.tag = tag;
	}

	public String getTerm() {
		return this.term;
	}

	public void setTerm(final String term) {
		this.term = term;
	}

	public String toString() {
		return String.valueOf(this.term) + ":" + this.tag;
	}
}
