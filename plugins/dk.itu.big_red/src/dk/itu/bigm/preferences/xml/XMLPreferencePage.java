package dk.itu.bigm.preferences.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.utilities.CommonFuncUtilities;

import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetDer;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetPri;

public class XMLPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {
	
	public static List<String[]> keySetXML = new ArrayList<String[]>();
	private Table keyTable;
	
	public XMLPreferencePage() {
		setPreferenceStore(BigMPlugin.getInstance().getPreferenceStore());
		setDescription("XML Configuration in use for Models");
	}
		
	@Override
	protected Control createContents(Composite parent) {
		CommonFuncUtilities.refreshPrefsContent(new Object[]{keySetPri, keySetDer}, 
				new String[]{"XMLPrimitive_", "XMLDerived_"}, keySetXML);
		
		CommonFuncUtilities.drawTableWithoutCheck(parent, keyTable, keySetXML);
		return parent;
	}
	
	@Override
	public void init(IWorkbench workbench) {
		/* do nothing */
	}

}