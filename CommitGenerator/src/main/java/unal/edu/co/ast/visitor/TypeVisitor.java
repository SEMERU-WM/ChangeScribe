package unal.edu.co.ast.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeVisitor extends ASTVisitor {
	private boolean isRoot;

	public TypeVisitor() {
		super();
		this.isRoot = true;
	}

	public boolean visit(final MethodDeclaration node) {
		/*final StereotypedMethod stereotypedMethod = new StereotypedMethod(node);
		stereotypedMethod.findStereotypes();
		TypeAnalyzer.this.report.append(stereotypedMethod.getReport());
		TypeAnalyzer.this.stereotypedMethods.add(stereotypedMethod);*/
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