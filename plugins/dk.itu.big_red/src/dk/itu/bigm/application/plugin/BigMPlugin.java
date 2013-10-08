package dk.itu.bigm.application.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import dk.itu.bigm.utilities.CommonFuncUtilities;
import dk.itu.bigm.utilities.ui.LinkSortPrefs;

import static dk.itu.bigm.preferences.owl.OWLPreferencePage.keySetOWL;
import static dk.itu.bigm.preferences.swrl.SWRLPreferencePage.keySetSWRL;
import static dk.itu.bigm.preferences.xml.XMLPreferencePage.keySetXML;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetInUse;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUML;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUserDef;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUserDefExisted;

/**
 * The BigMPlugin class is responsible for starting and stopping the BigM
 * plugin &mdash; and for keeping track of plugin-wide shared objects.
 */
public class BigMPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "dk.itu.bigm";
	private static BigMPlugin plugin;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		String prefXMLPath = plugin.getStateLocation().removeLastSegments(1) + "/org.eclipse.ui.workbench/linktype_prefs.xml";
		LinkSortPrefs.loadLinkSort(prefXMLPath);
		CommonFuncUtilities.refreshListContent(
				new Object[]{keySetUML, keySetOWL, keySetXML, keySetSWRL, keySetUserDef} ,keySetInUse);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	String prefXMLPath = plugin.getStateLocation().removeLastSegments(1) + "/org.eclipse.ui.workbench/linktype_prefs.xml";
	List<List<String>> linksortLists = CommonFuncUtilities.transformDataStructure(
			new Object[]{ keySetUML, keySetOWL, keySetXML, keySetSWRL, keySetUserDef},
			new String[] {"UML", "OWL", "XML", "SWRL", "UserDef"});
		LinkSortPrefs.buildLinkSortPrefsXMLDoc(linksortLists, keySetUserDefExisted, prefXMLPath);
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 * @return the shared instance
	 */
	public static BigMPlugin getInstance() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * Returns an {@link InputStream} for the given file in this plugin, if it
	 * exists.
	 * @param path a (plugin root-relative) path to a file
	 * @return an InputStream, or <code>null</code> if the file wasn't found
	 */
	public static InputStream getResource(String path) {
		try {
			URL u = FileLocator.find(
					getInstance().getBundle(), new Path(path), null);
			if (u != null)
				return u.openStream();
			else return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Returns a new {@link Status} object with a {@link Throwable} attached.
	 * <p>The severity of the new Status object is always {@link
	 * IStatus#ERROR}.
	 * @param t a {@link Throwable}
	 * @return a new {@link Status} object
	 */
	public static Status getThrowableStatus(Throwable t) {
		return new Status(IStatus.ERROR, PLUGIN_ID, t.getLocalizedMessage(), t);
	}
}
