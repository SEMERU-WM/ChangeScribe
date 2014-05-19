package co.edu.unal.colswe.changescribe.core;

import java.util.Vector;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

@SuppressWarnings("restriction")
public class ProjectInformation {
	private int totalIUnits;
	private int totalTypes;
	private int totalMethods;
	private Vector<Integer> methodsCounter;
	private double methodsMean;
	private double methodsStdDev;
	private IJavaProject project;

	public ProjectInformation(final IJavaProject project) {
		super();
		this.totalIUnits = 0;
		this.totalTypes = 0;
		this.totalMethods = 0;
		this.methodsCounter = new Vector<Integer>();
		this.project = project;
	}

	public void compute() {
		try {
			IPackageFragment[] packageFragments;
			for (int length = (packageFragments = this.project
					.getPackageFragments()).length, j = 0; j < length; ++j) {
				final IPackageFragment element = packageFragments[j];
				ICompilationUnit[] compilationUnits;
				for (int length2 = (compilationUnits = element
						.getCompilationUnits()).length, k = 0; k < length2; ++k) {
					final ICompilationUnit junit = compilationUnits[k];
					boolean typeCounted = false;
					IType[] allTypes;
					for (int length3 = (allTypes = junit.getAllTypes()).length, l = 0; l < length3; ++l) {
						final IType type = allTypes[l];
						if (type.isInterface() || type.isClass()) {
							if (!typeCounted) {
								++this.totalIUnits;
								typeCounted = true;
							}
							++this.totalTypes;
							this.methodsCounter.add(type.getMethods().length);
							this.totalMethods += type.getMethods().length;
						}
					}
				}
			}
			if (this.methodsCounter.size() != 0) {
				this.methodsMean = this.totalMethods
						/ this.methodsCounter.size();
				double sumOfSquareDifference = 0.0;
				for (final Integer i : this.methodsCounter) {
					sumOfSquareDifference += Math
							.pow(i - this.methodsMean, 2.0);
					this.methodsStdDev = Math.sqrt(sumOfSquareDifference
							/ this.methodsCounter.size());
				}
			} else {
				this.methodsMean = 0.0;
				this.methodsStdDev = 0.0;
			}
		} catch (JavaModelException ex) {
			System.err
					.println("Oops! An error occured when computing project information");
		}
	}

	public int getTotalUnits() {
		return this.totalIUnits;
	}

	public int getTotalTypes() {
		return this.totalTypes;
	}

	public Vector<Integer> getMethodsCounter() {
		return this.methodsCounter;
	}

	public double getMethodsMean() {
		return this.methodsMean;
	}

	public double getMethodsStdDev() {
		return this.methodsStdDev;
	}

	public String getName() {
		return this.project.getElementName();
	}
	
	public static String getAbsoluteURL(String path) {
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		if (!BundleUtility.isReady(bundle)) {
			return null;
		}
		String loc = bundle.getLocation();
		loc = loc.substring(loc.indexOf("file:"), loc.length()).concat(path);
		return loc;
	}
}
