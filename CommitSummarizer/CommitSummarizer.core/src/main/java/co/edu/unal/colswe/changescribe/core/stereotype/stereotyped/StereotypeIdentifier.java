package co.edu.unal.colswe.changescribe.core.stereotype.stereotyped;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.search.SearchMatch;

import co.edu.unal.colswe.changescribe.core.ast.JParser;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;

public class StereotypeIdentifier {
	private JParser parser;
	private List<StereotypedElement> stereotypedElements;
	double methodsMean;
	double methodsStdDev;
	private ICompilationUnit compilationUnit;
	private String scmOperation; 
	private StringBuilder builder;
	private ChangedFile changedFile;
	private List<SearchMatch> relatedTypes;
	private double impactPercentaje;

	public StereotypeIdentifier() {
		super();
		this.stereotypedElements = new LinkedList<StereotypedElement>();
		builder = new StringBuilder();
	}
	
	public StereotypeIdentifier(final ICompilationUnit unit,
			final double methodsMean, final double methodsStdDev) {
		super();
		this.compilationUnit = unit;
		this.parser = new JParser(unit);
		this.methodsMean = methodsMean;
		this.methodsStdDev = methodsStdDev;
		this.stereotypedElements = new LinkedList<StereotypedElement>();
		this.builder = new StringBuilder();
	}

	public StereotypeIdentifier(final IMember member, final double methodsMean,
			final double methodsStdDev) {
		super();
		this.parser = new JParser(member);
		this.methodsMean = methodsMean;
		this.methodsStdDev = methodsStdDev;
		this.stereotypedElements = new LinkedList<StereotypedElement>();
	}
	
	public StereotypeIdentifier(File file) throws CoreException {
		super();
		this.parser = new JParser(file);
		this.stereotypedElements = new LinkedList<StereotypedElement>();
		this.builder = new StringBuilder();
	}

	public void setParameters(final ICompilationUnit unit,
			final double methodsMean, final double methodsStdDev) {
		this.parser = new JParser(unit);
		this.methodsMean = methodsMean;
		this.methodsStdDev = methodsStdDev;
	}

	public void setParameters(final IMember member, final double methodsMean,
			final double methodsStdDev) {
		this.parser = new JParser(member);
		this.methodsMean = methodsMean;
		this.methodsStdDev = methodsStdDev;
	}

	public void identifyStereotypes() { 
		if (this.parser == null) {
			return;
		}
		this.parser.parse();
		for (final ASTNode element : this.parser.getElements()) {
			try {
				StereotypedElement stereoElement;
				if (element instanceof TypeDeclaration) {
					stereoElement = new StereotypedType((TypeDeclaration) element, this.methodsMean, this.methodsStdDev);
				} else {
					if (!(element instanceof MethodDeclaration)) {
						continue;
					}
					stereoElement = new StereotypedMethod((MethodDeclaration) element);
				}
				stereoElement.findStereotypes();
				this.stereotypedElements.add(stereoElement);
			} catch (NullPointerException ex) {
				
				//TODO DELETE
				ex.printStackTrace();
			}
		}
	}

	public List<StereotypedElement> getStereotypedElements() {
		return this.stereotypedElements;
	}

	public JParser getParser() {
		return this.parser;
	}

	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(ICompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public String getScmOperation() {
		return scmOperation;
	}

	public void setScmOperation(String scmOperation) {
		this.scmOperation = scmOperation;
	}

	public StringBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(StringBuilder builder) {
		this.builder = builder;
	}

	@Override
	public String toString() {
		if(null == builder) {
			builder = new StringBuilder();
		}
		return builder.toString();
	}

	public ChangedFile getChangedFile() {
		return changedFile;
	}

	public void setChangedFile(ChangedFile changedFile) {
		this.changedFile = changedFile;
	}

	public List<SearchMatch> getRelatedTypes() {
		return relatedTypes;
	}

	public void setRelatedTypes(List<SearchMatch> relatedTypes) {
		this.relatedTypes = relatedTypes;
	}

	public double getImpactPercentaje() {
		return impactPercentaje;
	}

	public void setImpactPercentaje(double impactPercentaje) {
		this.impactPercentaje = impactPercentaje;
	}
}
