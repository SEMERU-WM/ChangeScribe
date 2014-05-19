package co.edu.unal.colswe.changescribe.core.stereotype.analyzer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import co.edu.unal.colswe.changescribe.core.stereotype.stereotyped.StereotypedMethod;
import co.edu.unal.colswe.changescribe.core.visitor.TypeVisitor;

public class TypeAnalyzer {
	private List<StereotypedMethod> stereotypedMethods;
	private StringBuilder report;

	public TypeAnalyzer(final TypeDeclaration type) {
		super();
		this.stereotypedMethods = new LinkedList<StereotypedMethod>();
		this.report = new StringBuilder();
		final TypeVisitor visitor = new TypeVisitor(this);
		if(type != null && visitor != null) {
			type.accept((ASTVisitor) visitor);
		}
	}

	public List<StereotypedMethod> getStereotypedMethods() {
		return this.stereotypedMethods;
	}

	public StringBuilder getReport() {
		return this.report;
	}

}