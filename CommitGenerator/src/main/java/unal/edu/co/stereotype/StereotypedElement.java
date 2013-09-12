package unal.edu.co.stereotype;

import java.util.*;
import org.eclipse.jdt.core.dom.*;

public interface StereotypedElement
{
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
