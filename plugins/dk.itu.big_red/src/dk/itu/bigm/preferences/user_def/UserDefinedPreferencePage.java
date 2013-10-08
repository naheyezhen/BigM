package dk.itu.bigm.preferences.user_def;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import dk.itu.bigm.application.plugin.BigMPlugin;

import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUserDef;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUserDefExisted;

public class UserDefinedPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {
	
	private HashMap<String, String> beforeAction = new HashMap<String, String>();
	
	private Table keyTable;
	private Button selectAll;
	private Button deselectAll;
	private Button newUserDefConfig;
	public static String keyStr = null;
	public static String desStr = null;
	
	public UserDefinedPreferencePage() {
		setPreferenceStore(BigMPlugin.getInstance().getPreferenceStore());
		setDescription("User-difined configurations for Models");
	}
		
	@Override
	protected Control createContents(Composite parent) {
		keyTable = new Table(parent, SWT.MAX|SWT.BORDER|SWT.FULL_SELECTION|SWT.CHECK);
		keyTable.setHeaderVisible(true);
		keyTable.setLinesVisible(true);
		TableLayout tbLayout = new TableLayout();
		keyTable.setLayout(tbLayout);
		keyTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//set table header
		tbLayout.addColumnData(new ColumnWeightData(10));
		TableColumn id = new TableColumn(keyTable, SWT.NONE);
		id.setText("");
		tbLayout.addColumnData(new ColumnWeightData(60));
		TableColumn key = new TableColumn(keyTable, SWT.NONE);
		key.setText("Key");
		tbLayout.addColumnData(new ColumnWeightData(80));
		TableColumn des = new TableColumn(keyTable, SWT.NONE);
		des.setText("Description");
		
		Iterator<String> iterator = keySetUserDefExisted.keySet().iterator();
		//set table content with built-in key set configuration	
		while (iterator.hasNext()) {
			String entryKey = iterator.next();
			String value = keySetUserDefExisted.get(entryKey);
			final TableItem tbItem = new TableItem(keyTable, 0);
			if(keySetUserDef.containsKey(entryKey)){
				tbItem.setChecked(true);
			}
			tbItem.setText(new String[]{"", entryKey, value});	
		}
		
		Iterator<String> it = keySetUserDef.keySet().iterator();
		beforeAction.clear();
		while(it.hasNext()){
			String keyStr = it.next();
			String desStr = keySetUserDef.get(keyStr);
			beforeAction.put(keyStr, desStr);
		}
		
		Composite bottomComp = new Composite(parent, SWT.NONE);
		bottomComp.setLayout(new GridLayout(4, true));
		bottomComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		newUserDefConfig = new Button(bottomComp, SWT.NONE);
		newUserDefConfig.setText("    New    ");
		
		newUserDefConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					NewUserDefConfigDialog newDialog = new NewUserDefConfigDialog(getShell());
					newDialog.open();
					if(!(keyStr == null || keyStr.equals("") 
							||desStr == null || desStr.equals(""))){
						if(keySetUserDefExisted.containsKey(keyStr)){
							MessageDialog.openWarning(getShell(), "Duplicate configuration Warning", "the configuration to be created hava existed already");
							keyStr = null;
							desStr = null;
						}
						else{
							keySetUserDefExisted.put(keyStr, desStr);
							final TableItem tbItem = new TableItem(keyTable, 0);
							tbItem.setText(new String[]{"", keyStr, desStr});
						}
					}
				}				
			});
		
		selectAll = new Button(bottomComp, SWT.NONE);
		selectAll.setText("Select All");

		selectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				beforeAction.clear();
				for(TableItem item : keyTable.getItems()){
					item.setChecked(true);
					String key = item.getText(1).toString();
					String des = item.getText(2).toString();
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
		keySetUserDef.clear();
		for(TableItem item : keyTable.getItems()){
			if(item.getChecked()){
				String key = item.getText(1);
				String des = item.getText(2);
				beforeAction.put(key, des);
			}
		}
		keySetUserDef = beforeAction;
	}

	@Override
	public boolean performOk() {
		performApply();
		return true;
	}
	
	@Override
	public boolean performCancel() {
		return true;
	}

}