package dk.itu.bigm.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.utilities.CommonFuncUtilities;

import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetBool;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetClass;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetCmp;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetDTD;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetDer;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetInUse;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetInd;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetList;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetMath;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetPri;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetPro;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetString;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUML;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUserDef;

public class LinkSortConfigurationPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	private Table keyTable;
	
	public LinkSortConfigurationPreferencePage() {
		setPreferenceStore(BigMPlugin.getInstance().getPreferenceStore());
		setDescription("Configuration in use for Models");
	}
		
	@Override
	protected Control createContents(Composite parent) {
		
		//set table content with built-in key set configuration
		keySetInUse.clear();
		CommonFuncUtilities.refreshPrefsContent(new Object[]{keySetUML, keySetClass, keySetInd, keySetPro, 
				keySetBool, keySetString, keySetList, keySetCmp, keySetMath, keySetDTD, keySetPri, keySetDer, keySetUserDef},  
				new String[]{"UML_", "OWLClass_", "OWLIndividual_", "OWLProperty_", "SWRLBool_", "SWRLString_", "SWRLList_",
					"SWRLCmp_", "SWRLMath_", "SWRLDTD_", "XMLPrimitive_", "SWRLDerived_", "UserDef_"}, keySetInUse);
		
		CommonFuncUtilities.drawTableWithoutCheck(parent, keyTable, keySetInUse);
		return parent;
	}
	
	@Override
	public void init(IWorkbench workbench) {
		/* do nothing */
	}

}