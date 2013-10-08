package org.bigraph.bigmc.bigm.bgm;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class ImportWizardPage extends WizardPage {
	public ImportWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Import a BigMC file as a new BigM project.");
	}
	
	@Override
	public void createControl(Composite parent) {
		setControl(new Composite(parent, 0));
	}
}
