package dk.itu.bigm.preferences.user_def;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class NewUserDefConfigDialog extends Dialog{
	private Text key;
	private Text des;
	private String keyStr;
	private String desStr;

	public NewUserDefConfigDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite topComp = new Composite(parent, SWT.NONE);
		topComp.setLayout(new RowLayout());
		new Label(topComp, SWT.NONE).setText("Key: ");
		key = new Text(topComp, SWT.BORDER);
		key.setLayoutData(new RowData(100, -1));
		new Label(topComp, SWT.NONE).setText("Description: ");
		des = new Text(topComp, SWT.BORDER);
		des.setLayoutData(new RowData(100, -1));
		return topComp;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID){
			keyStr = key.getText();
			desStr = des.getText();
			if(keyStr == null || keyStr.equals("") 
					|| desStr == null || desStr.equals("")){
				MessageDialog.openWarning(null, "Empty Input Waring", "Key or Description can't be empty");
			}
			transferNewConfiguration(keyStr, desStr);
		}
		super.buttonPressed(buttonId);
	}
	
	public void transferNewConfiguration(String key, String des){
		UserDefinedPreferencePage.keyStr = key;
		UserDefinedPreferencePage.desStr = des;
	}
}
