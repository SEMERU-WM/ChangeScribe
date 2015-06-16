package co.edu.unal.colswe.changescribe.core.summarizer;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.stereotype.stereotyped.StereotypeIdentifier;
import co.edu.unal.colswe.changescribe.core.stereotype.stereotyped.StereotypedElement;

public class SummarizeType {
	
	private StringBuilder builder;
	private StereotypedElement element;
	private ChangedFile[] differences;
	private StereotypeIdentifier identifier;
	private boolean isLocal;
	
	public SummarizeType(StereotypedElement element, StereotypeIdentifier identifier, ChangedFile[] differences) {
		super();
		this.element = element;
		this.differences = differences;
		this.identifier = identifier;
		this.builder = new StringBuilder();
	}
	
	public void generate() {
		StringBuilder localBuilder = new StringBuilder("");
		builder = new StringBuilder();
		
		builder.append(GeneralDescriptor.describe(element, identifier.getParser().getCompilationUnit(), identifier.getScmOperation(), isLocal()));
		
		
		
		localBuilder.append(StereotypeMethodDescriptor.describe(getElement().getStereoSubElements()));
		localBuilder.append(ImpactSetDescriptor.describe(identifier.getCompilationUnit(), getDifferences(), identifier.getScmOperation()) + Constants.NEW_LINE);
		
		if(!localBuilder.toString().trim().equals("")) {
			if(getElement().getStereoSubElements() != null && getElement().getStereoSubElements().size() > 0) {
				builder.append(". It allows to: \n\n");
			} else {
				builder.append("\n\n");
			}
			builder.append(localBuilder.toString());
		} else {
			builder.append("\n\n");
		}
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

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}
}
