package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypeIdentifier;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedElement;

public class SummarizeType {
	
	private StringBuilder builder;
	private StereotypedElement element;
	private ChangedFile[] differences;
	private StereotypeIdentifier identifier;
	
	public SummarizeType(StereotypedElement element, StereotypeIdentifier identifier, ChangedFile[] differences) {
		super();
		this.element = element;
		this.differences = differences;
		this.identifier = identifier;
	}
	
	public void generate() {
		builder = new StringBuilder();
		builder.append(GeneralDescriptor.describe(element, identifier.getParser().getCompilationUnit(), identifier.getScmOperation()));
		builder.append(StereotypeMethodDescriptor.describe(getElement().getStereoSubElements()));
		builder.append(ImpactSetDescriptor.describe(identifier.getCompilationUnit(), getDifferences(), identifier.getScmOperation()) + "\n");
	}
	
	public StringBuilder getBuilder() {
		return builder;
	}
	
	public void setBuilder(StringBuilder builder) {
		this.builder = builder;
	}
	
	public StereotypedElement getElement() {
		return element;
	}
	
	public void setElement(StereotypedElement element) {
		this.element = element;
	}

	public ChangedFile[] getDifferences() {
		return differences;
	}

	public void setDifferences(ChangedFile[] differences) {
		this.differences = differences;
	}

	public StereotypeIdentifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(StereotypeIdentifier identifier) {
		this.identifier = identifier;
	}
}
