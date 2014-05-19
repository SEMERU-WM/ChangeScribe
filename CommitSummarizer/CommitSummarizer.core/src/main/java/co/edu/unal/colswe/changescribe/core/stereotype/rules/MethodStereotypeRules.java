package co.edu.unal.colswe.changescribe.core.stereotype.rules;

import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;

import co.edu.unal.colswe.changescribe.core.stereotype.analyzer.MethodAnalyzer;
import co.edu.unal.colswe.changescribe.core.stereotype.information.VariableInfo;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;

public class MethodStereotypeRules {
	protected MethodAnalyzer methodAnalyzer;

	protected MethodStereotype checkForAbstract() {
		if (!this.methodAnalyzer.hasBody()) {
			return MethodStereotype.ABSTRACT;
		}
		return null;
	}

	protected MethodStereotype checkForEmpty() {
		if (!this.methodAnalyzer.hasStatements()) {
			return MethodStereotype.EMPTY;
		}
		return null;
	}

	protected MethodStereotype checkForMutatorStereotype() {
		if (this.methodAnalyzer.getSetFields().isEmpty()) {
			return null;
		}
		if (!this.isVoid(this.methodAnalyzer.getReturnType())
				&& !this.isBoolean(this.methodAnalyzer.getReturnType())) {
			return MethodStereotype.NON_VOID_COMMAND;
		}
		if (this.methodAnalyzer.getSetFields().size() == 1) {
			return MethodStereotype.SET;
		}
		return MethodStereotype.COMMAND;
	}

	protected MethodStereotype checkForAccessorStereotype() {
		if (this.methodAnalyzer.getSetFields().isEmpty()) {
			if (!this.isVoid(this.methodAnalyzer.getReturnType())) {
				if (!this.methodAnalyzer.getGetFields().isEmpty()
						&& this.methodAnalyzer.getPropertyFields().isEmpty()) {
					return MethodStereotype.GET;
				}
				if (this.isBoolean(this.methodAnalyzer.getReturnType())) {
					if (!this.methodAnalyzer.getPropertyFields().isEmpty()) {
						return MethodStereotype.PREDICATE;
					}
				} else if (!this.methodAnalyzer.getPropertyFields().isEmpty()) {
					return MethodStereotype.PROPERTY;
				}
			} else if (!this.methodAnalyzer.getVoidAccessorFields().isEmpty()) {
				return MethodStereotype.VOID_ACCESSOR;
			}
		}
		return null;
	}

	protected MethodStereotype checkForCreationalStereotype() {
		if (this.methodAnalyzer.isConstructor()) {
			return MethodStereotype.CONSTRUCTOR;
		}
		if (this.methodAnalyzer.overridesClone()) {
			return MethodStereotype.COPY_CONSTRUCTOR;
		}
		if (this.methodAnalyzer.overridesFinalize()) {
			return MethodStereotype.DESTRUCTOR;
		}
		if (this.methodAnalyzer.isInstantiatedReturn()) {
			return MethodStereotype.FACTORY;
		}
		return null;
	}

	protected MethodStereotype checkForCollaborationalStereotype(
			final boolean asPrimaryStereotype) {
		boolean allPrimitiveParameters = true;
		boolean allPrimitiveVariables = true;
		int returnedFieldVariables = 0;
		int modifiedObjectParameters = 0;
		for (final VariableInfo parameter : this.methodAnalyzer.getParameters()) {
			if (parameter.getVariableBinding() != null && !this.isPrimitive(parameter.getVariableBinding())) {
				allPrimitiveParameters = false;
			}
			if (parameter.isReturned()
					&& !parameter.getAssignedFields().isEmpty()) {
				++returnedFieldVariables;
			}
			if (!parameter.isModified()
					|| this.isPrimitive(parameter.getVariableBinding())) {
				continue;
			}
			++modifiedObjectParameters;
		}
		for (final VariableInfo variable : this.methodAnalyzer.getVariables()) {
			if (variable.getVariableBinding() != null && !this.isPrimitive(variable.getVariableBinding())) {
				allPrimitiveVariables = false;
			}
			if (!variable.isReturned() || variable.getAssignedFields().isEmpty()) {
				continue;
			}
			++returnedFieldVariables;
		}
		if (asPrimaryStereotype) {
			if ((!this.methodAnalyzer.getParameters().isEmpty() && !allPrimitiveParameters)
					|| (!this.methodAnalyzer.getVariables().isEmpty() && !allPrimitiveVariables)) {
				return MethodStereotype.COLLABORATOR;
			}
		} else if (((!this.methodAnalyzer.getParameters().isEmpty() && modifiedObjectParameters > 0) || (!this.methodAnalyzer
				.getVariables().isEmpty() && !allPrimitiveVariables))
				&& this.methodAnalyzer.getParameters().size()
						+ this.methodAnalyzer.getVariables().size() > returnedFieldVariables) {
			return MethodStereotype.COLLABORATOR;
		}
		if (!this.methodAnalyzer.getInvokedLocalMethods().isEmpty()
				&& !this.methodAnalyzer.getInvokedExternalMethods().isEmpty()) {
			return MethodStereotype.COLLABORATOR;
		}
		if (!this.methodAnalyzer.getInvokedExternalMethods().isEmpty()
				&& this.methodAnalyzer.usesFields()) {
			return MethodStereotype.COLLABORATOR;
		}
		if (allPrimitiveParameters && allPrimitiveVariables) {
			if (!this.methodAnalyzer.getInvokedLocalMethods().isEmpty()
					&& this.methodAnalyzer.getInvokedExternalMethods()
							.isEmpty()) {
				return MethodStereotype.LOCAL_CONTROLLER;
			}
			if (!this.methodAnalyzer.getInvokedExternalMethods().isEmpty()
					&& this.methodAnalyzer.getInvokedLocalMethods().isEmpty()
					&& !this.methodAnalyzer.usesFields()
					&& this.methodAnalyzer.getGetFields().isEmpty()
					&& this.methodAnalyzer.getPropertyFields().isEmpty()
					&& this.methodAnalyzer.getSetFields().isEmpty()) {
				return MethodStereotype.CONTROLLER;
			}
		}
		return null;
	}

	private boolean isVoid(final Type type) {
		if (type.isPrimitiveType()) {
			final PrimitiveType primitive = (PrimitiveType) type;
			if (primitive.getPrimitiveTypeCode().equals(
					(Object) PrimitiveType.VOID)) {
				return true;
			}
		}
		return false;
	}

	private boolean isBoolean(final Type type) {
		if (type.isPrimitiveType()) {
			final PrimitiveType primitive = (PrimitiveType) type;
			if (primitive.getPrimitiveTypeCode().equals(
					(Object) PrimitiveType.BOOLEAN)) {
				return true;
			}
		}
		return false;
	}

	private boolean isPrimitive(final IVariableBinding binding) {
		return binding.getType().isPrimitive();
	}
}
