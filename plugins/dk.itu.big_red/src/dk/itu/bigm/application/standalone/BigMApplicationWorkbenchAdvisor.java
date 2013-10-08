package dk.itu.bigm.application.standalone;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class BigMApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	private static final String PERSPECTIVE_ID = "dk.itu.bigm.perspective";

    @Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new BigMApplicationWorkbenchWindowAdvisor(configurer);
    }
    
    @Override
	public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        org.eclipse.ui.ide.IDE.registerAdapters();
    }

	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	
	@Override
	public IAdaptable getDefaultPageInput() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot();
	}
}
