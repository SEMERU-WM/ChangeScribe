package unal.edu.co.ast.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import unal.edu.co.stereotype.StereotypedMethod;
import unal.edu.co.stereotype.TypeAnalyzer;

public class TypeVisitor extends ASTVisitor {
	private boolean isRoot;
	private TypeAnalyzer typeAnalyzer = null; 

	public TypeVisitor(TypeAnalyzer typeAnalyzer) {
		super();
		this.isRoot = true;
		this.typeAnalyzer = typeAnalyzer;
	}

	public boolean visit(final MethodDeclaration node) {
		final StereotypedMethod stereotypedMethod = new StereotypedMethod(node);
		stereotypedMethod.findStereotypes();
		typeAnalyzer.getReport().append(stereotypedMethod.getReport());
		typeAnalyzer.getStereotypedMethods().add(stereotypedMethod);
		return super.visit(node);
	}

	public boolean visit(final TypeDeclaration node) {
		if (this.isRoot) {
			this.isRoot = false;
			return super.visit(node);
		}
		return false;
	}
}