package dk.itu.bigm.preferences.swrl;

import java.util.HashMap;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.model.LinkSortKey;
import dk.itu.bigm.utilities.CommonFuncUtilities;

import static dk.itu.bigm.preferences.swrl.SWRLPreferencePage.keySetSWRL;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetBool;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetCmp;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetDTD;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetList;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetMath;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetString;

public class SWRLStringPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	private HashMap<String, String> beforeAction = new HashMap<String, String>();
	
	private Table keyTable;
	private Button selectAll;
	private Button deselectAll;
	
	public SWRLStringPreferencePage() {
		setPreferenceStore(BigMPlugin.getInstance().getPreferenceStore());
		setDescription("Built-in configuration for SWRL Strings");
	}
		
	@Override
	protected Control createContents(Composite parent) {
		keyTable = new Table(parent, SWT.MULTI|SWT.BORDER|SWT.FULL_SELECTION | SWT.CHECK);
		CommonFuncUtilities.drawTableWithCheck(parent, keyTable, LinkSortKey.keysForSWRLString, keySetString);
		
		CommonFuncUtilities.copyHashMapToHashMap(keySetString, beforeAction);
		
		Composite bottomComp = new Composite(parent, SWT.NONE);
		bottomComp.setLayout(new GridLayout(4, true));
		bottomComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectAll = new Button(bottomComp, SWT.NONE);
		selectAll.setText("Select All");
		selectAll.setEnabled(true);
		selectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				beforeAction.clear();
				for(TableItem item : keyTable.getItems()){
					item.setChecked(true);
					String key = item.getText(1).toString();
					String des = LinkSortKey.keysForSWRLString.get(key);
					beforeAction.put(key, des);	
				}
			}
		});
		
		deselectAll = new Button(bottomComp, SWT.NONE);
		deselectAll.setText("Deselect All");
		deselectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(TableItem item : keyTable.getItems()){
					item.setChecked(false);
				}		
				beforeAction.clear();
			}				
		});
		return parent;
	}
	
	@Override
	public void init(IWorkbench workbench) {
		/* do nothing */
	}
	
	@Override
	protected void performDefaults() {
		for(TableItem item : keyTable.getItems()){
			item.setChecked(false);
		}		
		beforeAction.clear();
	}
	
	@Override
	protected void performApply() {
		beforeAction.clear();
		keySetString.clear();
		for(TableItem item : keyTable.getItems()){
			if(item.getChecked()){
				String key = item.getText(1);
				String des = item.getText(2);
				beforeAction.put(key, des);
			}
		}
		keySetString = beforeAction;
		CommonFuncUtilities.refreshPrefsContent(new Object[]{keySetBool, keySetString, keySetList, keySetCmp, keySetMath, keySetDTD}, 
				new String[]{"SWRLBool_", "SWRLString_", "SWRLList_", "SWRLCmp_", "SWRLMath_", "SWRLDTD_"}, keySetSWRL);
	}

	@Override
	public boolean performOk() {
		performApply();
		return true;
	}
	
	@Override
	public boolean performCancel() {
		beforeAction.clear();
		return true;
	}

}