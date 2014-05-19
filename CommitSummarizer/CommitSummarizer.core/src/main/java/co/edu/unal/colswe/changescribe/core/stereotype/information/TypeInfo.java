package co.edu.unal.colswe.changescribe.core.stereotype.information;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class TypeInfo {
	private ITypeBinding typeBinding;
	private int frequency;

	public TypeInfo(final ITypeBinding type) {
		super();
		this.typeBinding = type;
		this.frequency = 1;
	}

	public ITypeBinding getTypeBinding() {
		return this.typeBinding;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void incrementFrequency() {
		++this.frequency;
	}

	public void incrementFrequencyBy(final int x) {
		this.frequency += x;
	}

	public int hashCode() {
		int result = 1;
		result = 31
				* result
				+ ((this.typeBinding == null) ? 0 : this.typeBinding.hashCode());
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
		final TypeInfo other = (TypeInfo) obj;
		if (this.typeBinding == null) {
			if (other.typeBinding != null) {
				return false;
			}
		} else if (!this.typeBinding.equals((Object) other.typeBinding)) {
			return false;
		}
		return true;
	}

	public static class TypeInformationComparator implements Comparator<TypeInfo> {
		public int compare(final TypeInfo o1, final TypeInfo o2) {
			final Integer freq1 = o1.getFrequency();
			final Integer freq2 = o2.getFrequency();
			return freq2.compareTo(freq1);
		}
	}
}
