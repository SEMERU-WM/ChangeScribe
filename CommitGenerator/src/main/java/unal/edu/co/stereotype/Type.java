package unal.edu.co.stereotype;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import unal.edu.co.ast.visitor.TypeVisitor;

public class Type {
	private List<StereotypedMethod> stereotypedMethods;
	private StringBuilder report;

	public Type(final TypeDeclaration type) {
		super();
		this.stereotypedMethods = new LinkedList<>();
		this.report = new StringBuilder();
		final TypeVisitor visitor = new TypeVisitor(this);
		type.accept((ASTVisitor) visitor);
	}

	public List<StereotypedMethod> getStereotypedMethods() {
		return this.stereotypedMethods;
	}

	public StringBuilder getReport() {
		return this.report;
	}

}