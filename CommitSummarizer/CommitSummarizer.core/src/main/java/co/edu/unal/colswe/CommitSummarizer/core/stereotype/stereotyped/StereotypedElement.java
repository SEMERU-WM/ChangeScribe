package co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped;

import java.util.List;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.Javadoc;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.MethodStereotype;

public interface StereotypedElement {
	BodyDeclaration getElement();

	List<MethodStereotype> getStereotypes();

	List<StereotypedElement> getStereoSubElements();

	void findStereotypes();

	String getReport();

	Javadoc getJavadoc();

	ChildPropertyDescriptor getJavadocDescriptor();

	String getName();

	String getQualifiedName();

	String getKey();
}
