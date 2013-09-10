package unal.edu.co.stereotype;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import unal.edu.co.ast.visitor.TypeVisitor;

public class TypeAnalyzer {
	private List<?> stereotypedMethods;
	private StringBuilder report;

	public TypeAnalyzer(final TypeDeclaration type) {
		super();
		/*this.stereotypedMethods = new LinkedList<StereotypedMethod>();
		this.report = new StringBuilder();*/
		final TypeVisitor visitor = new TypeVisitor();
		type.accept((ASTVisitor) visitor);
	}

	public List<?> getStereotypedMethods() {
		return this.stereotypedMethods;
	}

	public String getReport() {
		return this.report.toString();
	}

}