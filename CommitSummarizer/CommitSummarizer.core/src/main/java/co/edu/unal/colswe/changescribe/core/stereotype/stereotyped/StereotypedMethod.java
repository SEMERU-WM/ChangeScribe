package co.edu.unal.colswe.changescribe.core.stereotype.stereotyped;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.stereotype.analyzer.MethodAnalyzer;
import co.edu.unal.colswe.changescribe.core.stereotype.information.TypeInfo;
import co.edu.unal.colswe.changescribe.core.stereotype.rules.MethodStereotypeRules;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.CodeStereotype;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;

public class StereotypedMethod extends MethodStereotypeRules implements
		StereotypedElement {
	private static Pattern METHOD_KEY_PATTERN;
	private MethodDeclaration method;
	private MethodStereotype primaryStereotype;
	private MethodStereotype secondaryStereotype;
	private StringBuilder report;

	static {
		StereotypedMethod.METHOD_KEY_PATTERN = Pattern
				.compile("([A-Z])(([a-zA-Z_][a-zA-Z0-9_]+/)*)([a-zA-Z_][a-zA-Z0-9_]*)(~([a-zA-Z0-9_]+))?((\\$[a-zA-Z_][a-zA-Z0-9_]+)*)(\\$[0-9]+)?;\\.([a-zA-Z_][a-zA-Z0-9_]*)?(\\<.*\\>)?(\\(.*\\).*)");
	}

	public StereotypedMethod(final MethodDeclaration method) {
		super();
		this.method = method;
		this.report = new StringBuilder();
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

	public List<StereotypedElement> getStereoSubElements() {
		return new ArrayList<StereotypedElement>();
	}

	public MethodDeclaration getElement() {
		return this.method;
	}

	public Javadoc getJavadoc() {
		return this.method.getJavadoc();
	}

	public ChildPropertyDescriptor getJavadocDescriptor() {
		return MethodDeclaration.JAVADOC_PROPERTY;
	}

	public Set<IVariableBinding> getGetFields() {
		return this.methodAnalyzer.getGetFields();
	}

	public Set<IVariableBinding> getPropertyFields() {
		return this.methodAnalyzer.getPropertyFields();
	}

	public Set<IVariableBinding> getVoidAccessorFields() {
		return this.methodAnalyzer.getVoidAccessorFields();
	}

	public boolean isAnonymous() {
		return this.methodAnalyzer.isAnonymous();
	}

	public List<TypeInfo> getUsedTypes() {
		return this.methodAnalyzer.getUsedTypes();
	}

	public boolean overridesObjectMethod() {
		return this.methodAnalyzer.overridesClone()
				|| this.methodAnalyzer.overridesFinalize()
				|| this.methodAnalyzer.overridesToString()
				|| this.methodAnalyzer.overridesEquals()
				|| this.methodAnalyzer.overridesHashCode();
	}

	public void findStereotypes() {
		this.report.append(Constants.NEW_LINE + this.getKey());
		try { 
			this.methodAnalyzer = new MethodAnalyzer(this.method);
			this.primaryStereotype = this.findPrimaryStereotype();
			if (!this.primaryStereotype.getCategory().equals(MethodStereotype.Category.COLLABORATIONAL)
					&& !this.primaryStereotype.getCategory().equals(MethodStereotype.Category.DEGENERATE)) {
				this.secondaryStereotype = this.findSecondaryStereotype();
				if (this.secondaryStereotype != null) {
				}
			}
		} catch (NullPointerException ex) {
			this.primaryStereotype = MethodStereotype.INCIDENTAL;
			//TODO DELETE
			ex.printStackTrace();}
	}

	private MethodStereotype findPrimaryStereotype() {
		MethodStereotype stereotype = this.checkForAbstract();
		if (stereotype != null) {
			return stereotype;
		}
		stereotype = this.checkForCreationalStereotype();
		if (stereotype != null) {
			return stereotype;
		}
		stereotype = this.checkForEmpty();
		if (stereotype != null) {
			return stereotype;
		}
		stereotype = this.checkForMutatorStereotype();
		if (stereotype != null) {
			return stereotype;
		}
		stereotype = this.checkForAccessorStereotype();
		if (stereotype != null) {
			return stereotype;
		}
		stereotype = this.checkForCollaborationalStereotype(true);
		if (stereotype != null) {
			return stereotype;
		}
		return MethodStereotype.INCIDENTAL;
	}

	private MethodStereotype findSecondaryStereotype() {
		return this.checkForCollaborationalStereotype(false);
	}

	public String getReport() {
		return this.report.toString();
	}

	public boolean isGet() {
		return this.isInSubcategory(MethodStereotype.GET);
	}

	public boolean isPredicate() {
		return this.isInSubcategory(MethodStereotype.PREDICATE);
	}

	public boolean isProperty() {
		return this.isInSubcategory(MethodStereotype.PROPERTY);
	}

	public boolean isVoidAccessor() {
		return this.isInSubcategory(MethodStereotype.VOID_ACCESSOR);
	}

	public boolean isSet() {
		return this.isInSubcategory(MethodStereotype.SET);
	}

	public boolean isCommand() {
		return this.isInSubcategory(MethodStereotype.COMMAND);
	}

	public boolean isNonVoidCommand() {
		return this.isInSubcategory(MethodStereotype.NON_VOID_COMMAND);
	}

	public boolean isConstructor() {
		return this.isInSubcategory(MethodStereotype.CONSTRUCTOR);
	}

	public boolean isCopyConstructor() {
		return this.isInSubcategory(MethodStereotype.COPY_CONSTRUCTOR);
	}

	public boolean isDestructor() {
		return this.isInSubcategory(MethodStereotype.DESTRUCTOR);
	}

	public boolean isFactory() {
		return this.isInSubcategory(MethodStereotype.FACTORY);
	}

	public boolean isCollaborator() {
		return this.isInSubcategory(MethodStereotype.COLLABORATOR);
	}

	public boolean isLocalController() {
		return this.isInSubcategory(MethodStereotype.LOCAL_CONTROLLER);
	}

	public boolean isController() {
		return this.isInSubcategory(MethodStereotype.CONTROLLER);
	}

	public boolean isIncidental() {
		return this.isInSubcategory(MethodStereotype.INCIDENTAL);
	}

	public boolean isEmpty() {
		return this.isInSubcategory(MethodStereotype.EMPTY);
	}

	public boolean isAbstract() {
		return this.isInSubcategory(MethodStereotype.ABSTRACT);
	}

	private boolean isInSubcategory(final MethodStereotype stereotype) {
		boolean result = false;
		if (this.primaryStereotype != null) {
			result = this.primaryStereotype.equals(stereotype);
		}
		if (this.secondaryStereotype != null) {
			result = (result || this.secondaryStereotype.equals(stereotype));
		}
		return result;
	}

	private boolean isInCategory(
			final MethodStereotype.Category stereotypeCategory) {
		boolean result = false;
		if (this.primaryStereotype != null) {
			result = this.primaryStereotype.getCategory().equals(
					stereotypeCategory);
		}
		if (this.secondaryStereotype != null) {
			result = (result || this.secondaryStereotype.getCategory().equals(
					stereotypeCategory));
		}
		return result;
	}

	public boolean isAccessor() {
		return this.isInCategory(MethodStereotype.Category.ACCESSOR);
	}

	public boolean isMutator() {
		return this.isInCategory(MethodStereotype.Category.MUTATOR);
	}

	public boolean isCreational() {
		return this.isInCategory(MethodStereotype.Category.CREATIONAL);
	}

	public boolean isCollaborational() {
		return this.isInCategory(MethodStereotype.Category.COLLABORATIONAL);
	}

	public boolean isDegenerate() {
		return this.isInCategory(MethodStereotype.Category.DEGENERATE);
	}

	public int hashCode() {
		int result = 1;
		result = 31 * result
				+ ((this.method == null) ? 0 : this.method.hashCode());
		result = 31
				* result
				+ ((this.primaryStereotype == null) ? 0
						: this.primaryStereotype.hashCode());
		result = 31
				* result
				+ ((this.secondaryStereotype == null) ? 0
						: this.secondaryStereotype.hashCode());
		return result;
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final StereotypedMethod other = (StereotypedMethod) obj;
		if (this.method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!this.method.equals((Object) other.method)) {
			return false;
		}
		return this.primaryStereotype == other.primaryStereotype
				&& this.secondaryStereotype == other.secondaryStereotype;
	}

	public String getName() {
		return (this.method != null && this.method.resolveBinding() != null) ? this.method
				.resolveBinding().getName() : Constants.EMPTY_STRING;
	}

	public String getQualifiedName() {
		final StringBuilder qName = new StringBuilder();
		if (this.method != null && this.method.resolveBinding() != null) {
			final IMethodBinding bind = this.method.resolveBinding();
			qName.append(bind.getDeclaringClass().getQualifiedName());
			qName.append(".");
			qName.append(bind.getName());
			qName.append("(");
			int nParam = bind.getParameterTypes().length;
			ITypeBinding[] parameterTypes;
			for (int length = (parameterTypes = bind.getParameterTypes()).length, i = 0; i < length; ++i) {
				final ITypeBinding param = parameterTypes[i];
				qName.append(param.getName().toString());
				if (nParam > 1) {
					qName.append(",");
					--nParam;
				}
			}
			qName.append(")");
		} else if (this.method != null) {
			qName.append(this.method.getName().getFullyQualifiedName());
		}
		return qName.toString();
	}
	
	public String getFullyQualifiedName() {
		final StringBuilder qName = new StringBuilder();
		if (this.method != null && this.method.resolveBinding() != null) {
			final IMethodBinding bind = this.method.resolveBinding();
			qName.append(bind.getDeclaringClass().getPackage().getName());
			qName.append(".");
			qName.append(bind.getDeclaringClass().getName());
			qName.append(".");
			qName.append(bind.getName());
			qName.append("(");
			int nParam = bind.getParameterTypes().length;
			ITypeBinding[] parameterTypes;
			for (int length = (parameterTypes = bind.getParameterTypes()).length, i = 0; i < length; ++i) {
				final ITypeBinding param = parameterTypes[i];
				qName.append(param.getName().toString());
				if (nParam > 1) {
					qName.append(",");
					--nParam;
				}
			}
			qName.append(")");
		} else if (this.method != null) {
			if(this.method.getParent() instanceof TypeDeclaration) { 
				String fullyQualifiedClassName = ((CompilationUnit)((TypeDeclaration) this.method.getParent()).getParent()).getPackage().getName().getFullyQualifiedName();
				qName.append(fullyQualifiedClassName);
				qName.append(".");
				qName.append(((TypeDeclaration) this.method.getParent()).getName());
				qName.append(".");
			}
			
			qName.append(this.method.getName().getFullyQualifiedName());
			qName.append("(");
			@SuppressWarnings("rawtypes")
			List parameters = this.method.parameters();
			int i = 0;
			for (Object object : parameters) {
				SingleVariableDeclaration param = (SingleVariableDeclaration) object;
				qName.append(param.getType().toString());
				if(i >= 0 && i < parameters.size() - 1) {
					qName.append(",");
				}
				i++;
			}
			qName.append(")");
			
		}
		return qName.toString();
	}

	public String getKey() {
		final StringBuilder parsedKey = new StringBuilder();
		if (this.method != null && this.method.resolveBinding() != null) {
			final IMethodBinding binding = this.method.resolveBinding();
			final String key = binding.getKey();
			final Matcher matcher = StereotypedMethod.METHOD_KEY_PATTERN
					.matcher(key);
			if (matcher.find()) {
				if (matcher.group(2) != null) {
					parsedKey.append(matcher.group(2));
				}
				if (matcher.group(6) == null) {
					if (matcher.group(4) != null) {
						parsedKey.append(matcher.group(4));
					}
				} else {
					parsedKey.append(matcher.group(6));
				}
				if (matcher.group(7) != null) {
					parsedKey.append(matcher.group(7));
				}
				if (binding.getDeclaringClass().isAnonymous()) {
					ITypeBinding declaringClass = binding.getDeclaringClass()
							.getDeclaringClass();
					final StringBuilder addClasses = new StringBuilder();
					while (declaringClass.isMember()) {
						addClasses.insert(0, "$" + declaringClass.getName());
						declaringClass = declaringClass.getDeclaringClass();
					}
					parsedKey.append(addClasses.toString());
				}
				// parsedKey.append(Reporter.SEPARATOR);
				if (matcher.group(10) != null) {
					parsedKey.append(matcher.group(10));
					if (matcher.group(11) != null) {
						parsedKey.append(matcher.group(11));
					}
				}
				// parsedKey.append(Reporter.SEPARATOR);
				if (matcher.group(12) != null) {
					final int index = matcher.group(12).indexOf("|");
					if (index != -1) {
						parsedKey.append(matcher.group(12).substring(0, index));
					} else {
						parsedKey.append(matcher.group(12));
					}
				}
			} else {
				parsedKey.append(key);
			}
		}
		return parsedKey.toString();
	}
}
