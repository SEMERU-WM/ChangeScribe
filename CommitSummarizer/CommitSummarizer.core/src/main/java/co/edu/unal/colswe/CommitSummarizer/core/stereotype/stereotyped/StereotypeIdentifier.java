package co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import co.edu.unal.colswe.CommitSummarizer.core.ast.JParser;

public class StereotypeIdentifier {
	private JParser parser;
	private List<StereotypedElement> stereotypedElements;
	double methodsMean;
	double methodsStdDev;
	private ICompilationUnit compilationUnit;

	public StereotypeIdentifier() {
		super();
		this.stereotypedElements = new LinkedList<StereotypedElement>();
	}
	
	public StereotypeIdentifier(final ICompilationUnit unit,
			final double methodsMean, final double methodsStdDev) {
		super();
		this.compilationUnit = unit;
		this.parser = new JParser(unit);
		this.methodsMean = methodsMean;
		this.methodsStdDev = methodsStdDev;
		this.stereotypedElements = new LinkedList<StereotypedElement>();
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
					stereoElement = new StereotypedType(
							(TypeDeclaration) element, this.methodsMean,
							this.methodsStdDev);
				} else {
					if (!(element instanceof MethodDeclaration)) {
						continue;
					}
					stereoElement = new StereotypedMethod(
							(MethodDeclaration) element);
				}
				stereoElement.findStereotypes();
				this.stereotypedElements.add(stereoElement);
			} catch (NullPointerException ex) {
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
}
