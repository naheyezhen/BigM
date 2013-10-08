package dk.itu.bigm.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.utilities.ui.UI;

public class BigMPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	public BigMPreferencePage() {
		super(FLAT);
		setPreferenceStore(BigMPlugin.getInstance().getPreferenceStore());
		setDescription("Miscellaneous preferences for BigM.");
	}
	
	@Override
	public void createFieldEditors() {
		addField(
			new ListEditor(
				BigMPreferences.PREFERENCE_EXTERNAL_TOOLS,
				"User-defined external tools",
				getFieldEditorParent()) {
			@Override
			protected String[] parseString(String stringList) {
				return BigMPreferences.splitString(stringList);
			}
			
			@Override
			protected String getNewInputObject() {
				return UI.promptFor("External command",
						"Define an external command.", null, null);
			}
			
			@Override
			protected String createList(String[] items) {
				return BigMPreferences.joinString(items);
			}
		});
	}

	@Override
	public void init(IWorkbench workbench) {
		/* do nothing */
	}
	
}