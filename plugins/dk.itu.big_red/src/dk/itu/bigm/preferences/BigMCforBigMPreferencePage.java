package dk.itu.bigm.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import dk.itu.bigm.application.plugin.BigMPlugin;

public class BigMCforBigMPreferencePage
	extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public static final String PREFERENCE_BIGMC_PATH = "pathOfBigMC";
	public static String pathOfBigMC = "";

	public BigMCforBigMPreferencePage() {
		setPreferenceStore(BigMPlugin.getInstance().getPreferenceStore());
		setDescription("BigMC for BigM");
	}

	@Override
	protected void createFieldEditors() {
		addField(new FileFieldEditor(
			PREFERENCE_BIGMC_PATH, "Path of BigMC",
				getFieldEditorParent()) {
			@Override
			protected boolean checkState() {
				pathOfBigMC = getTextControl().getText();
				return true;
			}
		});
	}

	@Override
	public void init(IWorkbench workbench) {
		/* do nothing */
	}
}
