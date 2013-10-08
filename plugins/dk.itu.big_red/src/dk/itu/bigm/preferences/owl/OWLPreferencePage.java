package dk.itu.bigm.preferences.owl;

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

import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetClass;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetInd;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetPro;

public class OWLPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {
	
	public static List<String[]> keySetOWL = new ArrayList<String[]>();
	private Table keyTable;
	
	public OWLPreferencePage() {
		setPreferenceStore(BigMPlugin.getInstance().getPreferenceStore());
		setDescription("Built-in OWL Configuration for Models");
	}
		
	@Override
	protected Control createContents(Composite parent) {
		CommonFuncUtilities.refreshPrefsContent(new Object[]{keySetClass, keySetInd, keySetPro}, 
				new String[]{"OWLClass_", "OWLIndividual_", "OWLProperty_"}, keySetOWL);
		
		CommonFuncUtilities.drawTableWithoutCheck(parent, keyTable, keySetOWL);
		return parent;
	}
	
	@Override
	public void init(IWorkbench workbench) {
		/* do nothing */
	}

}