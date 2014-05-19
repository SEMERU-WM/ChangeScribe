package co.edu.unal.colswe.changescribe.core.textgenerator.phrase;

import java.util.*;

public class Parameter {
	private String typeName;
	private String variableName;
	private static Set<String> primitives;

	static {
		(Parameter.primitives = new HashSet<String>()).add("byte");
		Parameter.primitives.add("short");
		Parameter.primitives.add("int");
		Parameter.primitives.add("long");
		Parameter.primitives.add("float");
		Parameter.primitives.add("double");
		Parameter.primitives.add("char");
		Parameter.primitives.add("String");
		Parameter.primitives.add("boolean");
		Parameter.primitives.add("Object");
		Parameter.primitives.add("Long");
	}

	public Parameter(final String typeName, final String variableName) {
		super();
		this.typeName = typeName;
		this.variableName = variableName;
	}

	public String getTypeName() {
		return this.typeName;
	}

	public String getVariableName() {
		return this.variableName;
	}

	public boolean isPrimitive() {
		final int index = this.typeName.indexOf("[");
		if (index != -1) {
			return Parameter.primitives.contains(this.typeName.substring(0,
					index));
		}
		return Parameter.primitives.contains(this.typeName);
	}
}
