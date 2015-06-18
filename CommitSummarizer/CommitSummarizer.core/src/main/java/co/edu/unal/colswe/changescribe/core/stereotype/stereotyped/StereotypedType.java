package co.edu.unal.colswe.changescribe.core.stereotype.stereotyped;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.stereotype.analyzer.TypeAnalyzer;
import co.edu.unal.colswe.changescribe.core.stereotype.information.TypeInfo;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.CodeStereotype;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.TypeStereotype;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.TypeStereotypeRules;

public class StereotypedType extends TypeStereotypeRules implements StereotypedElement {
    private Set<IVariableBinding> fields;
    private TypeStereotype primaryStereotype;
    private TypeStereotype secondaryStereotype;
    private List<StereotypedMethod> stereotypedMethods;
    private List<StereotypedType> stereotypedSubTypes;
    private List<TypeInfo> relatedTypes;
    private StringBuilder report;
    private static Pattern TYPE_KEY_PATTERN;
    
    static {
        StereotypedType.TYPE_KEY_PATTERN = Pattern.compile("([A-Z])(([a-zA-Z_][a-zA-Z0-9_]+/)*)([a-zA-Z_][a-zA-Z0-9_]+)(~([a-zA-Z0-9_]+))?((\\$[a-zA-Z_][a-zA-Z0-9_]+)*)(\\$[0-9]+)?;");
    }
    
    public StereotypedType(final TypeDeclaration type, final double methodsMean, final double methodsStdDev) throws NullPointerException {
        super(type, methodsMean, methodsStdDev);
        this.fields = new HashSet<IVariableBinding>();
        this.stereotypedMethods = new ArrayList<StereotypedMethod>();
        this.stereotypedSubTypes = new ArrayList<StereotypedType>();
        this.relatedTypes = new LinkedList<TypeInfo>();
        this.report = new StringBuilder();
        try {
        	if(type.resolveBinding() != null) {
        		type.resolveBinding().getBinaryName();
        	}
        }
        catch (NullPointerException ex) {
            throw new NullPointerException("No type name found");
        }
    }
    
    public TypeDeclaration getElement() {
        return this.type;
    }
    
    public List<StereotypedElement> getStereoSubElements() {
        final List<StereotypedElement> elements = new ArrayList<StereotypedElement>(this.stereotypedMethods);
        elements.addAll(this.stereotypedSubTypes);
        return elements;
    }
    
    public Javadoc getJavadoc() {
        return this.type.getJavadoc();
    }
    
    public ChildPropertyDescriptor getJavadocDescriptor() {
        return TypeDeclaration.JAVADOC_PROPERTY;
    }
    
    public List<CodeStereotype> getStereotypes() {
        final ArrayList<CodeStereotype> stereotypes = new ArrayList<CodeStereotype>();
        if (this.primaryStereotype != null) {
            stereotypes.add(this.primaryStereotype);
        }
        if (this.secondaryStereotype != null) {
            stereotypes.add(this.secondaryStereotype);
        }
        return stereotypes;
    }
    
    public void findStereotypes() {
        FieldDeclaration[] fields;
        for (int length = (fields = this.type.getFields()).length, i = 0; i < length; ++i) {
            final FieldDeclaration field = fields[i];
            for (final Object o : field.fragments()) {
                final VariableDeclarationFragment fragment = (VariableDeclarationFragment)o;
                this.fields.add(fragment.resolveBinding());
            }
        }
        this.findMethodsStereotypes();
        this.findTypeStereotypes();
        this.completeReport();
        this.findSubtypesStereotypes();
    }
    
    private void findSubtypesStereotypes() {
        TypeDeclaration[] types;
        for (int length = (types = this.type.getTypes()).length, i = 0; i < length; ++i) {
            final TypeDeclaration subtype = types[i];
            if (subtype.resolveBinding().isClass()) {
                final StereotypedType subStereotypedType = new StereotypedType(subtype, this.methodsMean, this.methodsStdDev);
                subStereotypedType.findStereotypes();
                this.stereotypedSubTypes.add(subStereotypedType);
            }
        }
    }
    
    private void setStereotype(final TypeStereotype stereotype) {
        if (this.primaryStereotype == null) {
            this.primaryStereotype = stereotype;
        }
        else {
            this.secondaryStereotype = stereotype;
        }
    }
    
    private void findTypeStereotypes() {
        if (this.checkForInterface()) {
            this.setStereotype(TypeStereotype.INTERFACE);
            return;
        }
        if (this.checkForPool()) {
            this.setStereotype(TypeStereotype.POOL);
            return;
        }
        if (this.checkForDegenerate()) {
            this.setStereotype(TypeStereotype.DEGENERATE);
            return;
        }
        if (this.checkForLazyClass()) {
            this.setStereotype(TypeStereotype.LAZY_CLASS);
            return;
        }
        if (this.checkForLargeClass()) {
            this.setStereotype(TypeStereotype.LARGE_CLASS);
            return;
        }
        if (this.checkForDataClass()) {
            this.setStereotype(TypeStereotype.DATA_CLASS);
            return;
        }
        if (this.checkForMinimalEntity()) {
            this.setStereotype(TypeStereotype.MINIMAL_ENTITY);
            return;
        }
        if (this.checkForEntity()) {
            this.setStereotype(TypeStereotype.ENTITY);
            return;
        }
        if (this.checkForFactory()) {
            this.setStereotype(TypeStereotype.FACTORY);
            return;
        }
        if (this.checkForController()) {
            this.setStereotype(TypeStereotype.CONTROLLER);
            return;
        }
        if (this.checkForPureController()) {
            this.setStereotype(TypeStereotype.PURE_CONTROLLER);
            return;
        }
        if (this.checkForBoundary()) {
            this.setStereotype(TypeStereotype.BOUNDARY);
        }
        if (this.checkForDataProvider()) {
            this.setStereotype(TypeStereotype.DATA_PROVIDER);
            return;
        }
        if (this.checkForCommander()) {
            this.setStereotype(TypeStereotype.COMMANDER);
            return;
        }
    }
    
    private void findMethodsStereotypes() {
        final TypeAnalyzer analyzer = new TypeAnalyzer(this.type);
        this.stereotypedMethods = analyzer.getStereotypedMethods();
        this.report.append(analyzer.getReport());
        for (final StereotypedMethod stereotypedMethod : this.stereotypedMethods) {
            if (stereotypedMethod != null && stereotypedMethod.overridesObjectMethod() || stereotypedMethod.isConstructor() || stereotypedMethod.isAnonymous()) {
                continue;
            }
            this.addRelatedTypes(stereotypedMethod.getUsedTypes());
            this.addMethodToSet(stereotypedMethod);
            this.totalMethods.add(stereotypedMethod);
        }
    }
    
    private void addRelatedTypes(final List<TypeInfo> types) {
        for (final TypeInfo type : types) {
            if (this.relatedTypes.contains(type)) {
                this.relatedTypes.get(this.relatedTypes.indexOf(type)).incrementFrequencyBy(type.getFrequency());
            }
            else {
                this.relatedTypes.add(type);
            }
        }
    }
    
    private void addMethodToSet(final StereotypedMethod stereotypedMethod) {
        if (stereotypedMethod.isAccessor()) {
            this.accessorMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isMutator()) {
            this.mutatorMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isCollaborational()) {
            this.collaborationalMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isDegenerate()) {
            this.degenerateMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isGet()) {
            this.getMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isPredicate()) {
            this.predicateMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isProperty()) {
            this.propertyMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isVoidAccessor()) {
            this.voidAccessorMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isSet()) {
            this.setMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isCommand()) {
            this.commandMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isNonVoidCommand()) {
            this.nonVoidCommandMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isFactory()) {
            this.factoryMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isCollaborator()) {
            this.collaboratorMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isLocalController()) {
            this.localControllerMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isController()) {
            this.controllerMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isIncidental()) {
            this.incidentalMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isEmpty()) {
            this.emptyMethods.add(stereotypedMethod);
        }
        if (stereotypedMethod.isAbstract()) {
            this.abstractMethods.add(stereotypedMethod);
        }
    }
    
    public List<StereotypedMethod> getStereotypedMethods() {
        return this.stereotypedMethods;
    }
    
    public List<StereotypedType> getStereotypedSubTypes() {
        return this.stereotypedSubTypes;
    }
    
    private boolean typeIs(final TypeStereotype stereotype) {
        boolean result = false;
        if (this.primaryStereotype != null) {
            result = this.primaryStereotype.equals(stereotype);
        }
        if (this.secondaryStereotype != null) {
            result = (result || this.secondaryStereotype.equals(stereotype));
        }
        return result;
    }
    
    public boolean isInterface() {
        return this.typeIs(TypeStereotype.INTERFACE);
    }
    
    public boolean isEntity() {
        return this.typeIs(TypeStereotype.ENTITY);
    }
    
    public boolean isMinimalEntity() {
        return this.typeIs(TypeStereotype.MINIMAL_ENTITY);
    }
    
    public boolean isDataProvider() {
        return this.typeIs(TypeStereotype.DATA_PROVIDER);
    }
    
    public boolean isCommander() {
        return this.typeIs(TypeStereotype.COMMANDER);
    }
    
    public boolean isBoundary() {
        return this.typeIs(TypeStereotype.BOUNDARY);
    }
    
    public boolean isFactory() {
        return this.typeIs(TypeStereotype.FACTORY);
    }
    
    public boolean isController() {
        return this.typeIs(TypeStereotype.CONTROLLER);
    }
    
    public boolean isPureController() {
        return this.typeIs(TypeStereotype.PURE_CONTROLLER);
    }
    
    public boolean isLargeClass() {
        return this.typeIs(TypeStereotype.LARGE_CLASS);
    }
    
    public boolean isLazyClass() {
        return this.typeIs(TypeStereotype.LAZY_CLASS);
    }
    
    public boolean isDegenerate() {
        return this.typeIs(TypeStereotype.DEGENERATE);
    }
    
    public boolean isDataClass() {
        return this.typeIs(TypeStereotype.DATA_CLASS);
    }
    
    public boolean isPool() {
        return this.typeIs(TypeStereotype.POOL);
    }
    
    public String getReport() {
        return this.report.toString();
    }
    
    public List<TypeInfo> getRelatedTypes() {
        return this.relatedTypes;
    }
    
    public Set<IVariableBinding> getFields() {
        return this.fields;
    }
    
    private void completeReport() {
    }
    
    public String getName() {
        return (this.type != null && this.type.resolveBinding() != null) ? this.type.resolveBinding().getName() : Constants.EMPTY_STRING;
    }
    
    public String getQualifiedName() {
    	String qualifiedName = (this.type.getName() != null && this.type.resolveBinding() != null) ? this.type.resolveBinding().getQualifiedName() : Constants.EMPTY_STRING;
    	
    	if(null == qualifiedName || qualifiedName.isEmpty()) {
    		qualifiedName = type.getName().getFullyQualifiedName();
    	}
        return qualifiedName;
    }
    
    public String getFullyQualifiedName() {
    	StringBuilder fullyQualifiedClassName = new StringBuilder();
    	if(this.type.getParent() instanceof CompilationUnit) {
	    	fullyQualifiedClassName = new StringBuilder((((CompilationUnit) this.type.getParent())).getPackage().getName().getFullyQualifiedName());
	    	fullyQualifiedClassName.append(".");
	    	fullyQualifiedClassName.append(type.getName());
    	} else {
    		String name = (this.type != null && this.type.resolveBinding() != null) ? this.type.resolveBinding().getQualifiedName() : Constants.EMPTY_STRING;
    		fullyQualifiedClassName.append(name);
    	}
    	
        return fullyQualifiedClassName.toString();
    }
    
    public String getKey() {
        final StringBuilder parsedKey = new StringBuilder();
        if (this.type != null && this.type.resolveBinding() != null) {
            final ITypeBinding binding = this.type.resolveBinding();
            final String key = binding.getKey();
            final Matcher matcher = StereotypedType.TYPE_KEY_PATTERN.matcher(key);
            if (matcher.find()) {
                if (matcher.group(2) != null) {
                    parsedKey.append(matcher.group(2));
                }
                if (matcher.group(6) == null) {
                    if (matcher.group(4) != null) {
                        parsedKey.append(matcher.group(4));
                    }
                }
                else {
                    parsedKey.append(matcher.group(6));
                }
                if (matcher.group(7) != null) {
                    parsedKey.append(matcher.group(7));
                }
            }
        }
        return parsedKey.toString();
    }
}
