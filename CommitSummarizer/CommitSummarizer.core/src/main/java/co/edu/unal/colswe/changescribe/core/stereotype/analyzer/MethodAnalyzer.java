package co.edu.unal.colswe.changescribe.core.stereotype.analyzer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import co.edu.unal.colswe.changescribe.core.stereotype.information.TypeInfo;
import co.edu.unal.colswe.changescribe.core.stereotype.information.VariableInfo;

public class MethodAnalyzer {
    private boolean isConstructor;
    private boolean isAnonymous;
    private boolean hasBody;
    private boolean hasStatements;
    private boolean usesNonStaticFinalFields;
    private boolean overridesClone;
    private boolean overridesFinalize;
    private boolean overridesToString;
    private boolean overridesHashCode;
    private boolean overridesEquals;
    private Type returnType;
    private ITypeBinding declaringClass;
    private Set<IVariableBinding> getFields;
    private Set<IVariableBinding> propertyFields;
    private Set<IVariableBinding> voidAccessorFields;
    private Set<IVariableBinding> setFields;
    private List<VariableInfo> parameters;
    private List<VariableInfo> variables;
    private boolean instantiatedReturn;
    private Set<IMethodBinding> invokedLocalMethods;
    private Set<IMethodBinding> invokedExternalMethods;
    private List<TypeInfo> usedTypes;
    
    public MethodAnalyzer(final MethodDeclaration method) {
        super();
        this.isConstructor = false;
        this.isAnonymous = false;
        this.hasBody = false;
        this.hasStatements = false;
        this.usesNonStaticFinalFields = false;
        this.overridesClone = false;
        this.overridesFinalize = false;
        this.overridesToString = false;
        this.overridesHashCode = false;
        this.overridesEquals = false;
        this.getFields = new HashSet<IVariableBinding>();
        this.propertyFields = new HashSet<IVariableBinding>();
        this.voidAccessorFields = new HashSet<IVariableBinding>();
        this.setFields = new HashSet<IVariableBinding>();
        this.parameters = new LinkedList<VariableInfo>();
        this.variables = new LinkedList<VariableInfo>();
        this.instantiatedReturn = false;
        this.invokedLocalMethods = new HashSet<IMethodBinding>();
        this.invokedExternalMethods = new HashSet<IMethodBinding>();
        this.usedTypes = new LinkedList<TypeInfo>();
        method.accept((ASTVisitor)new MethodVisitor(method));
    }
    
    public boolean isConstructor() {
        return this.isConstructor;
    }
    
    public boolean isAnonymous() {
        return this.isAnonymous;
    }
    
    public boolean hasBody() {
        return this.hasBody;
    }
    
    public boolean hasStatements() {
        return this.hasStatements;
    }
    
    public boolean usesFields() {
        return this.usesNonStaticFinalFields;
    }
    
    public boolean overridesClone() {
        return this.overridesClone;
    }
    
    public boolean overridesFinalize() {
        return this.overridesFinalize;
    }
    
    public boolean overridesToString() {
        return this.overridesToString;
    }
    
    public boolean overridesHashCode() {
        return this.overridesHashCode;
    }
    
    public boolean overridesEquals() {
        return this.overridesEquals;
    }
    
    public Type getReturnType() {
        return this.returnType;
    }
    
    public ITypeBinding getDeclaringClass() {
        return this.declaringClass;
    }
    
    public Set<IVariableBinding> getGetFields() {
        return this.getFields;
    }
    
    public Set<IVariableBinding> getPropertyFields() {
        return this.propertyFields;
    }
    
    public Set<IVariableBinding> getVoidAccessorFields() {
        return this.voidAccessorFields;
    }
    
    public Set<IVariableBinding> getSetFields() {
        return this.setFields;
    }
    
    public List<VariableInfo> getParameters() {
        return this.parameters;
    }
    
    public List<VariableInfo> getVariables() {
        return this.variables;
    }
    
    public boolean isInstantiatedReturn() {
        return this.instantiatedReturn;
    }
    
    public Set<IMethodBinding> getInvokedLocalMethods() {
        return this.invokedLocalMethods;
    }
    
    public Set<IMethodBinding> getInvokedExternalMethods() {
        return this.invokedExternalMethods;
    }
    
    public List<TypeInfo> getUsedTypes() {
        return this.usedTypes;
    }
    
    private void checkNonFinalStaticFieldUse(final IVariableBinding field) {
        if (field != null && (!Modifier.isFinal(field.getModifiers()) || !Modifier.isStatic(field.getModifiers()))) {
            this.usesNonStaticFinalFields = true;
        }
    }
    
    static /* synthetic */ void access$0(final MethodAnalyzer methodAnalyzer, final ITypeBinding declaringClass) {
        methodAnalyzer.declaringClass = declaringClass;
    }
    
    static /* synthetic */ void access$2(final MethodAnalyzer methodAnalyzer, final boolean isAnonymous) {
        methodAnalyzer.isAnonymous = isAnonymous;
    }
    
    static /* synthetic */ void access$3(final MethodAnalyzer methodAnalyzer, final boolean hasBody) {
        methodAnalyzer.hasBody = hasBody;
    }
    
    static /* synthetic */ void access$4(final MethodAnalyzer methodAnalyzer, final boolean hasStatements) {
        methodAnalyzer.hasStatements = hasStatements;
    }
    
    static /* synthetic */ void access$5(final MethodAnalyzer methodAnalyzer, final boolean isConstructor) {
        methodAnalyzer.isConstructor = isConstructor;
    }
    
    static /* synthetic */ void access$6(final MethodAnalyzer methodAnalyzer, final Type returnType) {
        methodAnalyzer.returnType = returnType;
    }
    
    static /* synthetic */ void access$7(final MethodAnalyzer methodAnalyzer, final boolean overridesClone) {
        methodAnalyzer.overridesClone = overridesClone;
    }
    
    static /* synthetic */ void access$8(final MethodAnalyzer methodAnalyzer, final boolean overridesFinalize) {
    	methodAnalyzer.overridesFinalize = overridesFinalize;
    }
    
    static /* synthetic */ void access$9(final MethodAnalyzer methodAnalyzer, final boolean overridesToString) {
        methodAnalyzer.overridesToString = overridesToString;
    }
    
    static /* synthetic */ void access$10(final MethodAnalyzer methodAnalyzer, final boolean overridesEquals) {
        methodAnalyzer.overridesEquals = overridesEquals;
    }
    
    static /* synthetic */ void access$11(final MethodAnalyzer methodAnalyzer, final boolean overridesHashCode) {
        methodAnalyzer.overridesHashCode = overridesHashCode;
    }
    
    static /* synthetic */ void access$15(final MethodAnalyzer methodAnalyzer, final boolean instantiatedReturn) {
        methodAnalyzer.instantiatedReturn = instantiatedReturn;
    }
    
    private class MethodVisitor extends ASTVisitor
    {
        private int inReturn;
        private int inAssignmentRightSide;
        private int inCondition;
        private int inMethodArguments;
        private int assignedVariableIndex;
        private int assignedParameterIndex;
       
        
        public MethodVisitor(final MethodDeclaration node) {
            super();
            this.inReturn = 0;
            this.inAssignmentRightSide = 0;
            this.inCondition = 0;
            this.inMethodArguments = 0;
            this.assignedVariableIndex = -1;
            this.assignedParameterIndex = -1;
            if(node.resolveBinding() != null) {
            	MethodAnalyzer.access$0(MethodAnalyzer.this, node.resolveBinding().getDeclaringClass());
            	MethodAnalyzer.access$2(MethodAnalyzer.this, MethodAnalyzer.this.declaringClass.isAnonymous());
            }
            
            
            if (node.getBody() != null) {
                MethodAnalyzer.access$3(MethodAnalyzer.this, true);
                if (node.getBody().statements().size() > 0) {
                    MethodAnalyzer.access$4(MethodAnalyzer.this, true);
                }
            }
            MethodAnalyzer.access$5(MethodAnalyzer.this, node.isConstructor());
            MethodAnalyzer.access$6(MethodAnalyzer.this, node.getReturnType2());
            if(node.resolveBinding() != null) {
            	MethodAnalyzer.access$7(MethodAnalyzer.this, node.resolveBinding().getKey().contains(";.clone()Ljava/lang/Object;"));
                MethodAnalyzer.access$8(MethodAnalyzer.this, node.resolveBinding().getKey().contains(";.finalize()V"));
                MethodAnalyzer.access$9(MethodAnalyzer.this, node.resolveBinding().getKey().contains(";.toString()Ljava/lang/String;"));
                MethodAnalyzer.access$10(MethodAnalyzer.this, node.resolveBinding().getKey().contains(";.equals(Ljava/lang/Object;)Z"));
                MethodAnalyzer.access$11(MethodAnalyzer.this, node.resolveBinding().getKey().contains(";.hashCode()I"));
            }
            
            for (final Object o : node.parameters()) {
                final SingleVariableDeclaration parameter = (SingleVariableDeclaration)o;
                MethodAnalyzer.this.parameters.add(new VariableInfo(parameter.resolveBinding()));
            }
        }
        
        public boolean visit(final VariableDeclarationFragment node) {
            boolean isInstantiated = false;
            if (node.getInitializer() != null) {
                isInstantiated = (node.getInitializer() instanceof ClassInstanceCreation);
            }
            MethodAnalyzer.this.variables.add(new VariableInfo(node.resolveBinding(), isInstantiated));
            return super.visit(node);
        }
        
        public boolean visit(final ConditionalExpression node) {
            ++this.inCondition;
            node.getExpression().accept((ASTVisitor)this);
            --this.inCondition;
            node.getThenExpression().accept((ASTVisitor)this);
            node.getElseExpression().accept((ASTVisitor)this);
            return false;
        }
        
        public boolean visit(final ClassInstanceCreation node) {
            if (this.inReturn > 0 && this.inMethodArguments == 0 && this.inCondition == 0 && MethodAnalyzer.this.returnType != null && !MethodAnalyzer.this.returnType.isPrimitiveType()) {
                MethodAnalyzer.access$15(MethodAnalyzer.this, true);
            }
            return super.visit(node);
        }
        
        public boolean visit(final ReturnStatement node) {
            ++this.inReturn;
            final IVariableBinding field = this.getLocalField(node.getExpression());
            if (field != null) {
                MethodAnalyzer.this.getFields.add(field);
            } else {
                int index = this.getVariableIndex(node.getExpression());
                if (index != -1 && MethodAnalyzer.this.variables.get(index).isInstantiated() && !MethodAnalyzer.this.returnType.isPrimitiveType()) {
                    MethodAnalyzer.access$15(MethodAnalyzer.this, true);
                    MethodAnalyzer.this.variables.get(index).setReturned(true);
                } else if (index != -1) {
                    if (!MethodAnalyzer.this.variables.get(index).getAssignedFields().isEmpty()) {
                        for (final IVariableBinding var : MethodAnalyzer.this.variables.get(index).getAssignedFields()) {
                            MethodAnalyzer.this.propertyFields.add(var);
                        }
                    }
                    MethodAnalyzer.this.variables.get(index).setReturned(true);
                } else {
                    index = this.getParameterIndex(node.getExpression());
                    if (index != -1) {
                        for (final IVariableBinding par : MethodAnalyzer.this.parameters.get(index).getAssignedFields()) {
                            MethodAnalyzer.this.propertyFields.add(par);
                        }
                        MethodAnalyzer.this.parameters.get(index).setReturned(true);
                    }
                }
            }
            return super.visit(node);
        }
        
        public void endVisit(final ReturnStatement node) {
            --this.inReturn;
            super.endVisit(node);
        }
        
        public boolean visit(final Assignment node) {
            final IVariableBinding field = this.getLocalField(node.getLeftHandSide());
            if (field != null) {
                MethodAnalyzer.this.setFields.add(field);
            }
            this.assignedVariableIndex = this.getVariableIndex(node.getLeftHandSide());
            if (this.assignedVariableIndex != -1) {
                MethodAnalyzer.this.variables.get(this.assignedVariableIndex).setModified(true);
            }
            this.assignedParameterIndex = this.getParameterIndex(node.getLeftHandSide());
            if (this.assignedParameterIndex != -1) {
                MethodAnalyzer.this.parameters.get(this.assignedParameterIndex).setModified(true);
            }
            node.getLeftHandSide().accept((ASTVisitor)this);
            ++this.inAssignmentRightSide;
            node.getRightHandSide().accept((ASTVisitor)this);
            --this.inAssignmentRightSide;
            return false;
        }
        
        public void endVisit(final Assignment node) {
            this.assignedVariableIndex = -1;
            this.assignedParameterIndex = -1;
            super.endVisit(node);
        }
        
        public boolean visit(final MethodInvocation node) {
            if (this.inReturn > 0) {
                final IVariableBinding field = this.getLocalField(node.getExpression());
                if (field != null) {
                    MethodAnalyzer.this.propertyFields.add(field);
                }
            }
            if (this.isLocalMethod(node)) {
                MethodAnalyzer.this.invokedLocalMethods.add(node.resolveMethodBinding());
            }
            if (node.getExpression() instanceof SimpleName) {
                final SimpleName name = (SimpleName)node.getExpression();
                if (name.resolveBinding() instanceof ITypeBinding) {
                    MethodAnalyzer.this.invokedExternalMethods.add(node.resolveMethodBinding());
                }
            }
            if (node.getExpression() != null) {
                node.getExpression().accept((ASTVisitor)this);
            }
            node.getName().accept((ASTVisitor)this);
            ++this.inMethodArguments;
            for (final Object o : node.arguments()) {
                ((Expression)o).accept((ASTVisitor)this);
            }
            --this.inMethodArguments;
            return false;
        }
        
        public boolean visit(final ExpressionStatement node) {
            if (node.getExpression() instanceof MethodInvocation) {
                final MethodInvocation method = (MethodInvocation)node.getExpression();
                Expression expression;
                for (expression = method.getExpression(); expression instanceof MethodInvocation; expression = ((MethodInvocation)expression).getExpression()) {}
                final IVariableBinding field = this.getLocalField(expression);
                if (field != null) {
                    MethodAnalyzer.this.setFields.add(field);
                }
                int index = this.getVariableIndex(method.getExpression());
                if (index != -1) {
                    MethodAnalyzer.this.variables.get(index).setModified(true);
                } else {
                    index = this.getParameterIndex(method.getExpression());
                    if (index != -1) {
                        MethodAnalyzer.this.parameters.get(index).setModified(true);
                    }
                }
                if (this.isLocalMethod(method)) {
                    MethodAnalyzer.this.invokedLocalMethods.add(method.resolveMethodBinding());
                }
            }
            return super.visit(node);
        }
        
        public boolean visit(final SimpleName node) {
            final IVariableBinding field = this.getLocalField((Expression)node);
            MethodAnalyzer.this.checkNonFinalStaticFieldUse(field);
            if (field != null) {
                if (this.inAssignmentRightSide > 0 && this.assignedVariableIndex != -1) {
                    MethodAnalyzer.this.variables.get(this.assignedVariableIndex).addAssignedField(field);
                    return super.visit(node);
                }
                if (this.inAssignmentRightSide > 0 && this.assignedParameterIndex != -1 && !MethodAnalyzer.this.parameters.get(this.assignedParameterIndex).getVariableBinding().getType().isPrimitive()) {
                    MethodAnalyzer.this.parameters.get(this.assignedParameterIndex).addAssignedField(field);
                    MethodAnalyzer.this.voidAccessorFields.add(field);
                    super.visit(node);
                }
                if (this.inReturn > 0 && !(node.getParent() instanceof ReturnStatement) && this.inMethodArguments == 0) {
                    MethodAnalyzer.this.propertyFields.add((IVariableBinding)node.resolveBinding());
                    return super.visit(node);
                }
            }
            if (node.resolveBinding() instanceof ITypeBinding) {
                final ITypeBinding typeBinding = (ITypeBinding)node.resolveBinding();
                if (!typeBinding.isPrimitive() && !typeBinding.isParameterizedType()) {
                    this.addUsedType(typeBinding);
                }
            }
            return super.visit(node);
        }
        
        private void addUsedType(final ITypeBinding typeBinding) {
            final TypeInfo type = new TypeInfo(typeBinding);
            if (MethodAnalyzer.this.usedTypes.contains(type)) {
                MethodAnalyzer.this.usedTypes.get(MethodAnalyzer.this.usedTypes.indexOf(type)).incrementFrequency();
            } else {
                MethodAnalyzer.this.usedTypes.add(type);
            }
        }
        
        public boolean visit(final FieldAccess node) {
            MethodAnalyzer.this.checkNonFinalStaticFieldUse(this.getLocalField((Expression)node));
            return super.visit(node);
        }
        
        public boolean visit(final SuperFieldAccess node) {
            MethodAnalyzer.this.checkNonFinalStaticFieldUse(this.getLocalField((Expression)node));
            return super.visit(node);
        }
        
        public boolean visit(final QualifiedName node) {
            MethodAnalyzer.this.checkNonFinalStaticFieldUse(this.getLocalField((Expression)node));
            return super.visit(node);
        }
        
        public boolean visit(final ArrayAccess node) {
            MethodAnalyzer.this.checkNonFinalStaticFieldUse(this.getLocalField((Expression)node));
            return super.visit(node);
        }
        
        private IVariableBinding getLocalField(final Expression expression) {
            IVariableBinding variableBinding = null;
            if (expression instanceof FieldAccess) {
                variableBinding = ((FieldAccess)expression).resolveFieldBinding();
            }
            if (expression instanceof SimpleName) {
                final SimpleName simpleName = (SimpleName)expression;
                if (simpleName.resolveBinding() instanceof IVariableBinding) {
                    variableBinding = (IVariableBinding)simpleName.resolveBinding();
                }
            }
            if (variableBinding != null && variableBinding.isField() && this.isLocalField(variableBinding, MethodAnalyzer.this.declaringClass)) {
                return variableBinding;
            }
            if (expression instanceof SuperFieldAccess) {
                final SuperFieldAccess superFieldAccess = (SuperFieldAccess)expression;
                final IVariableBinding nameBinding = this.getLocalField((Expression)superFieldAccess.getName());
                if (nameBinding != null) {
                    return nameBinding;
                }
                return this.getLocalField((Expression)superFieldAccess.getQualifier());
            } else {
                if (expression instanceof ArrayAccess) {
                    final ArrayAccess arrayAccess = (ArrayAccess)expression;
                    return this.getLocalField(arrayAccess.getArray());
                }
                if (!(expression instanceof QualifiedName)) {
                    return null;
                }
                final QualifiedName qualifiedName = (QualifiedName)expression;
                final IVariableBinding nameBinding = this.getLocalField((Expression)qualifiedName.getName());
                if (nameBinding != null) {
                    return nameBinding;
                }
                return this.getLocalField((Expression)qualifiedName.getQualifier());
            }
        }
        
        private boolean isLocalField(final IVariableBinding field, final ITypeBinding relatedType) {
            if (field.getDeclaringClass() != null) {
                if (field.getDeclaringClass().equals((Object)relatedType)) {
                    return true;
                }
                if (relatedType != null && relatedType.isNested() && !Modifier.isStatic(relatedType.getModifiers()) && this.isLocalField(field, relatedType.getDeclaringClass())) {
                    return true;
                }
                if (relatedType != null && relatedType.getSuperclass() != null && this.isLocalField(field, relatedType.getSuperclass())) {
                    return true;
                }
                ITypeBinding[] interfaces;
                if(relatedType != null) {
	                for (int length = (interfaces = relatedType.getInterfaces()).length, i = 0; i < length; ++i) {
	                    final ITypeBinding interfaceType = interfaces[i];
	                    if (this.isLocalField(field, interfaceType)) {
	                        return true;
	                    }
	                }
                }
            }
            return false;
        }
        
        private int getVariableIndex(final Expression expression) {
            if (expression instanceof SimpleName) {
                final SimpleName simpleName = (SimpleName)expression;
                if (simpleName.resolveBinding() instanceof IVariableBinding) {
                    final VariableInfo var = new VariableInfo((IVariableBinding)simpleName.resolveBinding());
                    if (MethodAnalyzer.this.variables.contains(var)) {
                        return MethodAnalyzer.this.variables.indexOf(var);
                    }
                }
            }
            if (!(expression instanceof QualifiedName)) {
                return -1;
            }
            final QualifiedName qualifiedName = (QualifiedName)expression;
            final int index = this.getVariableIndex((Expression)qualifiedName.getName());
            if (index != -1) {
                return index;
            }
            return this.getVariableIndex((Expression)qualifiedName.getQualifier());
        }
        
        private int getParameterIndex(final Expression expression) {
            if (expression instanceof SimpleName) {
                final SimpleName simpleName = (SimpleName)expression;
                if (simpleName.resolveBinding() instanceof IVariableBinding) {
                    final VariableInfo var = new VariableInfo((IVariableBinding)simpleName.resolveBinding());
                    if (MethodAnalyzer.this.parameters.contains(var)) {
                        return MethodAnalyzer.this.parameters.indexOf(var);
                    }
                }
            }
            if (!(expression instanceof QualifiedName)) {
                return -1;
            }
            final QualifiedName qualifiedName = (QualifiedName)expression;
            final int index = this.getParameterIndex((Expression)qualifiedName.getName());
            if (index != -1) {
                return index;
            }
            return this.getParameterIndex((Expression)qualifiedName.getQualifier());
        }
        
        private boolean isLocalMethod(final MethodInvocation method) {
            final IMethodBinding methodBinding = method.resolveMethodBinding();
            if (methodBinding != null && methodBinding.getDeclaringClass() != null) {
                if (methodBinding.getDeclaringClass().equals((Object)MethodAnalyzer.this.declaringClass)) {
                    return true;
                }
                ITypeBinding relatedClass = MethodAnalyzer.this.declaringClass;
                while (relatedClass != null && relatedClass.isNested() && !Modifier.isStatic(relatedClass.getModifiers())) {
                    relatedClass = relatedClass.getDeclaringClass();
                    if (relatedClass == null) {
                        break;
                    }
                    if (methodBinding.getDeclaringClass().equals((Object)relatedClass)) {
                        return true;
                    }
                }
                if(MethodAnalyzer.this.declaringClass != null) {
                	for (relatedClass = MethodAnalyzer.this.declaringClass.getSuperclass(); relatedClass != null; relatedClass = relatedClass.getSuperclass()) {
                        if (methodBinding.getDeclaringClass().equals((Object)relatedClass)) {
                            return true;
                        }
                    }
                }
                
            }
            return false;
        }
    }
}
