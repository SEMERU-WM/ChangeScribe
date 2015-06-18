package co.edu.unal.colswe.changescribe.core.stereotype.taxonomy;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import co.edu.unal.colswe.changescribe.core.stereotype.stereotyped.StereotypedMethod;

public class TypeStereotypeRules {
    protected TypeDeclaration type;
    protected Set<StereotypedMethod> totalMethods;
    protected Set<StereotypedMethod> getMethods;
    protected Set<StereotypedMethod> predicateMethods;
    protected Set<StereotypedMethod> propertyMethods;
    protected Set<StereotypedMethod> voidAccessorMethods;
    protected Set<StereotypedMethod> setMethods;
    protected Set<StereotypedMethod> commandMethods;
    protected Set<StereotypedMethod> nonVoidCommandMethods;
    protected Set<StereotypedMethod> factoryMethods;
    protected Set<StereotypedMethod> localControllerMethods;
    protected Set<StereotypedMethod> collaboratorMethods;
    protected Set<StereotypedMethod> controllerMethods;
    protected Set<StereotypedMethod> incidentalMethods;
    protected Set<StereotypedMethod> emptyMethods;
    protected Set<StereotypedMethod> abstractMethods;
    protected Set<StereotypedMethod> accessorMethods;
    protected Set<StereotypedMethod> mutatorMethods;
    protected Set<StereotypedMethod> collaborationalMethods;
    protected Set<StereotypedMethod> degenerateMethods;
    protected double methodsMean;
    protected double methodsStdDev;
    
    protected TypeStereotypeRules() {
        super();
        this.totalMethods = new HashSet<StereotypedMethod>();
        this.getMethods = new HashSet<StereotypedMethod>();
        this.predicateMethods = new HashSet<StereotypedMethod>();
        this.propertyMethods = new HashSet<StereotypedMethod>();
        this.voidAccessorMethods = new HashSet<StereotypedMethod>();
        this.commandMethods = new HashSet<StereotypedMethod>();
        this.setMethods = new HashSet<StereotypedMethod>();
        this.nonVoidCommandMethods = new HashSet<StereotypedMethod>();
        this.factoryMethods = new HashSet<StereotypedMethod>();
        this.localControllerMethods = new HashSet<StereotypedMethod>();
        this.collaboratorMethods = new HashSet<StereotypedMethod>();
        this.controllerMethods = new HashSet<StereotypedMethod>();
        this.incidentalMethods = new HashSet<StereotypedMethod>();
        this.emptyMethods = new HashSet<StereotypedMethod>();
        this.abstractMethods = new HashSet<StereotypedMethod>();
        this.accessorMethods = new HashSet<StereotypedMethod>();
        this.mutatorMethods = new HashSet<StereotypedMethod>();
        this.collaborationalMethods = new HashSet<StereotypedMethod>();
        this.degenerateMethods = new HashSet<StereotypedMethod>();
    }
    
    public TypeStereotypeRules(final TypeDeclaration type, final double methodsMean, final double methodsStdDev) throws NullPointerException {
        this();
        if (type == null) {
            throw new NullPointerException("The type can't be null");
        }
        this.type = type;
        this.methodsMean = methodsMean;
        this.methodsStdDev = methodsStdDev;
    }
    
    protected boolean checkForInterface() {
        return this.type.isInterface();
    }
    
    protected boolean checkForEntity() {
        final boolean rule1 = !this.accessorMethods.isEmpty() && !this.mutatorMethods.isEmpty();
        final boolean rule2 = 0.4 * this.totalMethods.size() <= (double)this.collaborationalMethods.size() && (double)this.collaborationalMethods.size() <= 0.6 * this.totalMethods.size();
        final boolean rule3 = this.controllerMethods.isEmpty();
        return rule1 && rule2 && rule3;
    }
    
    protected boolean checkForMinimalEntity() {
        final HashSet<StereotypedMethod> temp = new HashSet<StereotypedMethod>(this.totalMethods);
        temp.removeAll(this.accessorMethods);
        temp.removeAll(this.mutatorMethods);
        temp.removeAll(this.localControllerMethods);
        final boolean rule1 = temp.isEmpty();
        final boolean rule2 = !this.accessorMethods.isEmpty() && !this.mutatorMethods.isEmpty();
        final boolean rule3 = this.controllerMethods.isEmpty() && this.factoryMethods.isEmpty();
        return rule1 && rule2 && rule3;
    }
    
    protected boolean checkForDataProvider() {
        final boolean rule1 = (double)this.accessorMethods.size() > 2.0 * this.mutatorMethods.size();
        final Set<StereotypedMethod> temp = this.controllerMethods;
        temp.addAll(this.factoryMethods);
        final boolean rule2 = (double)this.accessorMethods.size() > 2.0 * temp.size();
        return rule1 && rule2;
    }
    
    protected boolean checkForCommander() {
        final boolean rule1 = (double)this.mutatorMethods.size() > 2.0 * this.accessorMethods.size();
        final Set<StereotypedMethod> temp = this.controllerMethods;
        temp.addAll(this.factoryMethods);
        final boolean rule2 = (double)this.mutatorMethods.size() > 2.0 * temp.size();
        return rule1 && rule2;
    }
    
    protected boolean checkForBoundary() {
        final boolean rule1 = this.collaboratorMethods.size() >= this.totalMethods.size() - this.collaboratorMethods.size();
        final boolean rule2 = (double)this.factoryMethods.size() <= 0.5 * this.totalMethods.size();
        final boolean rule3 = (double)this.controllerMethods.size() <= 0.3333333333333333 * this.totalMethods.size();
        return rule1 && rule2 && rule3;
    }
    
    protected boolean checkForFactory() {
        final boolean rule1 = (double)this.factoryMethods.size() >= 0.6666666666666666 * this.totalMethods.size();
        return rule1;
    }
    
    protected boolean checkForController() {
        final HashSet<StereotypedMethod> temp = new HashSet<StereotypedMethod>(this.collaborationalMethods);
        temp.addAll(this.factoryMethods);
        final boolean rule1 = (double)temp.size() >= 0.6666666666666666 * this.totalMethods.size();
        final boolean rule2 = !this.controllerMethods.isEmpty() || !this.factoryMethods.isEmpty();
        final boolean rule3 = !this.mutatorMethods.isEmpty() || !this.accessorMethods.isEmpty();
        return rule1 && rule2 && rule3;
    }
    
    protected boolean checkForPureController() {
        final HashSet<StereotypedMethod> temp = new HashSet<StereotypedMethod>(this.totalMethods);
        temp.removeAll(this.controllerMethods);
        temp.removeAll(this.factoryMethods);
        temp.removeAll(this.localControllerMethods);
        final boolean rule1 = temp.isEmpty();
        final boolean rule2 = this.mutatorMethods.isEmpty() && this.accessorMethods.isEmpty();
        final boolean rule3 = !this.controllerMethods.isEmpty();
        return rule1 && rule2 && rule3;
    }
    
    protected boolean checkForLargeClass() {
        final boolean rule1 = 0.2 * this.totalMethods.size() <= (double)(this.accessorMethods.size() + this.mutatorMethods.size()) && (double)(this.accessorMethods.size() + this.mutatorMethods.size()) <= 0.6666666666666666 * this.totalMethods.size();
        final HashSet<StereotypedMethod> temp = new HashSet<StereotypedMethod>(this.controllerMethods);
        temp.addAll(this.factoryMethods);
        final boolean rule2 = 0.2 * this.totalMethods.size() <= (double)temp.size() && (double)temp.size() <= 0.6666666666666666 * this.totalMethods.size();
        final boolean rule3 = !this.controllerMethods.isEmpty();
        final boolean rule4 = !this.factoryMethods.isEmpty();
        final boolean rule5 = !this.accessorMethods.isEmpty();
        final boolean rule6 = !this.mutatorMethods.isEmpty();
        final boolean rule7 = (double)this.totalMethods.size() > this.methodsMean + this.methodsStdDev;
        return rule1 && rule2 && rule3 && rule4 && rule5 && rule6 && rule7;
    }
    
    protected boolean checkForLazyClass() {
        final boolean rule1 = (double)this.incidentalMethods.size() >= 0.3333333333333333 * this.totalMethods.size();
        final boolean rule2 = (double)(this.getMethods.size() + this.setMethods.size() + this.incidentalMethods.size()) >= 0.8 * this.totalMethods.size();
        return rule1 && rule2;
    }
    
    protected boolean checkForDegenerate() {
        final boolean rule1 = (double)this.emptyMethods.size() >= 0.3333333333333333 * this.totalMethods.size();
        final boolean rule2 = (double)(this.getMethods.size() + this.setMethods.size() + this.emptyMethods.size()) >= 0.8 * this.totalMethods.size();
        return rule1 && rule2;
    }
    
    protected boolean checkForDataClass() {
        final boolean rule1 = (double)(this.getMethods.size() + this.setMethods.size()) > 0.0;
        final boolean rule2 = this.totalMethods.size() == this.getMethods.size() + this.setMethods.size();
        final boolean rule3 = this.collaborationalMethods.isEmpty();
        return rule1 && rule2 && rule3;
    }
    
    protected boolean checkForPool() {
        double staticFinalFields = 0.0;
        FieldDeclaration[] fields;
        for (int length = (fields = this.type.getFields()).length, i = 0; i < length; ++i) {
            final FieldDeclaration field = fields[i];
            if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                ++staticFinalFields;
            }
        }
        final boolean rule1 = staticFinalFields >= 0.6666666666666666 * staticFinalFields && this.totalMethods.size() < 2;
        final boolean rule2 = staticFinalFields > 2.0;
        return rule1 && rule2;
    }
}
