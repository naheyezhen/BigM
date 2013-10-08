package dk.itu.bigm.preferences.swrl;

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

import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetBool;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetCmp;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetDTD;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetList;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetMath;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetString;

public class SWRLPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {
	
	public static List<String[]> keySetSWRL = new ArrayList<String[]>();
	private Table keyTable;
	
	public SWRLPreferencePage() {
		setPreferenceStore(BigMPlugin.getInstance().getPreferenceStore());
		setDescription("Built-in SWRL Configuration for Models");
	}
		
	@Override
	protected Control createContents(Composite parent) {
		CommonFuncUtilities.refreshPrefsContent(new Object[]{keySetBool, keySetString, keySetList, keySetCmp, keySetMath, keySetDTD}, 
				new String[]{"SWRLBool_", "SWRLString_", "SWRLList_", "SWRLCmp_", "SWRLMath_", "SWRLDTD_"}, keySetSWRL);
		
		CommonFuncUtilities.drawTableWithoutCheck(parent, keyTable, keySetSWRL);
		return parent;
	}
	
	@Override
	public void init(IWorkbench workbench) {
		/* do nothing */
	}

}