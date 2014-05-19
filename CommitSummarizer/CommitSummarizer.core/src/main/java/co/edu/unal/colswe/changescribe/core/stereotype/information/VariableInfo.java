package co.edu.unal.colswe.changescribe.core.stereotype.information;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.IVariableBinding;

public class VariableInfo {
	private IVariableBinding variableBinding;
	private Set<IVariableBinding> assignedFields;
	private boolean isInstantiated;
	private boolean isReturned;
	private boolean isModified;

	public VariableInfo(final IVariableBinding name) {
		super();
		this.variableBinding = name;
		this.isInstantiated = false;
		this.isReturned = false;
		this.isModified = false;
		this.assignedFields = new HashSet<IVariableBinding>();
	}

	public VariableInfo(final IVariableBinding name,
			final boolean isInstantiated) {
		super();
		this.variableBinding = name;
		this.isInstantiated = isInstantiated;
		this.isReturned = false;
		this.isModified = false;
		this.assignedFields = new HashSet<IVariableBinding>();
	}

	public IVariableBinding getVariableBinding() {
		return this.variableBinding;
	}

	public Set<IVariableBinding> getAssignedFields() {
		return this.assignedFields;
	}

	public void addAssignedField(final IVariableBinding field) {
		this.assignedFields.add(field);
	}

	public void setInstantiated(final boolean isInstantiated) {
		this.isInstantiated = isInstantiated;
	}

	public void setReturned(final boolean isReturned) {
		this.isReturned = isReturned;
	}

	public void setModified(final boolean isModified) {
		this.isModified = isModified;
	}

	public boolean isInstantiated() {
		return this.isInstantiated;
	}

	public boolean isReturned() {
		return this.isReturned;
	}

	public boolean isModified() {
		return this.isModified;
	}

	public int hashCode() {
		int result = 1;
		result = 31
				* result
				+ ((this.variableBinding == null) ? 0 : this.variableBinding
						.hashCode());
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
		final VariableInfo other = (VariableInfo) obj;
		if (this.variableBinding == null) {
			if (other.variableBinding != null) {
				return false;
			}
		} else if (!this.variableBinding.equals((Object) other.variableBinding)) {
			return false;
		}
		return true;
	}
}
