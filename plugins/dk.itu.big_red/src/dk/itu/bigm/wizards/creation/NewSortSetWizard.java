package dk.itu.bigm.wizards.creation;

import org.bigraph.model.SortSet;
import org.bigraph.model.savers.SaveFailedException;
import org.bigraph.model.savers.SortSetXMLSaver;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import dk.itu.bigm.utilities.io.IOAdapter;
import dk.itu.bigm.utilities.resources.EclipseFileWrapper;
import dk.itu.bigm.utilities.resources.Project;
import dk.itu.bigm.utilities.resources.Project.ModificationRunner.Callback;
import dk.itu.bigm.utilities.ui.UI;

public class NewSortSetWizard extends Wizard implements INewWizard {
	private WizardNewFileCreationPage page = null;
	
	@Override
	public boolean performFinish() {
		IContainer c =
			Project.findContainerByPath(null, page.getContainerFullPath());
		if (c != null) {
			try {
				final IFile sorFile = c.getFile(new Path(page.getFileName()));
				IOAdapter io = new IOAdapter();
				SortSetXMLSaver r = new SortSetXMLSaver().setModel(new SortSet());
				
				r.setFile(new EclipseFileWrapper(sorFile)).setOutputStream(io.getOutputStream()).
					exportObject();
				Project.setContents(sorFile, io.getInputStream(),
						new Callback() {
					@Override
					public void onSuccess() {
						try {
							UI.openInEditor(sorFile);
						} catch (PartInitException pie) {
							/* ? */
							pie.printStackTrace();
						}
					}
				});
				
				return true;
			} catch (SaveFailedException e) {
				page.setErrorMessage(e.getLocalizedMessage());
			}
		}
		return false;
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		page = new WizardNewFileCreationPage("newSortSetWizardPage", selection);
		
		page.setTitle("SortSet");
		page.setDescription("Create a new sort set in an existing bigraphical reactive system.");
		page.setFileExtension("bigraph-sortset");
		
		addPage(page);
	}
}
