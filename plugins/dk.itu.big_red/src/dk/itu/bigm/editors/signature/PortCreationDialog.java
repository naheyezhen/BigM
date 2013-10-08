package dk.itu.bigm.editors.signature;

import static dk.itu.bigm.preferences.owl.OWLPreferencePage.keySetOWL;
import static dk.itu.bigm.preferences.swrl.SWRLPreferencePage.keySetSWRL;
import static dk.itu.bigm.preferences.xml.XMLPreferencePage.keySetXML;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetInUse;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUML;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUserDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bigraph.model.LinkSort;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import dk.itu.bigm.utilities.CommonFuncUtilities;

public class PortCreationDialog extends Dialog{
	
	private Text name = null;
	private String portName = null;
	private Combo linkSortOnPort = null;
	private String portSort = "none";
	private Combo roleOnPort = null;
	private String portRole = "none";
	
	private String portSortWithRole = null;
	
	private org.bigraph.model.Control parentControl;

	protected PortCreationDialog(Shell shell, org.bigraph.model.Control model) {
		super(shell);
		parentControl = model;
	}
	
	protected void configureShell(Shell newShell){
		super.configureShell(newShell);
		newShell.setText("Add a new port");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Control createDialogArea(Composite parent) {
		CommonFuncUtilities.refreshListContent(new Object[]{keySetUML, keySetOWL, keySetXML, keySetSWRL, keySetUserDef} ,keySetInUse);
		Composite topComp = new Composite(parent, 0);
		topComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout leftLayout = new GridLayout(4, false);
		topComp.setLayout(leftLayout);
		
		new Label(topComp, SWT.NONE).setText("name: ");
		name = new Text(topComp, SWT.BORDER);
		
		new Label(topComp, SWT.NONE);
		new Label(topComp, SWT.NONE);
		
		new Label(topComp, SWT.NONE).setText("sort: ");
		linkSortOnPort = new Combo(topComp, SWT.NONE);
		linkSortOnPort.add("<none>");
		
		String pcSort = parentControl.getPlaceSort();
		if(pcSort.startsWith("UML")){
			initialLinkSortOnPort(linkSortOnPort, keySetUML, "UML_");
			initialLinkSortOnPort(linkSortOnPort, keySetUserDef, "");
		}
		else if(pcSort.startsWith("OWL")){
			initialLinkSortOnPort(linkSortOnPort, keySetOWL, "");
			initialLinkSortOnPort(linkSortOnPort, keySetSWRL, "");
			initialLinkSortOnPort(linkSortOnPort, keySetUserDef, "");
		}
		else if(pcSort.startsWith("XML")){
			initialLinkSortOnPort(linkSortOnPort, keySetXML, "");
			initialLinkSortOnPort(linkSortOnPort, keySetUserDef, "");
		}
		else if(pcSort.startsWith("UserDef")){
			List<LinkSort> linkSorts = (List<LinkSort>) parentControl.getSignature().getFormRules().getSortSet().getLinkSorts();
			ArrayList<String> keySetUserDefLinkSorts = new ArrayList<String>();
			for(LinkSort ls : linkSorts){
				keySetUserDefLinkSorts.add(ls.getName());
			}
			initialLinkSortOnPort(linkSortOnPort, keySetUserDefLinkSorts);
		}
		
		linkSortOnPort.select(0);

		linkSortOnPort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portSort = linkSortOnPort.getItem(linkSortOnPort.getSelectionIndex());
				if("<none>".equals(portSort))
					portSort = "none";
				portSortWithRole = portSort + ":" + portRole;
			}
		});
			
		new Label(topComp, SWT.NONE).setText("role: ");
		roleOnPort = new Combo(topComp, SWT.NONE);
		roleOnPort.add("<none>");
		roleOnPort.add("start");
		roleOnPort.add("end");
		roleOnPort.select(0);
		
		roleOnPort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portRole = roleOnPort.getItem(roleOnPort.getSelectionIndex());
				if("<none>".equals(portRole))
					portRole = "none";
				portSortWithRole = portSort + ":" + portRole;
			}
		});
		
		return parent;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID){
			portName = name.getText();
			if(portName == null || portName.equals("") ){
				MessageDialog.openWarning(null, "Empty Port Name Waring", "Port Name can't be empty");
			}
			else if(linkSortOnPort.getSelectionIndex() == -1){
				MessageDialog.openWarning(null, "Empty Port Sort Waring", "Port Sort can't be empty");
			}
			else if(roleOnPort.getSelectionIndex() == -1){
				MessageDialog.openWarning(null, "Empty Port Role Waring", "Port Role can't be empty");
			}
			else{
				SignatureEditorPolygonCanvas.portName = portName;
				SignatureEditorPolygonCanvas.portSort = portSortWithRole;
			}
		}
		super.buttonPressed(buttonId);
	}
	
	@SuppressWarnings("unchecked")
	public void initialLinkSortOnPort(Combo c, Object keys, String category){
		if(keys instanceof ArrayList){
			Iterator<String[]> it = ((ArrayList<String[]>) keys).iterator();
			while(it.hasNext()){
				String key = it.next()[0];
				c.add(category + key);
			}
		}
		else if(keys instanceof HashMap){
			Iterator<String> it = ((HashMap<String, String>) keys).keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				c.add(category + key);
			}			
		}
	}
	
	public void initialLinkSortOnPort(Combo c, ArrayList<String> keys){
		for(String key : keys){
			c.add(key);
		}
	}
	
}
