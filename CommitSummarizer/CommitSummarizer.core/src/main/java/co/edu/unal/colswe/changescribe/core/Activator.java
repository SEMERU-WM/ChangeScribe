package co.edu.unal.colswe.changescribe.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import co.edu.unal.colswe.changescribe.core.textgenerator.pos.POSTagger;
import co.edu.unal.colswe.changescribe.core.ui.FilesChangedListDialog;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ChangeScribe.core";

	// The shared instance
	private static Activator plugin;
	
	boolean started;
	
	private FilesChangedListDialog filesChangedListDialog;
	/**
	 * The constructor
	 */
	public Activator() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin  = this;
		started = true;
		POSTagger.init();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		started = false;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public FilesChangedListDialog getFilesChangedListDialog() {
		return filesChangedListDialog;
	}

	public void setFilesChangedListDialog(FilesChangedListDialog filesChangedListDialog) {
		this.filesChangedListDialog = filesChangedListDialog;
	}
}
